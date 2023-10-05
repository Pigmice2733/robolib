package com.pigmice.frc.lib.drivetrain.differential.commands.auto;

import java.util.HashMap;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.FollowPathWithEvents;
import com.pathplanner.lib.commands.PPRamseteCommand;
import com.pigmice.frc.lib.drivetrain.differential.DifferentialDrivetrain;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FollowPathDifferential extends SequentialCommandGroup {
        /**
         * Use a RamseteController to follow a specified path.
         * Call only from autonomous path-following commands that define a trajectory
         * and a trajectory configuration.
         * 
         * @param drivetrain a drivetrain subsystem
         * @param trajectory a path-following trajectory
         */
        public FollowPathDifferential(DifferentialDrivetrain drivetrain,
                        PathPlannerTrajectory trajectory) {
                var config = drivetrain.config;
                addCommands(
                                drivetrain.setOdometryPoseCommand(trajectory.getInitialPose()),
                                new PPRamseteCommand(
                                                trajectory,
                                                drivetrain::getPose,
                                                new RamseteController(),
                                                config.FEED_FORWARD,
                                                config.KINEMATICS,
                                                drivetrain::getWheelSpeeds,
                                                new PIDController(config.PATH_P, config.PATH_I,
                                                                config.PATH_D),
                                                new PIDController(config.PATH_P, config.PATH_I,
                                                                config.PATH_D),
                                                drivetrain::setVoltages,
                                                false,
                                                drivetrain),
                                new InstantCommand(() -> drivetrain.arcadeDrive(0, 0)));

                addRequirements(drivetrain);
        }

        public FollowPathDifferential(DifferentialDrivetrain drivetrain, PathPlannerTrajectory trajectory,
                        HashMap<String, Command> eventMap) {
                var config = drivetrain.config;
                addCommands(
                                drivetrain.setOdometryPoseCommand(trajectory.getInitialPose()),
                                new FollowPathWithEvents(
                                                new PPRamseteCommand(
                                                                trajectory,
                                                                drivetrain::getPose,
                                                                new RamseteController(),
                                                                config.FEED_FORWARD,
                                                                config.KINEMATICS,
                                                                drivetrain::getWheelSpeeds,
                                                                new PIDController(config.PATH_P, config.PATH_I,
                                                                                config.PATH_D),
                                                                new PIDController(config.PATH_P, config.PATH_I,
                                                                                config.PATH_D),
                                                                drivetrain::setVoltages,
                                                                false,
                                                                drivetrain),
                                                trajectory.getMarkers(),
                                                eventMap),
                                new InstantCommand(() -> drivetrain.arcadeDrive(0, 0)));

                addRequirements(drivetrain);
        }

        /**
         * Use a RamseteController to follow a specified path.
         * Call only from autonomous path-following commands that define a trajectory
         * and a trajectory configuration.
         * 
         * @param drivetrain a drivetrain subsystem
         * @param pathName   the name of a premade path to follow
         */
        public FollowPathDifferential(DifferentialDrivetrain drivetrain, String pathName, boolean reversed) {
                this(
                                drivetrain,
                                PathPlanner.loadPath(
                                                pathName,
                                                drivetrain.config.PATH_CONSTRAINTS,
                                                reversed));
        }

        public FollowPathDifferential(DifferentialDrivetrain drivetrain, String pathName,
                        HashMap<String, Command> eventMap,
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