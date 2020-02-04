package com.pigmice.frc.lib.controllers;

import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.pigmice.frc.lib.utils.Range;

public class PID implements IController {
    private final PIDGains gains;
    private final double period;

    private Range outputBounds;
    private Range inputBounds;

    private boolean continuous;
    private boolean useDerivativeOnInput;

    private double integralTerm;
    private double previousInput;
    private double previousError;

    public PID(PIDGains gains, Range outputBounds, double period) {
        this.gains = gains;
        this.period = period;
        this.outputBounds = outputBounds;

        this.integralTerm = 0.0;
        this.previousInput = 0.0;
        this.previousError = 0.0;
    }

    /**
     * Set this to true when your setpoint is not continuous This will switch from
     * using derivative of the error to derivative of the input
     */
    public void setDerivativeOnInput(boolean on) {
        this.useDerivativeOnInput = on;
    }

    /**
     * Allow error to wrap from one end of the input to another
     */
    public void setContinuous(Range inputBounds, boolean continuous) {
        this.continuous = continuous;
        this.inputBounds = inputBounds;
    }

    public void initialize(double input, double currentOutput) {
        integralTerm = currentOutput;
        previousInput = input;
        previousError = 0;
    }

    public double calculateOutput(double input, ISetpoint setpoint) {
        double error = setpoint.getPosition() - input;
        if (continuous) {
            error = calculateContinuousError(error);
        }

        integralTerm += gains.kI() * error * period;

        double derivative;
        if (useDerivativeOnInput) {
            double deltaInput = calculateContinuousError(input - previousInput);
            derivative = -deltaInput / period;
        } else {
            derivative = (error - previousError) / period;
        }

        previousInput = input;
        previousError = error;

        double feedback = gains.kP() * error + integralTerm + gains.kD() * derivative;
        double feedforward = gains.kF() * setpoint.getPosition() +
                             gains.kV() * setpoint.getVelocity() +
                             gains.kA() * setpoint.getAcceleration();

        double output = feedforward + feedback;

        // Prevent integral windup and lag
        adjustIntegralTerm(output);

        return outputBounds.clamp(output);
    }

    private double calculateContinuousError(double error) {
        if (Math.abs(error) > inputBounds.size() / 2.0) {
            return error - Math.signum(error) * inputBounds.size();
        }
        return error;
    }

    // If integral term and output are outside of outputBounds, adjust integral
    // term so output is within bounds, and clamp integral within outputBounds.
    private void adjustIntegralTerm(double output) {
        if (output > outputBounds.max() && integralTerm > outputBounds.max()) {
            integralTerm -= output - outputBounds.max();
        } else if (output < outputBounds.min() && integralTerm < outputBounds.min()) {
            integralTerm += outputBounds.min() - output;
        }
        integralTerm = outputBounds.clamp(integralTerm);
    }
}
