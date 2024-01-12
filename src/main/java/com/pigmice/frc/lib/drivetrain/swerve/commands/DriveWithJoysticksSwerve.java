// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package com.pigmice.frc.lib.drivetrain.swerve.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.pigmice.frc.lib.drivetrain.swerve.SwerveDrivetrain;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import swervelib.SwerveDrive;

public class DriveWithJoysticksSwerve extends CommandBase {
    private final SwerveDrive drivetrain;
    private final DoubleSupplier driveSpeedX, driveSpeedY, turnSpeed;
    private final BooleanSupplier fieldOriented;

    /** The default drive command for swerve. Optional field-oriented input. */
    public DriveWithJoysticksSwerve(
            SwerveDrive drivetrain, DoubleSupplier driveSpeedX, DoubleSupplier driveSpeedY,
            DoubleSupplier turnSpeed, BooleanSupplier fieldOriented) {
        this.drivetrain = drivetrain;
        this.driveSpeedX = driveSpeedX;
        this.driveSpeedY = driveSpeedY;
        this.turnSpeed = turnSpeed;
        this.fieldOriented = fieldOriented;

        // addRequirements(drivetrain);
    }

    /** The default drive command for swerve. */
    public DriveWithJoysticksSwerve(SwerveDrive drivetrain, DoubleSupplier driveSpeedX,
            DoubleSupplier driveSpeedY, DoubleSupplier turnSpeed) {
        this(drivetrain, driveSpeedX, driveSpeedY, turnSpeed, () -> false);
    }

    @Override
    public void execute() {
        if (fieldOriented.getAsBoolean())

            drivetrain.driveFieldOriented(ChassisSpeeds.fromFieldRelativeSpeeds(
                    driveSpeedY.getAsDouble(), driveSpeedX.getAsDouble(),
                    -turnSpeed.getAsDouble(), drivetrain.getYaw()));
        else
            drivetrain.drive(new ChassisSpeeds(
                    driveSpeedY.getAsDouble(), driveSpeedX.getAsDouble(),
                    -turnSpeed.getAsDouble()));
    }
}