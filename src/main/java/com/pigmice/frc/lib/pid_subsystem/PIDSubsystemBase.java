// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.pid_subsystem;

import java.util.function.DoubleSupplier;

import com.pigmice.frc.lib.shuffleboard_helper.ShuffleboardHelper;
import com.pigmice.frc.lib.utils.Utils;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkAbsoluteEncoder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PIDSubsystemBase extends SubsystemBase {
    private final ShuffleboardTab shuffleboardTab;

    private final CANSparkMax motor;
    private final ProfiledPIDController pidController;

    private boolean useSoftwareStop;
    private DoubleSupplier getMinAllowedPosition;
    private DoubleSupplier getMaxAllowedPosition;

    private boolean useLimitSwitch;
    private DigitalInput limitSwitch;
    private double lsPosition;
    private boolean lsInverted;
    private LimitSwitchSide lsSide;

    private boolean useThroughBoreEncoder;

    private double targetRotation;
    private Constraints constraints;

    private boolean runPID;

    private double maxAllowedOutput;

    public PIDSubsystemBase(CANSparkMax motor, double p, double i, double d, Constraints constraints,
            boolean motorInverted, double encoderRotationConversion, int currentLimit,
            ShuffleboardTab shuffleboardTab, boolean controllerTuningMode, boolean motorTestingMode) {

        this.shuffleboardTab = shuffleboardTab;
        this.constraints = constraints;

        motor.restoreFactoryDefaults();
        motor.getEncoder().setPosition(0);
        motor.setInverted(motorInverted);
        motor.getEncoder().setPositionConversionFactor(encoderRotationConversion);
        motor.setSmartCurrentLimit(currentLimit);
        motor.setIdleMode(IdleMode.kBrake);

        this.motor = motor;

        pidController = new ProfiledPIDController(p, i, d, constraints);

        runPID = true;
        maxAllowedOutput = 1;

        ShuffleboardHelper
                .addOutput("Current Position", shuffleboardTab, this::getCurrentRotation)
                .asDial(-180, 180).withPosition(0, 0);

        ShuffleboardHelper
                .addOutput("Setpoint", shuffleboardTab,
                        () -> pidController.getSetpoint().position)
                .asDial(-180, 180).withPosition(1, 0);

        ShuffleboardHelper.addOutput("Target Position", shuffleboardTab, () -> targetRotation)
                .asDial(-180, 180).withPosition(2, 0);

        ShuffleboardHelper.addOutput("Motor Output", shuffleboardTab,
                motor::get).withPosition(0, 1);

        ShuffleboardHelper.addOutput("Motor Temp", shuffleboardTab,
                motor::getMotorTemperature).withPosition(1, 1);

        ShuffleboardHelper.addOutput("At Max Output", shuffleboardTab,
                () -> (Math.abs(motor.get()) >= maxAllowedOutput))
                .withPosition(3, 0);

        if (motorTestingMode) {
            ShuffleboardHelper.addInput("Set Motor Output", shuffleboardTab,
                    (input) -> outputToMotor((double) input), 0).withPosition(2, 1);

            runPID = false;
        } else if (controllerTuningMode) {
            ShuffleboardHelper.addProfiledController("Rotation Controller", shuffleboardTab, pidController,
                    constraints.maxVelocity, constraints.maxAcceleration).withPosition(0, 2);

            ShuffleboardHelper.addInput("Target Pos Input", shuffleboardTab,
                    (input) -> setTargetRotation((double) input), 0).withPosition(1, 2);
        }
    }

    /**
     * Adds a software stop to prevent the mechanism from over-rotating.
     * 
     * @param minAllowedPosition the minimum allowed mechanism position
     * @param maxAllowedPosition the maximum allowed mechanism position
     */
    public void addSoftwareStop(double minAllowedPosition, double maxAllowedPosition) {
        addSoftwareStop(() -> minAllowedPosition, () -> maxAllowedPosition);
    }

    /**
     * Adds a software stop to prevent the mechanism from over-rotating.
     * 
     * @param minAllowedPosition the minimum allowed mechanism position
     * @param maxAllowedPosition the maximum allowed mechanism position
     */
    public void addSoftwareStop(DoubleSupplier getMinAllowedPosition, DoubleSupplier getMaxAllowedPosition) {
        this.useSoftwareStop = true;
        this.getMinAllowedPosition = getMinAllowedPosition;
        this.getMaxAllowedPosition = getMaxAllowedPosition;

        ShuffleboardHelper.addOutput("At Software Stop", shuffleboardTab,
                () -> (getCurrentRotation() > getMaxAllowedPosition.getAsDouble()
                        || getCurrentRotation() < getMinAllowedPosition.getAsDouble()))
                .withPosition(4, 0);
    }

    /**
     * Adds a limit switch with the given parameters.
     * 
     * @param position the mechanism position the limit switch is located at
     * @param port     the CAN ID of the limit switch
     * @param inverted does the digitalInput return negative when the switch is
     *                 pressed?
     * @param side     direction the mechanism is moving when it hits the switch
     */
    public void addLimitSwitch(double position, int port, boolean inverted, LimitSwitchSide side) {
        this.lsPosition = position;
        this.limitSwitch = new DigitalInput(port);
        this.lsInverted = inverted;
        this.lsSide = side;

        this.useLimitSwitch = true;

        ShuffleboardHelper.addOutput("Limit Switch", shuffleboardTab, () -> limitSwitchPressed()).withPosition(5, 0);
    }

    /**
     * Add an encoder other than the Neo's built-in encoder.
     */
    public void useThroughBoreEncoder() {
        this.useThroughBoreEncoder = true;
    }

    /** Sets the max allowed motor output. */
    public void setMaxAllowedOutput(double maxAllowedOutput) {
        this.maxAllowedOutput = maxAllowedOutput;
    }

    /** Resets the controller to the mechanism's current rotation. */
    public void resetRotationController() {
        double currentRotation = getCurrentRotation();
        targetRotation = currentRotation;
        pidController.reset(currentRotation);
    }

    @Override
    public void periodic() {
        updateClosedLoopControl();
    }

    /** Calculates and applies the next output from the PID controller. */
    private void updateClosedLoopControl() {
        if (!runPID)
            return;

        if (useLimitSwitch && limitSwitchPressed()) {
            setEncoderPosition(lsPosition);
        }

        double calculatedOutput = pidController.calculate(getCurrentRotation(),
                new State(targetRotation, 0), constraints);
        outputToMotor(calculatedOutput);
    }

    /** Sets the percent output of the rotation motor. */
    public void outputToMotor(double percentOutput) {
        double currentRotation = getCurrentRotation();

        if (useSoftwareStop)
            percentOutput = Utils.applySoftwareStop(currentRotation, percentOutput,
                    getMinAllowedPosition.getAsDouble(), getMaxAllowedPosition.getAsDouble());

        if (useLimitSwitch && limitSwitchPressed()) {
            if (lsSide == LimitSwitchSide.POSITIVE)
                percentOutput = Math.min(percentOutput, 0);
            else
                percentOutput = Math.max(percentOutput, 0);
        }

        percentOutput = MathUtil.clamp(percentOutput, -maxAllowedOutput, maxAllowedOutput);
        motor.set(percentOutput);
    }

    /** Returns the mechanism's current rotation in degrees. */
    public double getCurrentRotation() {
        if (useThroughBoreEncoder)
            return motor.getAbsoluteEncoder(SparkAbsoluteEncoder.Type.kDutyCycle).getPosition();
        else
            return (motor.getEncoder().getPosition());
    }

    /** Sets the mechanism's target position. */
    public void setTargetRotation(double targetDegrees) {
        if (useSoftwareStop)
            targetRotation = MathUtil.clamp(targetRotation, getMinAllowedPosition.getAsDouble(),
                    getMaxAllowedPosition.getAsDouble());
        targetRotation = targetDegrees;
    }

    /** Adjusts the mechanism's target position by the given amount. */
    public void changeTargetRotation(double delta) {
        setTargetRotation(targetRotation + delta);
    }

    /** Returns the current velocity of the mechanism in degrees per second. */
    public double getTurretVelocity() {
        return motor.getEncoder().getVelocity();
    }

    /** Returns the current rotation of the mechanism in degrees. */
    public double getTargetRotation() {
        return targetRotation;
    }

    /**
     * Sets the max velocity and acceleration of the mechanism as a Constraints
     * object.
     */
    public void setPIDConstraints(double velocity, double acceleration) {
        this.constraints = new Constraints(velocity, acceleration);
    }

    /** Sets the position of the encoder. */
    public void setEncoderPosition(double position) {
        if (useThroughBoreEncoder) {
            motor.getAbsoluteEncoder(SparkAbsoluteEncoder.Type.kDutyCycle)
                    .setZeroOffset(position - getCurrentRotation());
        } else {
            motor.getEncoder().setPosition(position);
        }
    }

    /** Pauses the PID loop, preventing it from outputting to the motor. */
    public void stopPID() {
        outputToMotor(0);
        runPID = false;
    }

    /** Starts/resumes the PID loop. */
    public void startPID() {
        runPID = true;
    }

    /** Completely resets the PID controller and encoder. */
    public void resetPID() {
        pidController.reset(getCurrentRotation());
        setTargetRotation(getCurrentRotation());
    }

    /** Returns true if the limit switch is currently pressed. */
    public boolean limitSwitchPressed() {
        return lsInverted ^ limitSwitch.get();
    }

    /** Returns the motor instance. */
    public CANSparkMax getMotor() {
        return motor;
    }

    public enum LimitSwitchSide {
        POSITIVE, NEGATIVE
    }
}