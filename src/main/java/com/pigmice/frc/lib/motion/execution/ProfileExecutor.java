package com.pigmice.frc.lib.motion.execution;

import com.pigmice.frc.lib.controllers.IController;
import com.pigmice.frc.lib.motion.profile.IProfile;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;

import edu.wpi.first.wpilibj.Timer;

public class ProfileExecutor {
    public interface Output {
        void set(double output);
    }

    public interface Input {
        double get();
    }

    private final IProfile profile;
    private final IController controller;

    private final Output output;
    private final Input input;

    private final double allowableError;
    private final double finalTarget;

    private double startTime;

    public ProfileExecutor(IProfile profile, IController controller, Output output, Input input, double allowableError) {
        this.profile = profile;
        this.controller = controller;

        this.output = output;
        this.input = input;

        this.allowableError = allowableError;

        finalTarget = profile.getPosition(profile.getDuration());
    }

    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        profile.reset();
        controller.initialize(input.get(), 0.0);
    }

    /**
     * Returns whether the error is within the allowable error, and outputs the
     * current setpoint
     */
    public boolean update() {
        double time = Timer.getFPGATimestamp() - startTime;
        ISetpoint sp = profile.getSetpoint(time);

        double currentInput = input.get();

        double outputDemand = controller.calculateOutput(currentInput, sp);

        output.set(outputDemand);

        return Math.abs(finalTarget - currentInput) <= allowableError;
    }
}
