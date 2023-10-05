// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.drivetrain.differential;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DifferentialDrivetrain extends SubsystemBase {
    public final DifferentialConfig config;

    private final MotorControllerGroup leftGroup;
    private final MotorControllerGroup rightGroup;

    private final CANSparkMax leftDrive;
    private final CANSparkMax rightDrive;

    private final AHRS gyro = new AHRS();

    private final DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(new Rotation2d(), 0, 0,
            new Pose2d());

    private Pose2d pose = new Pose2d();

    public double outputFactor = 1;

    public DifferentialDrivetrain(DifferentialConfig config, boolean onlyUseDriveMotors) {
        this.config = config;

        leftDrive = new CANSparkMax(config.LEFT_DRIVE_PORT, MotorType.kBrushless);
        rightDrive = new CANSparkMax(config.RIGHT_DRIVE_PORT, MotorType.kBrushless);

        leftDrive.restoreFactoryDefaults();
        rightDrive.restoreFactoryDefaults();

        leftDrive.setIdleMode(IdleMode.kBrake);
        rightDrive.setIdleMode(IdleMode.kBrake);

        leftDrive.getEncoder().setPositionConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION);
        rightDrive.getEncoder().setPositionConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION);

        // velocity is in RPM, so to find MPS we must linearize and divide by 60
        leftDrive.getEncoder().setVelocityConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION / 60);
        rightDrive.getEncoder().setVelocityConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION / 60);

        if (onlyUseDriveMotors) {
            leftGroup = new MotorControllerGroup(leftDrive);
            rightGroup = new MotorControllerGroup(rightDrive);
        } else {
            var leftFollow = new CANSparkMax(config.LEFT_FOLLOW_PORT, MotorType.kBrushless);
            var rightFollow = new CANSparkMax(config.RIGHT_FOLLOW_PORT, MotorType.kBrushless);

            leftFollow.restoreFactoryDefaults();
            rightFollow.restoreFactoryDefaults();

            leftGroup = new MotorControllerGroup(leftDrive, leftFollow);
            rightGroup = new MotorControllerGroup(rightDrive, rightFollow);
        }

        rightGroup.setInverted(true);
        // rightDrive.getEncoder().setInverted(true);

        resetOdometry();
    }

    public void periodic() {
        updateOdometry();
    }

    /**
     * Sets motor output factor to a slowMultiplier if slowmode is enabled or 1 if
     * slowmode is disabled.
     * 
     * @param slowEnabled whether or not slowmode should be enabled
     */
    public void setSlow(boolean slowEnabled) {
        outputFactor = slowEnabled ? config.SLOW_MULTIPLIER : 1;
    }

    /** Updates the odometry pose with the heading and position measurements. */
    private void updateOdometry() {
        pose = odometry.update(getHeading(), (leftGroup.getInverted() ? -1 : 1) * leftDrive.getEncoder().getPosition(),
                (rightGroup.getInverted() ? -1 : 1) * rightDrive.getEncoder().getPosition());
    }

    /** Returns the robot's current yaw as a Rotation2d object (in radians). */
    public Rotation2d getHeading() {
        return new Rotation2d(-gyro.getAngle() * (Math.PI / 180));
    }

    /** Returns the robot's current pitch in degrees. */
    public double getPitch() {
        return -gyro.getPitch();
    }

    /** Returns the robot's current roll in degrees. */
    public double getRoll() {
        return gyro.getRoll();
    }

    /** Returns the average distance moved by left and right since last reset */
    public double getAverageDistance() {
        double left = leftDrive.getEncoder().getPosition() * (leftGroup.getInverted() ? -1 : 1);
        double right = rightDrive.getEncoder().getPosition() * (rightGroup.getInverted() ? -1 : 1);

        return (left + right) / 2d;
    }

    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
        double left = (leftGroup.getInverted() ? -1 : 1) * leftDrive.getEncoder().getVelocity();
        double right = (rightGroup.getInverted() ? -1 : 1) * rightDrive.getEncoder().getVelocity();

        return new DifferentialDriveWheelSpeeds(left, right);
    }

    /** Returns the robot's current pose. */
    public Pose2d getPose() {
        return pose;
    }

    /** Zeros odometry, gyro, and drive encoders. */
    public void resetOdometry() {
        gyro.reset();
        gyro.setAngleAdjustment(0);

        odometry.resetPosition(new Rotation2d(), 0.0, 0.0, new Pose2d());

        leftDrive.getEncoder().setPosition(0);
        rightDrive.getEncoder().setPosition(0);
    }

    /** resetOdometry() as a command */
    public Command resetOdometryCommand() {
        return Commands.runOnce(() -> resetOdometry());
    }

    /** Sets odometry to a specific Pose2d. */
    public void setOdometryPose(Pose2d newPose) {
        gyro.reset();
        gyro.setAngleAdjustment(-newPose.getRotation().getDegrees());
        odometry.resetPosition(newPose.getRotation(), 0, 0, newPose);

        leftDrive.getEncoder().setPosition(0);
        rightDrive.getEncoder().setPosition(0);
    }

    /** setOdometryPose() as a command */
    public Command setOdometryPoseCommand(Pose2d pose) {
        return Commands.runOnce(() -> setOdometryPose(pose));
    }

    /**
     * Calculates and applies left and right motor outputs from arcade drive forward
     * and turn values
     */
    public void arcadeDrive(double forward, double turn) {
        double left = forward + turn;
        double right = forward - turn;

        setPercentOutputs(left, right);
    }

    /** Sets the motors to a specific voltage */
    public void setVoltages(double left, double right) {
        leftGroup.setVoltage(left * outputFactor);
        rightGroup.setVoltage(right * outputFactor);
    }

    /** Sets the motors to a percent output (zero to one) */
    public void setPercentOutputs(double left, double right) {
        leftGroup.set(left);
        rightGroup.set(right);
    }

    /** Stops all drivetrain motors */
    public void stop() {
        setVoltages(0, 0);
    }

    /** @return whether or not the NavX is currently connected */
    public boolean navxConnected() {
        return gyro.isConnected();
    }
}
