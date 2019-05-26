package com.pigmice.frc.lib.pidf;

import com.pigmice.frc.lib.utils.Range;

public class PIDF {
    private Gains gains;

    private Range outputBounds;
    private Range inputBounds;

    private boolean continuous;
    private boolean useDerivativeOnInput;

    private double integralTerm;
    private double previousTime;
    private double previousInput;
    private double previousError;

    private double previousDerivative;

    public PIDF(Gains gains, Range outputBounds) {
        this.gains = gains;
        this.outputBounds = outputBounds;

        this.integralTerm = 0.0;
        this.previousTime = 0.0;
        this.previousInput = 0.0;
        this.previousError = 0.0;

        this.previousDerivative = 0.0;
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

    public void initialize(double input, double time, double currentOutput) {
        integralTerm = currentOutput;
        previousTime = time;
        previousInput = input;
        previousError = 0;

        previousDerivative = 0;
    }

    public double calculateOutput(double input, double setpoint, double time) {
        return calculateOutput(input, setpoint, 0.0, 0.0, time);
    }

    public double calculateOutput(double input, double setpoint, double velocity, double acceleration, double time) {
        double error = setpoint - input;
        if (continuous) {
            error = calculateContinuousError(error);
        }

        double deltaTime = time - previousTime;

        integralTerm += gains.kI() * error * deltaTime;

        double derivative;
        if (deltaTime > 0.0) {
            if (useDerivativeOnInput) {
                double deltaInput = calculateContinuousError(input - previousInput);
                derivative = -deltaInput / deltaTime;
            } else {
                derivative = (error - previousError) / deltaTime;
            }
        } else {
            derivative = previousDerivative;
        }

        previousInput = input;
        previousError = error;
        previousTime = time;
        previousDerivative = derivative;

        double feedback = gains.kP() * error + integralTerm + gains.kD() * derivative;
        double feedforward = gains.kF() * setpoint + gains.kV() * velocity + gains.kA() * acceleration;

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
