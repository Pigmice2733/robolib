package com.pigmice.frc.lib.drivetrain.swerve;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.MkSwerveModuleBuilder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class SwerveConfig {
    public final MkSwerveModuleBuilder FRONT_LEFT_MODULE;
    public final MkSwerveModuleBuilder FRONT_RIGHT_MODULE;
    public final MkSwerveModuleBuilder BACK_LEFT_MODULE;
    public final MkSwerveModuleBuilder BACK_RIGHT_MODULE;

    public final PathConstraints PATH_CONSTRAINTS;
    public final PIDController PATH_DRIVE_PID;
    public final PIDController PATH_TURN_PID;

    public final PIDConstants PATH_DRIVE_PID_CONSTANTS;
    public final PIDConstants PATH_TURN_PID_CONSTANTS;

    public final double MANUAL_DRIVE_SPEED;
    public final double MANUAL_TURN_SPEED;

    public final double SLOWMODE_MULTIPLIER;

    public final SwerveDriveKinematics KINEMATICS;
    public final SimpleMotorFeedforward FEED_FORWARD;

    public final double MAX_ATTAINABLE_SPEED;

    public final ShuffleboardTab DRIVETRAIN_TAB;

    public final Translation2d ROTATION_CENTER_OFFSET;

    /**
     * <strong>Recommended use:</strong> create a SwerveConfig object in constants
     * to pass into any subsystems and commands that need it.
     * 
     * @param modules          swerve module objects for use in the subsystem
     * @param pathConstraints  max allowed speed and acceleration in path following
     * @param pathDrivePID     a PID controller used for driving during path
     *                         following
     * @param pathTurnPID      a PID controller used for turning during path
     *                         following
     * @param manualDriveSpeed max speed while driving with joysticks (m/s)
     * @param manualTurnSpeed  max speed while turning with joysticks (rad/s)
     * @param slowMultiplier   multiplied by speed when slowmode is on
     * @param kinematics       used to convert chassis speeds to individual module
     *                         speeds
     * @param feedForward      used to convert wheel speeds to volts
     * @param drivetrainTab    Shuffleboard tab to use for all drivetrain
     *                         debugging
     */
    public SwerveConfig(MkSwerveModuleBuilder frontLeftModule,
            MkSwerveModuleBuilder frontRightModule,
            MkSwerveModuleBuilder backLeftModule, MkSwerveModuleBuilder backRightModule,
            PathConstraints pathConstraints, PIDController pathDrivePID, PIDController pathTurnPID,
            double manualDriveSpeed, double manualTurnSpeed, double slowMultiplier,
            SwerveDriveKinematics kinematics, SimpleMotorFeedforward feedForward,
            ShuffleboardTab drivetrainTab, Translation2d rotationCenterOffset) {

        FRONT_LEFT_MODULE = frontLeftModule;
        FRONT_RIGHT_MODULE = frontRightModule;
        BACK_LEFT_MODULE = backLeftModule;
        BACK_RIGHT_MODULE = backRightModule;

        PATH_CONSTRAINTS = pathConstraints;
        PATH_DRIVE_PID = pathDrivePID;
        PATH_TURN_PID = pathTurnPID;

        PATH_DRIVE_PID_CONSTANTS = new PIDConstants(
                pathDrivePID.getP(), pathDrivePID.getI(), pathDrivePID.getD());
        PATH_TURN_PID_CONSTANTS = new PIDConstants(
                pathDrivePID.getP(), pathDrivePID.getI(), pathDrivePID.getD());

        MANUAL_DRIVE_SPEED = manualDriveSpeed; // max meters / second
        MANUAL_TURN_SPEED = manualTurnSpeed; // max radians / second

        SLOWMODE_MULTIPLIER = slowMultiplier;

        KINEMATICS = kinematics;
        FEED_FORWARD = feedForward;

        MAX_ATTAINABLE_SPEED = (12.0 - feedForward.ks) / feedForward.kv;

        DRIVETRAIN_TAB = drivetrainTab;

        this.ROTATION_CENTER_OFFSET = rotationCenterOffset;
    }
}
