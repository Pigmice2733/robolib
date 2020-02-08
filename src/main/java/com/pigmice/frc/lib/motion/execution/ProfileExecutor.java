package com.pigmice.frc.lib.motion.execution;

import com.pigmice.frc.lib.controllers.IController;
import com.pigmice.frc.lib.motion.profile.IProfile;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;

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
    private final double maxFinalSpeed;
    private final double finalTarget;

    private final Input timeInput;
    private double startTime;
    private double previousInput;
    private double previousTime;

    public ProfileExecutor(IProfile profile, IController controller, Output output, Input input, double allowableError, double maxFinalSpeed, Input timeInput) {
        this.profile = profile;
        this.controller = controller;

        this.output = output;
        this.input = input;

        this.allowableError = allowableError;
        this.maxFinalSpeed = maxFinalSpeed;

        this.timeInput = timeInput;
        this.previousInput = 0.0;
        this.previousTime = 0.0;

        finalTarget = profile.getPosition(profile.getDuration());
    }

    public void initialize() {
        startTime = timeInput.get();
        profile.reset();
        controller.initialize(input.get(), 0.0);
    }

    /**
     * Returns whether the error is within the allowable error and below the max speed,
     * and outputs the current setpoint
     */
    public boolean update() {
        double time = timeInput.get() - startTime;
        ISetpoint sp = profile.getSetpoint(time);

        double currentInput = input.get();

        double outputDemand = controller.calculateOutput(currentInput, sp);

        output.set(outputDemand);

        double speed = Math.abs((currentInput - previousInput)/(time - previousTime));

        previousInput = currentInput;
        previousTime = time;

        return (Math.abs(finalTarget - currentInput) <= allowableError) && (speed <= maxFinalSpeed);
    }
}
