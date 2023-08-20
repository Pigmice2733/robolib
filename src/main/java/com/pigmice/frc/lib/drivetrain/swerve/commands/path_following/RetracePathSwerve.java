// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.pigmice.frc.lib.drivetrain.swerve.commands.path_following;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPoint;
import com.pigmice.frc.lib.drivetrain.swerve.SwerveDrivetrain;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class RetracePathSwerve extends CommandBase {
    private final SwerveDrivetrain drivetrain;

    /**
     * While running: periodicly record robots positions. On End: scheduce a
     * FollowPath command to retrace the robots path
     */
    public RetracePathSwerve(SwerveDrivetrain drivetrain) {
        this.drivetrain = drivetrain;

        addRequirements(drivetrain);
    }

    Timer timer;
    TimerTask task;
    ArrayList<Pose2d> positions;

    @Override
    public void initialize() {
        positions = new ArrayList<Pose2d>();

        task = new TimerTask() {
            @Override
            public void run() {
                recordPosition();
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 500);
    }

    public void recordPosition() {
        positions.add(drivetrain.getPose());
        System.out.println("Timer Task Called");
    }

    @Override
    public void execute() {

    }

    @Override
    public void end(boolean interrupted) {
        timer.cancel();
        timer.purge();
        task.cancel();

        if (positions.size() < 2)
            return;

        Collections.reverse(positions);

        ArrayList<PathPoint> points = new ArrayList<PathPoint>();
        for (int i = 0; i < positions.size() - 1; i++) {
            Pose2d current = positions.get(i);
            Pose2d next = positions.get(i + 1);
            Rotation2d angleToNext = Rotation2d.fromRadians(Math.atan2(
                    next.getY() - current.getY(),
                    next.getX() - current.getX()));

            points.add(new PathPoint(current.getTranslation(), angleToNext, current.getRotation()));
        }

        points.add(new PathPoint(
                positions.get(positions.size() - 1).getTranslation(),
                points.get(points.size() - 1).heading,
                positions.get(positions.size() - 1).getRotation()));

        PathPlannerTrajectory trajectory = PathPlanner.generatePath(drivetrain.config.PATH_CONSTRAINTS, points);
        CommandScheduler.getInstance().schedule(new FollowPathSwerve(drivetrain, trajectory));
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
