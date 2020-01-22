package com.pigmice.frc.lib.utils;

import java.util.ArrayList;
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
        int lowIndex = 0;
        int highIndex = data.size() - 1;

        return _binarySearch(data, target, lowIndex, highIndex);
    }

    private static int _binarySearch(ArrayList<Double> data, double target, int lowIndex, int highIndex) {
        if (highIndex - lowIndex == 1) {
            return lowIndex;
        }

        int mid = (lowIndex + highIndex) / 2;

        if (data.get(mid) < target) {
            return _binarySearch(data, target, mid, highIndex);
        } else {
            return _binarySearch(data, target, lowIndex, mid);
        }
    }

    public static List<Point> circleLineIntersections(Point lineStart, Point lineEnd, Point circleCenter, double radius) {
        Vector lineDelta = lineEnd.subtract(lineStart);
        double A = lineDelta.dot(lineDelta);

        Vector dist = lineStart.subtract(circleCenter);
        double B = 2 * lineDelta.dot(dist);

        double C = dist.dot(dist) - (radius * radius);

        double discriminant = B*B - 4*A*C;

        if (discriminant < 0.0) {
            return new ArrayList<Point>();
        }

        double sqrtdiscr = Math.sqrt(discriminant);

        double t0 = (-B - sqrtdiscr) / (2 * A);
        double t1 = (-B + sqrtdiscr) / (2 * A);

        Point p0 = lineStart.translate(lineDelta.scale(t0));
        Point p1 = lineStart.translate(lineDelta.scale(t1));

        return List.of(p0, p1);
    }
}
