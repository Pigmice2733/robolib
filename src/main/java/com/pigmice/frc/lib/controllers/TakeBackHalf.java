package com.pigmice.frc.lib.controllers;

import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.pigmice.frc.lib.utils.Range;

/**
 * Implementation of robust and simple to tune "Take Back Half" controller,
 * controller designed by W. Steven Woodward. Velocity controller.
 */
public class TakeBackHalf implements IController {
    private double gain;
    private double currentOutput;
    private double previousError;
    private double takeBackHalf;

    // Approximation of output needed to remain at setpoint
    // Used for selecting optimum initial TBH value
    private double targetOutput;

    private final Range outputBounds = new Range(-1.0, 1.0);

    public TakeBackHalf(double gain, double targetOutput) {
        this.gain = gain;
        currentOutput = 0;
        previousError = 1;
        this.targetOutput = targetOutput;
        takeBackHalf = 2*targetOutput - 1;
    }

    public void updateTargetOutput(double targetOutput) {
        this.targetOutput = targetOutput;
    }

    public void initialize(double input, double currentOutput) {
        this.currentOutput = currentOutput;
        previousError = 1;
        takeBackHalf = 2*targetOutput - 1;
    }

    public double calculateOutput(double input, ISetpoint setpoint) {
        double error = setpoint.getVelocity() - input;

        if (Math.signum(previousError) != Math.signum(error)) {
            currentOutput = 0.5 * (currentOutput + takeBackHalf);
            takeBackHalf = currentOutput;
        } else {
            currentOutput += error * gain;
        }

        previousError = error;

        currentOutput = outputBounds.clamp(currentOutput);

        return currentOutput;
    }
}
