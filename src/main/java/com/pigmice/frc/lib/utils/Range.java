package com.pigmice.frc.lib.utils;

/**
 * Range is a representation of a range of numbers, with upper and lower bounds.
 */
public class Range {
    private final double max, min;

    /**
     * Create a Range with given upper and a lower bounds
     *
     * @param min Lower bound of range
     * @param max Upper bound of range
     */
    public Range(double min, double max) {
        this.max = Math.max(min, max);
        this.min = Math.min(min, max);
    }

    /**
     * Create a Range with no bounds
     *
     * @return A Range containing all possible doubles
     */
    public static Range noBounds() {
        return new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Create a Range from 0.0 to 1.0
     *
     * @return A Range containing zero to one
     */
    public static Range natural() {
        return new Range(0.0, 1.0);
    }

    public double max() {
        return max;
    }

    public double min() {
        return min;
    }

    /**
     * Gets the size of this Range, which is defined as the upper bound minus the
     * lower bound.
     *
     * @return The size of this range
     */
    public double size() {
        return max - min;
    }

    /**
     * Clamps a value with this Range. If the value is already within the Range it
     * is returned unchanged, if it is larger than the upper bound then the upper
     * bound is returned, if it is smaller than the lower bound than the lower bound
     * is returned.
     *
     * @param value The value to be clamped
     * @return The result of clamping the value
     */
    public double clamp(double value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Determines whether this Range overlaps with another Range. If the two Ranges
     * share any values, including the upper and lower bounds, they are considered
     * to be overlapping.
     *
     * @param other The Range to check for overlap with
     * @return <code>True</code> if there is overlap, <code>False</code> otherwise
     */
    public boolean overlaps(Range other) {
        return min <= other.max() && other.min() <= max;
    }

    /**
     * Determines whether a value is contained within this Range. If the value is
     * between, or equal to, the upper and lower bounds it is considered to be
     * contained within this Range.
     *
     * @param value The number to determine whther this Range contains it
     * @return <code>True</code> if the value is contained within this Range,
     *         <code>False</code> otherwise
     */
    public boolean contains(double value) {
        return min <= value && value <= max;
    }
}
