package com.pigmice.frc.lib.controllers;

import com.pigmice.frc.lib.utils.Range;

public class TakeBackHalf implements IController {
    private double gain;
    private double currentOutput;
    private double previousError;
    private double takeBackHalf;

    private final Range outputBounds = new Range(-1.0, 1.0);

    public TakeBackHalf(double gain) {
        this.gain = gain;
        currentOutput = 0;
        previousError = 1;
        takeBackHalf = 0;
    }

    public void initialize(double input, double currentOutput) {
        this.currentOutput = currentOutput;
        previousError = 1;
    }

    public double calculateOutput(double input, double setpoint) {
        double error = setpoint - input;
        currentOutput += error * gain;

        if (Math.signum(previousError) != Math.signum(error)) {
            currentOutput = 0.5 * (currentOutput + takeBackHalf);
            takeBackHalf = currentOutput;
        }

        previousError = error;

        currentOutput = outputBounds.clamp(currentOutput);

        return currentOutput;
    }
}
