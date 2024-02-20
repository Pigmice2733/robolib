// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.pid_subsystem;

import java.util.function.Supplier;

import com.pigmice.frc.lib.shuffleboard_helper.ShuffleboardHelper;
import com.pigmice.frc.lib.utils.Utils;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;

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

    private double encoderRotationConversion;

    private boolean useSoftwareStop;
    private double minAllowedPosition;
    private double maxAllowedPosition;

    private boolean useLimitSwitch;
    private DigitalInput limitSwitch;
    private double lsPosition;
    private boolean lsInverted;
    private LimitSwitchSide lsSide;

    private boolean useCustomEncoder;
    private Supplier<Double> getCustomEncoderPosition;
    private double encoderZeroPosition;

    private double targetRotation;
    private Constraints constraints;

    private boolean runPID = true;

    public double maxAllowedOutput = 1;

    public PIDSubsystemBase(CANSparkMax motor, double p, double i, double d, Constraints constraints,
            boolean motorInverted, double encoderRotationConversion, int currentLimit,
            ShuffleboardTab shuffleboardTab, boolean controllerTuningMode, boolean motorTestingMode) {

        this.shuffleboardTab = shuffleboardTab;
        this.constraints = constraints;
        this.encoderRotationConversion = encoderRotationConversion;

        motor.restoreFactoryDefaults();
        motor.getEncoder().setPosition(0);
        motor.setInverted(false);
        motor.getEncoder().setPositionConversionFactor(encoderRotationConversion);
        motor.setSmartCurrentLimit(currentLimit);
        motor.setIdleMode(IdleMode.kBrake);

        this.motor = motor;

        pidController = new ProfiledPIDController(p, i, d, constraints);

        ShuffleboardHelper
                .addOutput("Current Position", shuffleboardTab, () -> getCurrentRotation())
                .asDial(-180, 180);

        ShuffleboardHelper.addOutput("Target Position", shuffleboardTab, () -> targetRotation)
                .asDial(-180, 180);

        ShuffleboardHelper
                .addOutput("Setpoint", shuffleboardTab,
                        () -> pidController.getSetpoint().position)
                .asDial(-180, 180);

        ShuffleboardHelper.addOutput("Motor Output", shuffleboardTab,
                () -> motor.get());

        ShuffleboardHelper.addOutput("Motor Temp", shuffleboardTab,
                () -> motor.getMotorTemperature());

        if (motorTestingMode) {
            ShuffleboardHelper.addInput("Set Motor Output", shuffleboardTab,
                    (input) -> outputToMotor((double) input), 0);

            runPID = false;
        } else if (controllerTuningMode) {
            ShuffleboardHelper.addProfiledController("Rotation Controller", shuffleboardTab, pidController,
                    constraints.maxVelocity, constraints.maxAcceleration);

            ShuffleboardHelper.addInput("Target Pos Input", shuffleboardTab,
                    (input) -> setTargetRotation((double) input), 0);
        }
    }

    /**
     * Adds a software stop to prevent the mechanism from over rotating
     * 
     * @param minAllowedPosition the min allowed mechanism position
     * @param maxAllowedPosition the max allowed mechanism position
     */
    public void addSoftwareStop(double minAllowedPosition, double maxAllowedPosition) {
        this.useSoftwareStop = true;
        this.minAllowedPosition = minAllowedPosition;
        this.maxAllowedPosition = maxAllowedPosition;
    }

    /**
     * Adds a limit switch
     * 
     * @param lsPosition  the mechanism position the limit switch is located at
     * @param limitSwitch the limit switch
     * @param lsInverted  does the digitalInput return negative when the switch is
     *                    pressed?
     * @param lsSide      direction the mechanism is moving when it hits the switch
     */
    public void addLimitSwitch(double lsPosition, int limitSwitchPort, boolean lsInverted,
            LimitSwitchSide lsSide) {
        this.lsPosition = lsPosition;
        this.limitSwitch = new DigitalInput(limitSwitchPort);
        this.lsInverted = lsInverted;
        this.lsSide = lsSide;

        ShuffleboardHelper.addOutput("Limit Switch", shuffleboardTab, () -> limitSwitchPressed());
    }

    /**
     * Add an encoder other than the Neo's built in encoder
     * 
     * @param getEncoderPosition a supplier of the current encoder position, prior
     *                           to the conversion factor being applied
     */
    public void addCustomEncoder(Supplier<Double> getEncoderPosition) {
        this.useCustomEncoder = true;
        this.getCustomEncoderPosition = getEncoderPosition;
        this.encoderZeroPosition = getEncoderPosition.get();
    }

    /** Resets the controller to the turret's current rotation. */
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

        double calculatedOutput = pidController.calculate(getCurrentRotation(),
                targetRotation);
        outputToMotor(calculatedOutput);
    }

    /** Sets the percent output of the turret rotation motor. */
    public void outputToMotor(double percentOutput) {
        double currentRotation = getCurrentRotation();

        if (useSoftwareStop)
            percentOutput = Utils.applySoftwareStop(currentRotation, percentOutput,
                    minAllowedPosition,
                    maxAllowedPosition);

        // if (useLimitSwitch && limitSwitchPressed()) {
        // setEncoderPosition(lsPosition);
        // if (lsSide == LimitSwitchSide.POSITIVE)
        // percentOutput = Math.min(percentOutput, 0);
        // else
        // percentOutput = Math.max(percentOutput, 0);
        // }

        percentOutput = MathUtil.clamp(percentOutput, -maxAllowedOutput, maxAllowedOutput);
        motor.set(percentOutput);
    }

    /** Returns the turret's current rotation in degrees. */
    public double getCurrentRotation() {
        if (useCustomEncoder)
            return getCustomEncoderPosition.get() * encoderRotationConversion - encoderZeroPosition;
        else
            return (motor.getEncoder().getPosition());
    }

    /** Sets the turret's target position. */
    public void setTargetRotation(double targetDegrees) {
        if (useSoftwareStop)
            targetRotation = MathUtil.clamp(targetRotation, minAllowedPosition, maxAllowedPosition);

        targetRotation = targetDegrees;
    }

    /** Adjusts the turret's target position by the given amount. */
    public void changeTargetRotation(double delta) {
        setTargetRotation(targetRotation + delta);
    }

    /** Returns the current velocity of the turret in degrees per second. */
    public double getTurretVelocity() {
        return motor.getEncoder().getVelocity();
    }

    /** Returns the current rotation of the turret in degrees. */
    public double getTargetRotation() {
        return targetRotation;
    }

    /**
     * Sets the max velocity and acceleration of the turret as a Constraints object.
     */
    public void setPIDConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    public void setEncoderPosition(double position) {
        if (useCustomEncoder)
            encoderZeroPosition = getCustomEncoderPosition.get() - position;
        else
            motor.getEncoder().setPosition(position);
    }

    public void stopPID() {
        outputToMotor(0);
        runPID = false;
    }

    public void startPID() {
        runPID = true;
    }

    public void resetPID() {
        pidController.reset(getCurrentRotation());
        setTargetRotation(getCurrentRotation());
    }

    public boolean limitSwitchPressed() {
        return lsInverted ? !limitSwitch.get() : limitSwitch.get();
    }

    public CANSparkMax getMotor() {
        return motor;
    }

    public enum LimitSwitchSide {
        POSITIVE,
        NEGATIVE
    }
}
