package com.pigmice.frc.lib.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for useful mathematical functions and other utilities.
 */
public class Utils {

    /**
     * lerp (linear interpolation) maps numbers to other numbers via a linear
     * function. Given a line defined by two points, lerp caculates the y-value of
     * the line at a given x-value. The x- and y-values are not constrained to be
     * within the given points, so this function can also perform linear
     * extrapolation.
     *
     * @param val The x-value to map
     * @param x0  The x-coordinate of the first point, or the minimum input value
     * @param x1  The x-coordinate of the second point, or the maximum input value
     * @param y0  The y-value of the first point, or the minimum output value
     * @param y1  The y-value of the second point, or the maximum output value
     * @return The y-value the specified x-value maps onto
     * @see <a href="https://en.wikipedia.org/wiki/Linear_interpolation">Linear
     *      interpolation on Wikipedia</a>
     */
    public static double lerp(double val, double x0, double x1, double y0, double y1) {
        return (val - x0) * (y1 - y0) / (x1 - x0) + y0;
    }

    /**
     * lerp (linear interpolation) maps numbers to other numbers via a linear
     * function. Given a line defined by two points, lerp calculates the y-value of
     * the line at a given x-value. The x- and y-values are not constrained to be
     * within the given points, so this function can also perform linear
     * extrapolation.
     *
     * @param a the first value, or the minimum input value
     * @param b the second value, or the maximum output value
     * @param t the x-value, as a fraction of the range, to sample
     * @return the y-value the specified x-value maps onto
     */
    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    /**
     * Checks whether two doubles are within <code>1e-6</code> of each other. This
     * is useful for cases where floating-point rounding and errors may build up,
     * but it is still desirable to check values for equality.
     *
     * @param a the first double
     * @param b the second double
     * @return <code>True</code> if the values are very close, <code>False</code>
     *         otherwise
     * @see #almostEquals(double, double, double)
     */
    public static boolean almostEquals(double a, double b) {
        return almostEquals(a, b, 1e-6);
    }

    /**
     * Checks whether two doubles are within a specified difference. This
     * is useful for cases where floating-point rounding and errors may build up,
     * but it is still desirable to check values for equality.
     *
     * @param a the first double
     * @param b the second double
     * @return <code>True</code> if the difference between the values is less than
     *         <code>epsilon</code>, <code>False</code> if it is greater than or
     *         equal to <code>epsilon</code>
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
     * @param data   the array of numbers to search
     * @param target the target value
     * @return the index of the largest number smaller than the target
     */
    public static int binarySearch(ArrayList<Double> data, double target) {
        return _binarySearch(data, target, 0, data.size() - 1);
    }

    private static int mid;

    private static int _binarySearch(ArrayList<Double> data, double target, int lowIndex, int highIndex) {
        if (highIndex - lowIndex == 1) {
            return lowIndex;
        }

        mid = (lowIndex + highIndex) / 2;

        if (data.get(mid) < target) {
            return _binarySearch(data, target, mid, highIndex);
        } else {
            return _binarySearch(data, target, lowIndex, mid);
        }
    }

    private static double a, b, discriminant, t0, t1;
    private static Vector dist;

    /**
     * Finds the point(s) of intersection between a circle and an infinite line
     * defined by a Point and a Vector. The locations are given as fractional
     * multiples of the Vector, starting at <code>lineStart</code>. That is, for an
     * intersection specified by the output <code>x</code>, the intersection Point
     * is <code> lineStart + x * lineDirection</code>.
     *
     * @param lineStart     a Point on the line
     * @param lineDirection vector direction of the line
     * @param circleCenter  the center of the circle
     * @param radius        the radius of the circle
     * @return a list of all the intersection locations, given as described above
     */
    public static List<Double> circleLineIntersections(Point lineStart, Vector lineDirection, Point circleCenter,
            double radius) {
        a = lineDirection.dot(lineDirection);
        b = 2 * lineDirection.dot(dist);
        dist = lineStart.subtract(circleCenter);

        discriminant = b * b - 4 * a * (dist.dot(dist) - (radius * radius));

        if (discriminant < 0.0) {
            return new ArrayList<>();
        }

        t0 = (-b - Math.sqrt(discriminant)) / (2 * a);
        t1 = (-b + Math.sqrt(discriminant)) / (2 * a);

        return new ArrayList<>(Arrays.asList(t0, t1));
    }

    private static double t;

    /**
     * Projects a Point onto a line segment (defined by a Point and a Vector),
     * clamping the projected Point within the segment. The location of the
     * projected Point is given as a fraction of the line Vector starting at
     * <code>start</code>.
     *
     * @param point     the Point to project
     * @param start     the start of the line segment
     * @param direction direction and length of the line
     * @return the projected point, specified as described above
     */
    public static double project(Point point, Point start, Vector direction) {
        t = point.subtract(start).dot(direction) / direction.dot(direction);
        return Range.natural().clamp(t);
    }
}
