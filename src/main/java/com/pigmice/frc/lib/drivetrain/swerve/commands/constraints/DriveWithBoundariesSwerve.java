package com.pigmice.frc.lib.drivetrain.swerve.commands.constraints;

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

import java.util.function.Supplier;

import com.pigmice.frc.lib.drivetrain.swerve.SwerveDrivetrain;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DriveWithBoundariesSwerve extends CommandBase {
    private final SwerveDrivetrain drivetrain;
    private final Supplier<Double> driveSpeedX, driveSpeedY, turnSpeed;

    private final Translation2d minBound;
    private final Translation2d maxBound;

    /**
     * Field oriented DriveWithJoysticks but the robot will not go past the
     * specified boundaries.
     */
    public DriveWithBoundariesSwerve(SwerveDrivetrain drivetrain, Supplier<Double> driveSpeedX,
            Supplier<Double> driveSpeedY,
            Supplier<Double> turnSpeed, Translation2d minBound, Translation2d maxBound) {

        this.drivetrain = drivetrain;
        this.driveSpeedX = driveSpeedX;
        this.driveSpeedY = driveSpeedY;
        this.turnSpeed = turnSpeed;
        this.minBound = minBound;
        this.maxBound = maxBound;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        double vx = driveSpeedY.get();
        double vy = driveSpeedX.get();

        Pose2d pose = drivetrain.getPose();
        if (pose.getX() < minBound.getX())
            vx = Math.max(0, vx);
        if (pose.getY() < minBound.getY())
            vy = Math.max(0, vy);
        if (pose.getX() > maxBound.getX())
            vx = Math.min(0, vx);
        if (pose.getY() > maxBound.getY())
            vy = Math.min(0, vy);

        drivetrain.driveChassisSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(vx, vy,
                -turnSpeed.get(), drivetrain.getHeading()));
    }
}