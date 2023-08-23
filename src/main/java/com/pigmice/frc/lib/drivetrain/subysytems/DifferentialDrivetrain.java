// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.drivetrain.subysytems;

import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.lib.drivetrain.differential.DifferentialConfig;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DifferentialDrivetrain extends SubsystemBase {
    private final DifferentialConfig config;

    private final MotorControllerGroup leftGroup;
    private final MotorControllerGroup rightGroup;

    private final CANSparkMax leftDrive;
    private final CANSparkMax rightDrive;

    private final AHRS gyro = new AHRS();

    private final DifferentialDriveKinematics kinematics;
    private final DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(new Rotation2d(), 0, 0,
            new Pose2d());

    private Pose2d pose = new Pose2d();

    private boolean slowEnabled = false;
    private boolean backwards = false;
    public double outputFactor = 1;

    private Field2d field = new Field2d();

    public DifferentialDrivetrain(DifferentialConfig config, boolean onlyUseDriveMotors) {
        this.config = config;

        leftDrive = new CANSparkMax(config.LEFT_DRIVE_PORT, MotorType.kBrushless);
        rightDrive = new CANSparkMax(config.RIGHT_DRIVE_PORT, MotorType.kBrushless);

        leftDrive.restoreFactoryDefaults();
        rightDrive.restoreFactoryDefaults();

        kinematics = new DifferentialDriveKinematics(config.TRACK_WIDTH);

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
        this.slowEnabled = slowEnabled;
        outputFactor = slowEnabled ? config.SLOW_MULTIPLIER : 1;
    }

    public void enableSlow() {
        setSlow(true);
    }

    public void disableSlow() {
        setSlow(false);
    }

    public void toggleSlow() {
        setSlow(!this.slowEnabled);
    }

    public boolean isSlow() {
        return slowEnabled;
    }

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }

    public void enableBackwards() {
        setBackwards(true);
    }

    public void disableBackwards() {
        setBackwards(false);
    }

    public void toggleBackwards() {
        setBackwards(!this.backwards);
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

    /**
     * Returns the average distance moved by left and right wheels since last reset.
     */
    public double getAverageDistance() {
        double left = leftDrive.getEncoder().getPosition() * (leftGroup.getInverted() ? -1 : 1);
        double right = rightDrive.getEncoder().getPosition() * (rightGroup.getInverted() ? -1 : 1);

        return (left + right) / 2d;
    }

    /** Returns the DifferentialDriveKinematics object used by the drivetrain. */
    public DifferentialDriveKinematics getKinematics() {
        return kinematics;
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
        return new InstantCommand(() -> resetOdometry());
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
        return new InstantCommand(() -> setOdometryPose(pose));
    }

    public void arcadeDrive(double forward, double turn) {
        double left = forward + turn;
        double right = forward - turn;

        setPercentOutputs(left, right);
    }

    public void setVoltages(double left, double right) {
        leftGroup.setVoltage(left * outputFactor);
        rightGroup.setVoltage(right * outputFactor);
    }

    public void setPercentOutputs(double left, double right) {
        leftGroup.set(left);
        rightGroup.set(right);
    }

    public void stop() {
        setVoltages(0, 0);
    }

    public boolean navxConnected() {
        return gyro.isConnected();
    }
}
