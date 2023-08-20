
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package com.pigmice.frc.lib.drivetrain.swerve.commands.constraints;

import java.util.function.Supplier;

import com.pigmice.frc.lib.drivetrain.swerve.SwerveDrivetrain;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DriveFacingPositionSwerve extends CommandBase {
    private final SwerveDrivetrain drivetrain;
    private final Supplier<Double> driveSpeedX, driveSpeedY;
    private final Translation2d targetPos;
    private PIDController controller = new PIDController(0.1, 0, 0);

    /**
     * Field oriented DriveWithJoysticks with the robot always facing the specified
     * target positions
     */
    public DriveFacingPositionSwerve(SwerveDrivetrain drivetrain, Supplier<Double> driveSpeedX,
            Supplier<Double> driveSpeedY,
            Translation2d targetPos) {
        this.drivetrain = drivetrain;
        this.driveSpeedX = driveSpeedX;
        this.driveSpeedY = driveSpeedY;
        this.targetPos = targetPos;

        controller.enableContinuousInput(-180, 180);
        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        Translation2d robotPose = drivetrain.getPose().getTranslation();

        double angle = Math
                .toDegrees(Math.atan2(targetPos.getY() - robotPose.getY(), targetPos.getX() - robotPose.getX()));
        double radPerSec = controller.calculate(drivetrain.getPose().getRotation().getDegrees(), angle);

        drivetrain.driveChassisSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(driveSpeedY.get(), driveSpeedX.get(),
                radPerSec, drivetrain.getHeading()));
    }
}