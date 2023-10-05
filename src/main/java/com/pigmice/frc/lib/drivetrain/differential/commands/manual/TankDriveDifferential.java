package com.pigmice.frc.lib.drivetrain.differential.commands.manual;

import java.util.function.DoubleSupplier;

import com.pigmice.frc.lib.drivetrain.differential.DifferentialDrivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TankDriveDifferential extends CommandBase {
    private final DifferentialDrivetrain drivetrain;
    private final DoubleSupplier left, right;

    /**
     * Drive according to an tank-drive system.
     * 
     * @param drivetrain a drivetrain subsystem
     * @param left       the speed to move the left wheels at
     * @param right      the speed to move the right wheels at
     */
    public TankDriveDifferential(DifferentialDrivetrain drivetrain, DoubleSupplier left, DoubleSupplier right) {
        this.drivetrain = drivetrain;
        this.left = left;
        this.right = right;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        drivetrain.setPercentOutputs(left.getAsDouble(), right.getAsDouble());
    }
}
