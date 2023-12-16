package com.pigmice.frc.lib.drivetrain.swerve;

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.lib.shuffleboard_helper.ShuffleboardHelper;
import com.revrobotics.CANSparkMax;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveDrivetrain extends SubsystemBase {
        public final SwerveConfig config;

        private final SwerveModule frontLeftModule;
        private final SwerveModule frontRightModule;
        private final SwerveModule backLeftModule;
        private final SwerveModule backRightModule;

        private final AHRS gyro;
        private final SwerveDriveOdometry odometry;

        private Pose2d pose;
        private ChassisSpeeds targetSpeeds;
        private SwerveModuleState[] states;

        private boolean isSlow;

        public SwerveDrivetrain(SwerveConfig config) {
                this.config = config;

                frontLeftModule = config.FRONT_LEFT_MODULE.build();
                frontRightModule = config.FRONT_RIGHT_MODULE.build();
                backLeftModule = config.BACK_LEFT_MODULE.build();
                backRightModule = config.BACK_RIGHT_MODULE.build();

                gyro = new AHRS();
                pose = new Pose2d();
                targetSpeeds = new ChassisSpeeds();
                states = getModuleStates();

                odometry = new SwerveDriveOdometry(config.KINEMATICS, new Rotation2d(),
                                getModulePositions());
                resetOdometry();

                isSlow = false;

                config.DRIVETRAIN_TAB.add("Heading", gyro).withPosition(4, 0);
                ShuffleboardHelper.addOutput("Pose X", config.DRIVETRAIN_TAB, () -> pose.getX())
                                .withPosition(4, 2);
                ShuffleboardHelper.addOutput("Pose Y", config.DRIVETRAIN_TAB, () -> pose.getY())
                                .withPosition(5, 2);

                ShuffleboardHelper
                                .addOutput("Target X vel", config.DRIVETRAIN_TAB,
                                                () -> targetSpeeds.vxMetersPerSecond)
                                .withPosition(0, 3);
                ShuffleboardHelper
                                .addOutput("Target Y vel", config.DRIVETRAIN_TAB,
                                                () -> targetSpeeds.vyMetersPerSecond)
                                .withPosition(1, 3);
                ShuffleboardHelper.addOutput("Target Rot. vel", config.DRIVETRAIN_TAB,
                                () -> targetSpeeds.omegaRadiansPerSecond).withPosition(2, 3);

                ShuffleboardHelper.addOutput("Velocity X", config.DRIVETRAIN_TAB,
                                () -> gyro.getVelocityX());
                ShuffleboardHelper.addOutput("Velocity Y", config.DRIVETRAIN_TAB,
                                () -> gyro.getVelocityY());
                ShuffleboardHelper.addOutput("Speed", config.DRIVETRAIN_TAB,
                                () -> Math
                                                .sqrt(Math.pow(gyro.getVelocityX(), 2)
                                                                + Math.pow(gyro.getVelocityY(), 2)));

                ((CANSparkMax) frontLeftModule.getDriveMotor()).setSmartCurrentLimit(20);
                ((CANSparkMax) frontRightModule.getDriveMotor()).setSmartCurrentLimit(20);
                ((CANSparkMax) backLeftModule.getDriveMotor()).setSmartCurrentLimit(20);
                ((CANSparkMax) backRightModule.getDriveMotor()).setSmartCurrentLimit(20);

                ((CANSparkMax) frontLeftModule.getSteerMotor()).setSmartCurrentLimit(20);
                ((CANSparkMax) frontRightModule.getSteerMotor()).setSmartCurrentLimit(20);
                ((CANSparkMax) backLeftModule.getSteerMotor()).setSmartCurrentLimit(20);
                ((CANSparkMax) backRightModule.getSteerMotor()).setSmartCurrentLimit(20);

        }

        @Override
        public void periodic() {
                applyModuleStates();
                pose = odometry.update(getHeading(), getModulePositions());
        }

        /** Apply the current target swerve module states. */
        private void applyModuleStates() {
                if (states == null)
                        System.out.println("Module states are NULL");

                SwerveDriveKinematics.desaturateWheelSpeeds(states, config.MAX_ATTAINABLE_SPEED);

                frontLeftModule.set(calculateFeedForward(states[0].speedMetersPerSecond),
                                states[0].angle.getRadians());
                frontRightModule.set(calculateFeedForward(states[1].speedMetersPerSecond),
                                states[1].angle.getRadians());
                backLeftModule.set(calculateFeedForward(states[2].speedMetersPerSecond),
                                states[2].angle.getRadians());
                backRightModule.set(calculateFeedForward(states[3].speedMetersPerSecond),
                                states[3].angle.getRadians());
        }

        private double calculateFeedForward(double velocity) {
                velocity = MathUtil.applyDeadband(velocity, 0.001);
                velocity *= isSlow ? config.SLOWMODE_MULTIPLIER : 1;
                return config.FEED_FORWARD.calculate(velocity);
        }

        /** Set target swerve module states based on a ChassisSpeeds object. */
        public void driveChassisSpeeds(ChassisSpeeds speeds) {
                targetSpeeds = speeds;
                driveModuleStates(config.KINEMATICS.toSwerveModuleStates(speeds, config.ROTATION_CENTER_OFFSET));
        }

        /** Set target swerve module states. */
        public void driveModuleStates(SwerveModuleState[] states) {
                targetSpeeds = config.KINEMATICS.toChassisSpeeds(states);
                this.states = states;
        }

        /**
         * Returns an array of SwerveModulePosition objects, containing the position and
         * angle of each
         * module.
         */
        public SwerveModulePosition[] getModulePositions() {
                SwerveModulePosition[] positions = {
                                new SwerveModulePosition(frontLeftModule.getDriveDistance(),
                                                new Rotation2d(frontLeftModule.getSteerAngle())),
                                new SwerveModulePosition(frontRightModule.getDriveDistance(),
                                                new Rotation2d(frontRightModule.getSteerAngle())),
                                new SwerveModulePosition(backLeftModule.getDriveDistance(),
                                                new Rotation2d(backLeftModule.getSteerAngle())),
                                new SwerveModulePosition(backRightModule.getDriveDistance(),
                                                new Rotation2d(backRightModule.getSteerAngle()))
                };
                return positions;
        }

        /**
         * Returns an array of SwerveModuleState objects, containing the velocity and
         * angle of each
         * module.
         */
        public SwerveModuleState[] getModuleStates() {
                SwerveModuleState[] states = {
                                new SwerveModuleState(frontLeftModule.getDriveVelocity(),
                                                new Rotation2d(frontLeftModule.getSteerAngle())),
                                new SwerveModuleState(frontRightModule.getDriveVelocity(),
                                                new Rotation2d(frontRightModule.getSteerAngle())),
                                new SwerveModuleState(backLeftModule.getDriveVelocity(),
                                                new Rotation2d(backLeftModule.getSteerAngle())),
                                new SwerveModuleState(backRightModule.getDriveVelocity(),
                                                new Rotation2d(backRightModule.getSteerAngle()))
                };
                return states;
        }

        /** Resets the odometry and gyro to the given pose. */
        public void resetOdometry(Pose2d pose) {
                System.out.println("Reset Odometry to: " + pose);
                gyro.reset();
                gyro.setAngleAdjustment(-pose.getRotation().getDegrees());
                odometry.resetPosition(getHeading(), getModulePositions(), pose);
        }

        /** Resets the odometry and gyro to the origin position. */
        public void resetOdometry() {
                System.out.println("Reset odometry");
                resetOdometry(new Pose2d());
        }

        /** Returns a command to reset the odometry to the given pose. */
        public Command resetOdometryCommand(Pose2d pose) {
                return new InstantCommand(() -> resetOdometry(pose));
        }

        /** Returns the current yaw of the robot. */
        public Rotation2d getHeading() {
                return new Rotation2d(Math.toRadians(-gyro.getAngle()));
        }

        /** Returns the current (estimated) pose of the robot. */
        public Pose2d getPose() {
                if (pose == null)
                        System.out.println("Robot pose is NULL");
                return pose;
        }

        /** Returns true if slowmode is enabled, false otherwise. */
        public boolean getSlowmode() {
                return isSlow;
        }

        /** Turn slowmode on and off. */
        public void setSlowmode(boolean slow) {
                isSlow = slow;
        }
}
