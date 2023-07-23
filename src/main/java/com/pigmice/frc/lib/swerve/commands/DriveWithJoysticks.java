// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package com.pigmice.frc.lib.swerve.commands;

import java.util.function.Supplier;

import com.pigmice.frc.lib.swerve.SwerveDrivetrain;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DriveWithJoysticks extends CommandBase {
    private final SwerveDrivetrain drivetrain;
    private final Supplier<Double> driveSpeedX, driveSpeedY, turnSpeed;
    private final Supplier<Boolean> fieldOriented;

    /** The default drive command for swerve. Optional field oriented move. */
    public DriveWithJoysticks(SwerveDrivetrain drivetrain, Supplier<Double> driveSpeedX, Supplier<Double> driveSpeedY,
            Supplier<Double> turnSpeed, Supplier<Boolean> fieldOriented) {
        this.drivetrain = drivetrain;
        this.driveSpeedX = driveSpeedX;
        this.driveSpeedY = driveSpeedY;
        this.turnSpeed = turnSpeed;
        this.fieldOriented = fieldOriented;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        if (fieldOriented.get())
            drivetrain.driveChassisSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(driveSpeedY.get(), driveSpeedX.get(),
                    -turnSpeed.get(), drivetrain.getHeading()));
        else
            drivetrain.driveChassisSpeeds(new ChassisSpeeds(driveSpeedY.get(), driveSpeedX.get(), -turnSpeed.get()));
    }
}