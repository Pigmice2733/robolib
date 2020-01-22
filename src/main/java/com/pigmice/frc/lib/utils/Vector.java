package com.pigmice.frc.lib.utils;

/**
 * A 2D vector data type
 */
public class Vector implements XY {
    private final double x, y;

    /**
     * Constructs a Vector from x and y components.
     *
     * @param x The x component of the Vector
     * @param y The y component of the Vector
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the zero Vector, which has no length or direction and each component
     * is zero.
     *
     * @return The zero Vector
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
        Vector other = (Vector) o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return (int) (3.0 + 11.0 * x + 193.0 * y);
    }

    /**
     * Scales this Vector by a scalar factor.
     *
     * @param scale The factor to scale by
     * @return The scaled Vector
     */
    public Vector scale(double scale) {
        return new Vector(x * scale, y * scale);
    }

    /**
     * Adds another Vector to this Vector
     *
     * @param v The Vector to add to this one
     * @return The sum of the Vectors
     */
    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    /**
     * Compute the dot product of this Vector and another Vector
     *
     * @param v The other vector
     * @return The dot product of the Vectors
     */
    public double dot(Vector v) {
        return x*v.x + y*v.y;
    }

    /**
     * Computes the magnitude of this Vector.
     *
     * @return The magnitude of this Vector
     */
    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Calculates the angle of this Vector in radians. The angle is measured
     * counterclockwise from the x axis.
     *
     * @return The angle of this Vector in radians
     */
    public double getAngle() {
        return Math.atan2(y, x);
    }

    /**
     * Returns this Vector rotated counterclockwise by a specified angle.
     *
     * @param angle The rotation angle in radians
     * @return The rotated Vector
     */
    public Vector rotate(double angle) {
        double rotatedX = x * Math.cos(angle) - y * Math.sin(angle);
        double rotatedY = x * Math.sin(angle) + y * Math.cos(angle);
        return new Vector(rotatedX, rotatedY);
    }
}
