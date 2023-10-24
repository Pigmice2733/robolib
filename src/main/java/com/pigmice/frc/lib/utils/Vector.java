package com.pigmice.frc.lib.utils;

/**
 * A type representing 2-D vectors.
 */
public class Vector implements XY {
    private final double x, y;

    /**
     * Constructs a Vector from x and y components.
     *
     * @param x the x-component of the Vector
     * @param y the y-component of the Vector
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the zero Vector, <0,0>.
     *
     * @return the zero Vector
     */
    public static Vector zero() {
        return new Vector(0, 0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if ((o == null) || (o.getClass() != this.getClass())) {
            return false;
        }
        return x == ((Vector) o).x && y == ((Vector) o).y;
    }

    @Override
    public int hashCode() {
        return (int) (3.0 + 11.0 * x + 193.0 * y);
    }

    /**
     * Scales this Vector by a scalar factor.
     *
     * @param scale the factor to scale by
     * @return the scaled Vector
     */
    public Vector scale(double scale) {
        return new Vector(x * scale, y * scale);
    }

    /**
     * Adds another Vector to this Vector.
     *
     * @param v the Vector to add to this one
     * @return the sum of the Vectors
     */
    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    /**
     * Compute the dot product of this Vector and another Vector.
     *
     * @param v the other vector
     * @return the dot product of the Vectors
     */
    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    /**
     * Computes the magnitude of this Vector.
     *
     * @return the magnitude of this Vector
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Normalizes this Vector; i.e., returns a new Vector with the same direction
     * and magnitude 1.
     *
     * @return the normalized Vector
     */
    public Vector normalize() {
        return scale(1.0 / magnitude());
    }

    /**
     * Calculates the angle of this Vector in radians. The angle is measured
     * counterclockwise from the x-axis.
     *
     * @return the angle of this Vector in radians
     */
    public double angle() {
        return Math.atan2(y, x);
    }

    /**
     * Returns this Vector rotated counterclockwise by a specified angle.
     *
     * @param angle the rotation angle in radians
     * @return the rotated Vector
     */
    public Vector rotate(double angle) {
        return new Vector(
                x * Math.cos(angle) - y * Math.sin(angle),
                x * Math.sin(angle) + y * Math.cos(angle));
    }
}
