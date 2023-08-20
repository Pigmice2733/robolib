// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.drivetrain.differential;

import static edu.wpi.first.math.system.plant.LinearSystemId.identifyDrivetrainSystem;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.DifferentialDriveAccelerationLimiter;
import edu.wpi.first.math.controller.DifferentialDriveFeedforward;
import edu.wpi.first.math.controller.DifferentialDriveWheelVoltages;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DifferentialDrivetrain extends SubsystemBase {
    private final DifferentialConfig config;
    GenericEntry xPosEntry;

    // TODO
    // private final GenericEntry yPosEntry, motorVeloEntry, navXVeloEntry,
    // leftVoltageEntry, rightVoltageEntry;

    private final CANSparkMax leftDrive;
    private final CANSparkMax rightDrive;
    private final CANSparkMax leftFollow;
    private final CANSparkMax rightFollow;

    private final MotorControllerGroup leftGroup;
    private final MotorControllerGroup rightGroup;

    // private final DifferentialDrive differentialDrive = new
    // DifferentialDrive(leftGroup, rightGroup);

    private DifferentialDriveWheelSpeeds lastSpeeds = new DifferentialDriveWheelSpeeds();

    private final AHRS gyro = new AHRS();

    private LinearSystem<N2, N2, N2> drivetrainModel;

    private final DifferentialDriveKinematics kinematics;
    private final DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(new Rotation2d(), 0, 0,
            new Pose2d());

    private final DifferentialDriveAccelerationLimiter accelerationLimiter;

    private final DifferentialDriveFeedforward feedforward;

    private Pose2d pose = new Pose2d();

    private boolean slowEnabled = false;
    private boolean backwards = false;
    public double outputFactor = 1;

    private Field2d field = new Field2d();

    public DifferentialDrivetrain(DifferentialConfig config) {
        this.config = config;

        leftDrive = new CANSparkMax(config.LEFT_DRIVE_PORT, MotorType.kBrushless);
        rightDrive = new CANSparkMax(config.RIGHT_DRIVE_PORT, MotorType.kBrushless);
        leftFollow = new CANSparkMax(config.LEFT_FOLLOW_PORT, MotorType.kBrushless);
        rightFollow = new CANSparkMax(config.RIGHT_FOLLOW_PORT, MotorType.kBrushless);

        leftDrive.restoreFactoryDefaults();
        rightDrive.restoreFactoryDefaults();
        leftFollow.restoreFactoryDefaults();
        rightFollow.restoreFactoryDefaults();

        leftGroup = new MotorControllerGroup(leftDrive, leftFollow);
        rightGroup = new MotorControllerGroup(rightDrive, rightFollow);

        leftGroup.setInverted(config.LEFT_INVERTED);
        rightGroup.setInverted(config.RIGHT_INVERTED);

        drivetrainModel = identifyDrivetrainSystem(
                config.FEED_FORWARD.LINEAR_V,
                config.FEED_FORWARD.LINEAR_A,
                config.FEED_FORWARD.ANGULAR_V,
                config.FEED_FORWARD.ANGULAR_A);

        kinematics = new DifferentialDriveKinematics(config.DRIVETRAIN_WIDTH);

        accelerationLimiter = new DifferentialDriveAccelerationLimiter(
                drivetrainModel,
                config.DRIVETRAIN_WIDTH,
                config.MAX_ACCELERATION,
                config.MAX_ANGULAR_ACCELERATION);

        feedforward = new DifferentialDriveFeedforward(
                config.FEED_FORWARD.LINEAR_V, config.FEED_FORWARD.LINEAR_A,
                config.FEED_FORWARD.ANGULAR_V, config.FEED_FORWARD.ANGULAR_A,
                config.DRIVETRAIN_WIDTH);

        rightDrive.restoreFactoryDefaults();
        leftDrive.restoreFactoryDefaults();
        leftFollow.restoreFactoryDefaults();
        rightFollow.restoreFactoryDefaults();

        enableBrakeMode();

        // leftFollow.follow(leftDrive);
        // rightFollow.follow(rightDrive);

        // rightDrive.setInverted(true);

        rightGroup.setInverted(true);
        leftGroup.setInverted(false);

        leftDrive.getEncoder().setPositionConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION);
        rightDrive.getEncoder().setPositionConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION);

        // velocity is in RPM, so to find MPS we must linearize and divide by 60
        leftDrive.getEncoder().setVelocityConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION / 60);
        rightDrive.getEncoder().setVelocityConversionFactor(config.ROTATION_TO_DISTANCE_CONVERSION / 60);

        // TODO
        // ShuffleboardTab driveTab = Shuffleboard.getTab("Drivetrain");

        // xPosEntry = driveTab.add("X", 0.0).withPosition(0, 2).getEntry();
        // yPosEntry = driveTab.add("Y", 0.0).withPosition(1, 2).getEntry();

        // motorVeloEntry = driveTab.add("Motor Velocity", 0).withPosition(2,
        // 1).getEntry();
        // navXVeloEntry = driveTab.add("NavX Velocity", 0).withPosition(2,
        // 0).getEntry();

        // leftVoltageEntry = driveTab.add("Left Voltage",
        // 0).withWidget(BuiltInWidgets.kVoltageView).withPosition(0, 3)
        // .withProperties(Map.of("min", -12, "max", 12)).getEntry();
        // rightVoltageEntry = driveTab.add("Right Voltage",
        // 0).withWidget(BuiltInWidgets.kVoltageView).withPosition(0, 4)
        // .withProperties(Map.of("min", -12, "max", 12)).getEntry();

        // driveTab.add("Heading", gyro).withPosition(0, 0);

        // driveTab.add("Coast Mode", new InstantCommand(() ->
        // enableCoastMode())).withPosition(2, 2);
        // driveTab.add("Brake Mode", new InstantCommand(() ->
        // enableBrakeMode())).withPosition(2, 3);

        // driveTab.add("Field",
        // field).withWidget(BuiltInWidgets.kField).withPosition(4, 0).withSize(7, 5);
        // SmartDashboard.putData("FIELD", field);

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

        // TODO
        // if (ShuffleboardConfig.debugPrintsEnabled) {
        // xPosEntry.setDouble(pose.getX());
        // yPosEntry.setDouble(pose.getY());
        // field.setRobotPose(getPose());
        // SmartDashboard.putNumber("Pitch", this.getPitch());
        // }
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
     * Returns a DifferentialDriveWheelSpeeds object from the encoder velocities.
     */
    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
        double left = (leftGroup.getInverted() ? -1 : 1) * leftDrive.getEncoder().getVelocity();
        double right = (rightGroup.getInverted() ? -1 : 1) * rightDrive.getEncoder().getVelocity();

        return new DifferentialDriveWheelSpeeds(left, right);
    }

    /**
     * Returns the average distance moved by left and right wheels since last reset.
     */
    public double getAverageDistance() {
        double left = leftDrive.getEncoder().getPosition() * (leftGroup.getInverted() ? -1 : 1);
        double right = rightDrive.getEncoder().getPosition() * (rightGroup.getInverted() ? -1 : 1);

        return (left + right) / 2d;
    }

    /** Returns the feedforward object used by the drivetrain. */
    public DifferentialDriveFeedforward getFeedForward() {
        return feedforward;
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

    /**
     * Drives the robot with given directional and rotational speeds.
     * 
     * @param forward speed in robot's current direction
     * @param turn    turn speed (clockwise is positive)
     */
    public void arcadeDrive(double forward, double turn) {
        driveLimitedChassisSpeeds(new ChassisSpeeds(forward, 0, -turn));
    }

    public void driveWheelSpeeds(DifferentialDriveWheelSpeeds wheelSpeeds) {
        driveVoltages(feedforward.calculate(lastSpeeds.leftMetersPerSecond, wheelSpeeds.leftMetersPerSecond,
                lastSpeeds.rightMetersPerSecond, wheelSpeeds.rightMetersPerSecond, 20.0 / 1000.0));
        lastSpeeds = wheelSpeeds;
    }

    public void driveLimitedWheelSpeeds(DifferentialDriveWheelSpeeds wheelSpeeds) {
        driveLimitedVoltages(feedforward.calculate(lastSpeeds.leftMetersPerSecond, wheelSpeeds.leftMetersPerSecond,
                lastSpeeds.rightMetersPerSecond, wheelSpeeds.rightMetersPerSecond, 20.0 / 1000.0));
        lastSpeeds = wheelSpeeds;
    }

    public void driveLimitedVoltages(DifferentialDriveWheelVoltages voltages) {
        // double leftRate = leftDrive.getEncoder().getVelocity();
        // double rightRate = rightDrive.getEncoder().getVelocity();
        // System.out.println("BEFORE VOLTAGES: " + voltages.left + " | " +
        // voltages.right);

        DifferentialDriveWheelSpeeds speeds = getWheelSpeeds();

        voltages = accelerationLimiter.calculate(speeds.leftMetersPerSecond,
                speeds.rightMetersPerSecond, voltages.left,
                voltages.right);

        // TODO
        // if (ShuffleboardConfig.debugPrintsEnabled) {
        // motorVeloEntry
        // .setDouble(
        // (Math.abs(speeds.leftMetersPerSecond) +
        // Math.abs(speeds.rightMetersPerSecond)) / 2d);
        // navXVeloEntry.setDouble(this.gyro.getVelocityY());
        // leftVoltageEntry.setDouble(voltages.left);
        // rightVoltageEntry.setDouble(voltages.right);
        // }

        // System.out.println("AFTER VOLTAGES: " + voltages.left + " | " +
        // voltages.right);
        driveVoltages(voltages);
    }

    public void driveVoltages(double left, double right) {
        leftGroup.setVoltage(left * outputFactor);
        rightGroup.setVoltage(right * outputFactor);

        // TODO
        // if (ShuffleboardConfig.debugPrintsEnabled) {
        // leftVoltageEntry.setDouble(left);
        // rightVoltageEntry.setDouble(right);
        // }
    }

    public void driveVoltages(DifferentialDriveWheelVoltages voltages) {
        driveVoltages(voltages.left, voltages.right);
    }

    public void driveChassisSpeeds(ChassisSpeeds speeds) {
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(speeds);
        driveWheelSpeeds(wheelSpeeds);
    }

    public void driveLimitedChassisSpeeds(ChassisSpeeds speeds) {
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(speeds);
        driveLimitedWheelSpeeds(wheelSpeeds);
    }

    public void stop() {
        driveVoltages(0, 0);
    }

    public void enableBrakeMode() {
        leftDrive.setIdleMode(IdleMode.kBrake);
        rightDrive.setIdleMode(IdleMode.kBrake);
        leftFollow.setIdleMode(IdleMode.kBrake);
        rightFollow.setIdleMode(IdleMode.kBrake);
    }

    public void enableCoastMode() {
        leftDrive.setIdleMode(IdleMode.kCoast);
        rightDrive.setIdleMode(IdleMode.kCoast);
        leftFollow.setIdleMode(IdleMode.kCoast);
        rightFollow.setIdleMode(IdleMode.kCoast);
    }

    public boolean navxConnected() {
        return gyro.isConnected();
    }
}
