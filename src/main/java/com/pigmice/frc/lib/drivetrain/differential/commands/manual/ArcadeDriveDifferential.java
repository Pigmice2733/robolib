package com.pigmice.frc.lib.drivetrain.differential.commands.manual;

import java.util.function.DoubleSupplier;

import com.pigmice.frc.lib.drivetrain.differential.DifferentialDrivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArcadeDriveDifferential extends CommandBase {
    private DifferentialDrivetrain drivetrain;
    private DoubleSupplier forward, rotate;

    /**
     * Drive according to an arcade-style driving system.
     * 
     * @param drivetrain a drivetrain subsystem
     * @param forward    the speed to move forward at
     * @param rotate     the rotational speed to rotate at
     */
    public ArcadeDriveDifferential(DifferentialDrivetrain drivetrain, DoubleSupplier forward,
            DoubleSupplier rotate) {
        this.drivetrain = drivetrain;
        this.forward = forward;
        this.rotate = rotate;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        drivetrain.arcadeDrive(forward.getAsDouble(), rotate.getAsDouble());
    }
}
