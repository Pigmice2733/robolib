// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.swerve.commands.pathfinder;

import java.util.List;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPoint;
import com.pigmice.frc.lib.swerve.SwerveDrivetrain;
import com.pigmice.frc.lib.swerve.commands.path_following.FollowPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class DriveToPoint extends CommandBase {
    private final SwerveDrivetrain drivetrain;
    private final Pose2d targetPose;
    private final XboxController controllerToRumble;

    private Command pathCommand;

    public DriveToPoint(SwerveDrivetrain drivetrain, Pose2d targetPose, XboxController controllerToRumble) {
        this.drivetrain = drivetrain;
        this.targetPose = targetPose;
        this.controllerToRumble = controllerToRumble;
    }

    @Override
    public void initialize() {
        pathCommand = new SequentialCommandGroup(
                new FollowPath(drivetrain, generateTrajectory(drivetrain, targetPose)),
                new InstantCommand(() -> controllerToRumble.setRumble(RumbleType.kBothRumble, 0.75)));
        CommandScheduler.getInstance().schedule(pathCommand);
    }

    @Override
    public void end(boolean interrupted) {
        if (pathCommand != null) {
            pathCommand.cancel();
        }
        controllerToRumble.setRumble(RumbleType.kBothRumble, 0);
    }

    @Override
    public boolean isFinished() {
        return pathCommand.isFinished();
    }

    public static PathPlannerTrajectory generateTrajectory(SwerveDrivetrain drivetrain, Pose2d targetPose) {
        Pose2d currentPose = drivetrain.getPose();

        // Angle facing the end point when at the current point
        Rotation2d angleToEnd = Rotation2d.fromRadians(Math.atan2(targetPose.getY() - currentPose.getY(),
                targetPose.getX() - currentPose.getX()));

        PathPoint currentPoint = new PathPoint(currentPose.getTranslation(), angleToEnd, currentPose.getRotation());
        PathPoint endPoint = new PathPoint(targetPose.getTranslation(), angleToEnd, targetPose.getRotation());

        return PathPlanner.generatePath(drivetrain.config.pathConstraints, List.of(currentPoint, endPoint));
    }
}
