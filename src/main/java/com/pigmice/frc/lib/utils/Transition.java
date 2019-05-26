package com.pigmice.frc.lib.utils;

/**
 * Describes a transition in position/velocity/etc over a set period of time
 */
public class Transition {
    private double start;
    private double rate;
    private Range bounds;

    /**
     * Creates a Transition between two values over a set time period
     *
     * @param start    Start value for the Transition
     * @param end      End value for the Transition
     * @param duration Duration of the Transition
     */
    public Transition(double start, double end, double duration) {
        this.start = start;
        this.rate = (end - start) / duration;
        bounds = new Range(0.0, duration);
    }

    /**
     * Creates a 0.0 to 0.0 Transition with duration 1.0
     *
     * @return A zero Transition
     */
    public static Transition zero() {
        return new Transition(0.0, 0.0, 1.0);
    }

    /**
     * Gets the value at the specified time into this Transition
     *
     * @param time How far into this Transition to get the value at
     * @return The value of this Transition at the given time
     */
    public double get(double time) {
        time = bounds.clamp(time);
        return start + rate * time;
    }

    /**
     * Integrates the value of this Transition from the beginning to the specified
     * time
     *
     * @param time How far into this Transition to integrate to
     * @return The integral of this Transition
     */
    public double integrate(double time) {
        time = bounds.clamp(time);
        return 0.5 * (start + get(time)) * time;
    }

    /**
     * Gets the rate of change of the value for this Transition
     *
     * @return Transition rate
     */
    public double getRate() {
        return rate;
    }
}
