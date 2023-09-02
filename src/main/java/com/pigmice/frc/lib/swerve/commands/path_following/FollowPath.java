package com.pigmice.frc.lib.swerve.commands.path_following;

import java.util.HashMap;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import com.pigmice.frc.lib.swerve.SwerveConfig;
import com.pigmice.frc.lib.swerve.SwerveDrivetrain;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FollowPath extends SequentialCommandGroup {

    /**
     * Use a SwerveController to follow a specified path.
     * 
     * @param drivetrain a drivetrain subsystem
     * @param trajectory a path-following trajectory
     */
    public FollowPath(SwerveDrivetrain drivetrain, PathPlannerTrajectory trajectory) {
        SwerveConfig config = drivetrain.config;
        addCommands(
                drivetrain.resetOdometryCommand(trajectory.getInitialHolonomicPose()),
                new InstantCommand(() -> {
                    SmartDashboard.putBoolean("Command Done", false);
                }),
                new PPSwerveControllerCommand(
                        trajectory,
                        drivetrain::getPose,
                        config.KINEMATICS,
                        config.PATH_DRIVE_PID, // X controller
                        config.PATH_DRIVE_PID, // Y controller
                        config.PATH_TURN_PID, // turn controller
                        (output) -> drivetrain.driveModuleStates(output),
                        drivetrain),
                new InstantCommand(() -> this.cancel()));
        addRequirements(drivetrain);
    }

    /**
     * Use a SwerveController to follow a specified path.
     * 
     * @param drivetrain a drivetrain subsystem
     * @param trajectory a path-following trajectory
     * @param eventMap   commands to execute at certain events along the path
     *                   (configure events in Path Planner)
     */
    public FollowPath(SwerveDrivetrain drivetrain, PathPlannerTrajectory trajectory,
            HashMap<String, Command> eventMap) {
        SwerveConfig config = drivetrain.config;

        SwerveAutoBuilder autoBuilder = new SwerveAutoBuilder(
                drivetrain::getPose,
                drivetrain::resetOdometry,
                config.KINEMATICS,
                config.PATH_DRIVE_PID_CONSTANTS,
                config.PATH_TURN_PID_CONSTNATS,
                (SwerveModuleState[] output) -> drivetrain.driveModuleStates(output),
                eventMap,
                false,
                drivetrain);

        addCommands(
                drivetrain.resetOdometryCommand(trajectory.getInitialHolonomicPose()),
                autoBuilder.fullAuto(trajectory));
        addRequirements(drivetrain);
    }

    /**
     * Use a SwerveController to follow a specified path.
     * 
     * @param drivetrain a drivetrain subsystem
     * @param pathName   the name of a premade path to follow
     * @param reversed   reverse the robots direction
     */
    public FollowPath(SwerveDrivetrain drivetrain, String pathName, boolean reversed) {
        this(
                drivetrain,
                PathPlanner.loadPath(
                        pathName,
                        drivetrain.config.PATH_CONSTRAINTS,
                        reversed));
    }

    /**
     * Use a SwerveController to follow a specified path.
     * 
     * @param drivetrain a drivetrain subsystem
     * @param pathName   the name of a premade path to follow
     * @param eventMap   commands to execute at certain events along the path
     *                   (configure events in Path Planner)
     * @param reversed   reverse the robots direction
     */
    public FollowPath(SwerveDrivetrain drivetrain, String pathName, HashMap<String, Command> eventMap,
            boolean reversed) {
        this(
                drivetrain,
                PathPlanner.loadPath(
                        pathName,
                        drivetrain.config.PATH_CONSTRAINTS,
                        reversed),
                eventMap);
    }
}