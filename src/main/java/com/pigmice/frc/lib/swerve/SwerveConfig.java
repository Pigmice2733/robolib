package com.pigmice.frc.lib.swerve;

import com.pathplanner.lib.PathConstraints;
import com.swervedrivespecialties.swervelib.MkSwerveModuleBuilder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class SwerveConfig {
    public final MkSwerveModuleBuilder frontLeftModule;
    public final MkSwerveModuleBuilder frontRightModule;
    public final MkSwerveModuleBuilder backLeftModule;
    public final MkSwerveModuleBuilder backRightModule;

    public final PathConstraints pathConstraints;
    public final PIDController pathDrivePID;
    public final PIDController pathTurnPID;

    public final double manualDriveSpeed;
    public final double manualTurnSpeed;

    public final SwerveDriveKinematics kinematics;
    public final SimpleMotorFeedforward feedForward;

    public final double maxAttainableSpeed;

    public final ShuffleboardTab drivetrainTab;

    /** <strong>Recommended use:</strong> create a SwerveConfig objects in constants to pass into any subsystems and commands that need it
     * 
     * @param modules swerve module objects for use in the subsystem
     * @param pathConstraints max allowed speed and acceleration in path following 
     * @param pathDrivePID a PID controller used for driving during path following
     * @param pathTurnPID a PID controller used for turning during path following
     * @param manualDriveSpeed the max speed while driving with joysticks (meters / second)
     * @param manualTurnSpeed the max speed while turning with joysticks (radians / second)
     * @param kinematics used to convert chassis speeds to individual module speeds
     * @param feedForward used to convert wheel speeds (m/s) to volts
     * @param drivetrainTab the shuffleboard tab to use for all drivetrain debugging
     */
    public SwerveConfig(MkSwerveModuleBuilder frontLeftModule, MkSwerveModuleBuilder frontRightModule,
            MkSwerveModuleBuilder backLeftModule, MkSwerveModuleBuilder backRightModule,
            PathConstraints pathConstraints, PIDController pathDrivePID, PIDController pathTurnPID,
            float manualDriveSpeed, float manualTurnSpeed, SwerveDriveKinematics kinematics,
            SimpleMotorFeedforward feedForward, ShuffleboardTab drivetrainTab) {

        this.frontLeftModule = frontLeftModule;
        this.frontRightModule = frontRightModule;
        this.backLeftModule = backLeftModule;
        this.backRightModule = backRightModule;

        this.pathConstraints = pathConstraints;
        this.pathDrivePID = pathDrivePID;
        this.pathTurnPID = pathTurnPID;

        this.manualDriveSpeed = manualDriveSpeed; // max meters / second
        this.manualTurnSpeed = manualTurnSpeed; // max radians / second

        this.kinematics = kinematics;
        this.feedForward = feedForward;

        maxAttainableSpeed = (12.0 - feedForward.ks) / feedForward.kv;

        this.drivetrainTab = drivetrainTab;
    }
}
