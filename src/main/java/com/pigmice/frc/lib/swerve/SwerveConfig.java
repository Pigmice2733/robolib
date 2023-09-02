package com.pigmice.frc.lib.swerve;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.MkSwerveModuleBuilder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
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
    public final PIDConstants PATH_TURN_PID_CONSTNATS;

    public final double MANUAL_DRIVE_SPEED;
    public final double MANUAL_TURN_SPEED;

    public final SwerveDriveKinematics KINEMATICS;
    public final SimpleMotorFeedforward FEED_FORWARD;

    public final double MAX_ATTAINABLE_SPEED;

    public final ShuffleboardTab DRIVETRAIN_TAB;

    /**
     * <strong>Recommended use:</strong> create a SwerveConfig objects in constants
     * to pass into any subsystems and commands that need it
     * 
     * @param modules          swerve module objects for use in the subsystem
     * @param pathConstraints  max allowed speed and acceleration in path following
     * @param pathDrivePID     a PID controller used for driving during path
     *                         following
     * @param pathTurnPID      a PID controller used for turning during path
     *                         following
     * @param manualDriveSpeed the max speed while driving with joysticks (meters /
     *                         second)
     * @param manualTurnSpeed  the max speed while turning with joysticks (radians /
     *                         second)
     * @param kinematics       used to convert chassis speeds to individual module
     *                         speeds
     * @param feedForward      used to convert wheel speeds (m/s) to volts
     * @param drivetrainTab    the shuffleboard tab to use for all drivetrain
     *                         debugging
     */
    public SwerveConfig(MkSwerveModuleBuilder frontLeftModule, MkSwerveModuleBuilder frontRightModule,
            MkSwerveModuleBuilder backLeftModule, MkSwerveModuleBuilder backRightModule,
            PathConstraints pathConstraints, PIDController pathDrivePID, PIDController pathTurnPID,
            double manualDriveSpeed, double manualTurnSpeed, SwerveDriveKinematics kinematics,
            SimpleMotorFeedforward feedForward, ShuffleboardTab drivetrainTab) {

        this.FRONT_LEFT_MODULE = frontLeftModule;
        this.FRONT_RIGHT_MODULE = frontRightModule;
        this.BACK_LEFT_MODULE = backLeftModule;
        this.BACK_RIGHT_MODULE = backRightModule;

        this.PATH_CONSTRAINTS = pathConstraints;
        this.PATH_DRIVE_PID = pathDrivePID;
        this.PATH_TURN_PID = pathTurnPID;

        this.PATH_DRIVE_PID_CONSTANTS = constantsFromController(pathDrivePID);
        this.PATH_TURN_PID_CONSTNATS = constantsFromController(pathTurnPID);

        this.MANUAL_DRIVE_SPEED = manualDriveSpeed; // max meters / second
        this.MANUAL_TURN_SPEED = manualTurnSpeed; // max radians / second

        this.KINEMATICS = kinematics;
        this.FEED_FORWARD = feedForward;

        MAX_ATTAINABLE_SPEED = (12.0 - feedForward.ks) / feedForward.kv;

        this.DRIVETRAIN_TAB = drivetrainTab;
    }

    private static PIDConstants constantsFromController(PIDController controller) {
        return new PIDConstants(controller.getP(), controller.getI(), controller.getD());
    }
}
