package com.pigmice.frc.lib.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utils hold utility functions that don't belong as their own classes
 */
public class Utils {

    /**
     * lerp (linear interpolation) maps numbers to other numbers via a linear
     * function. Given a line defined by two points, lerp caculates the y value of
     * the line at a given x value. The x and y values are not constrained to be
     * within the given points, so this function can also perform linear
     * extrapolation.
     *
     * @param val The x value to map
     * @param x0  The x coordinate of the first point, or the minimum input value
     * @param x1  The x coordinate of the second point, or the maximum input value
     * @param y0  The y value of the first point, or the minimum output value
     * @param y1  The y value of the second point, or the maximum output value
     * @return The y value the specified x value maps onto
     * @see <a href="https://en.wikipedia.org/wiki/Linear_interpolation">Linear
     *      interpolation on Wikipedia</a>
     */
    public static double lerp(double val, double x0, double x1, double y0, double y1) {
        final double weight = (val - x0) / (x1 - x0);
        return weight * (y1 - y0) + y0;
    }

    /**
     * lerp (linear interpolation) maps numbers to other numbers via a linear
     * function. Given a line defined by two points, lerp caculates the y value of
     * the line at a given x value. The x and y values are not constrained to be
     * within the given points, so this function can also perform linear
     * extrapolation.
     *
     * @param a The first value, or the minimum input value
     * @param b The second value, or the maximum output value
     * @param t The value between 0 and 1 to sample
     * @return The y value the specified x value maps onto
     */
    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    /**
     * Checks whether two doubles are within <code>1e-6</code> of each other. This
     * is useful for cases where floating point rounding and errors may build up,
     * but it is still desirable to check values for equality.
     *
     * @param a The first double
     * @param b The second double
     * @return <code>True</code> if the values are very close, <code>False</code>
     *         otherwise
     * @see #almostEquals(double, double, double)
     */
    public static boolean almostEquals(double a, double b) {
        return almostEquals(a, b, 1e-6);
    }

    /**
     * Checks whether two doubles are within a specified epsilon of each other. This
     * is useful for cases where floating point rounding and errors may build up,
     * but it is still desirable to check values for equality.
     *
     * @param a The first double
     * @param b The second double
     * @return <code>True</code> if the difference between the values is less than
     *         epsilon, <code>False</code> if it is greater than or equal to epsilon
     * @see #almostEquals(double, double)
     */
    public static boolean almostEquals(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    /**
     * Performs binary search on a <strong>sorted</strong> array of numbers,
     * returning the largest index such that the number at that index is smaller
     * than a target value.
     *
     * @param data   The array of numbers to search for
     * @param target The target value
     * @return The index of the largest number smaller than the target
     */
    public static int binarySearch(ArrayList<Double> data, double target) {
        return _binarySearch(data, target, 0, data.size() - 1);
    }

    private static int _binarySearch(ArrayList<Double> data, double target, int lowIndex, int highIndex) {
        if (highIndex - lowIndex == 1) {
            return lowIndex;
        }

        final int mid = (lowIndex + highIndex) / 2;

        if (data.get(mid) < target) {
            return _binarySearch(data, target, mid, highIndex);
        } else {
            return _binarySearch(data, target, lowIndex, mid);
        }
    }

    /**
     * Finds the locations of intersections between a circle and an infinite line
     * defined by a Point and a Vector. The locations are given as a fraction
     * of the Vector, starting at <code>lineStart</code>.
     *
     * For an intersection specified by the Double <code>x</code>, the
     * intersection Point is equal to <code>lineStart + x*lineDirection</code>.
     *
     * @param lineStart     A Point on the line
     * @param lineDirection Vector direction of the line
     * @param circleCenter  The center of the circle
     * @param radius        The radius of the circle
     * @return A list of all the intersection locations, given as described above
     */
    public static List<Double> circleLineIntersections(Point lineStart, Vector lineDirection, Point circleCenter,
            double radius) {
        final double A = lineDirection.dot(lineDirection);

        final Vector dist = lineStart.subtract(circleCenter);
        final double B = 2 * lineDirection.dot(dist);

        final double C = dist.dot(dist) - (radius * radius);

        final double discriminant = B * B - 4 * A * C;

        if (discriminant < 0.0) {
            return new ArrayList<>();
        }

        final double sqrtdiscr = Math.sqrt(discriminant);

        final double t0 = (-B - sqrtdiscr) / (2 * A);
        final double t1 = (-B + sqrtdiscr) / (2 * A);

        return new ArrayList<>(Arrays.asList(t0, t1));
    }

    /**
     * Projects a Point onto a line segment (defined by a Point and a Vector),
     * clamping the projected Point within the segment. The location of the
     * projected Point is given as a fraction of the line Vector starting at
     * <code>start</code>.
     *
     * @param point     The Point to project
     * @param start     The start of the line segment
     * @param direction Direction and length of the line
     * @return The projected point, specified as described above
     */
    public static double project(Point point, Point start, Vector direction) {
        Vector toStart = point.subtract(start);
        double t = toStart.dot(direction) / direction.dot(direction);

        return Range.natural().clamp(t);
    }

    /**
     * Provides a software stop on a mechanism with given bounds by checking that a
     * given output will not go past the bounds.
     * 
     * @param position the current position/rotation of the mechanism
     * @param speed    the provisional output for the mechanism
     * @param limit    the maximum position (in either direction) that the mechanism
     *                 should go
     * @return an appropriate output for the speed--the given value if possible or 0
     *         otherwise
     */
    public static double applySoftwareStop(double position, double speed, double limit) {
        if (position >= limit && speed > 0) {
            return 0;
        } else if (position <= -limit && speed < 0) {
            return 0;
        } else {
            return speed;
        }
    }
}
