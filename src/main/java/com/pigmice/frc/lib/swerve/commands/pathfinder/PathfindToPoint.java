// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.swerve.commands.pathfinder;

import com.pigmice.frc.lib.pathfinder.Pathfinder;
import com.pigmice.frc.lib.pathfinder.PathfinderResult;
import com.pigmice.frc.lib.swerve.SwerveDrivetrain;
import com.pigmice.frc.lib.swerve.commands.path_following.FollowPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class PathfindToPoint extends CommandBase {
    private final SwerveDrivetrain drivetrain;
    private final Pathfinder pathfinder;
    private final Pose2d goalPose;

    private FollowPath pathCommand;

    /** Find a path between the robots current pose and goal pose then follow it */
    public PathfindToPoint(SwerveDrivetrain drivetrain, Pathfinder pathfinder, Pose2d goalPose) {
        this.drivetrain = drivetrain;
        this.pathfinder = pathfinder;
        this.goalPose = goalPose;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        PathfinderResult result = pathfinder.findPath(drivetrain.getPose().getTranslation(), goalPose.getTranslation());

        if (!result.pathFound()) {
            end(true);
            return;
        }
        pathCommand = new FollowPath(drivetrain, result.getAsTrajectory(drivetrain.config.pathConstraints));
        pathCommand.schedule();
    }

    @Override
    public void end(boolean interrupted) {
        if (pathCommand != null) {
            pathCommand.cancel();
        }
    }

    @Override
    public boolean isFinished() {
        return pathCommand == null || pathCommand.isFinished();
    }
}