package com.pigmice.frc.lib.utils;

import com.pigmice.frc.lib.utils.Odometry.Pose;

/**
 * A Point represents a point in some 2D space
 */
public class Point implements XY {
    private final double x, y;

    /**
     * Constructs a Point from x, y coordinates
     *
     * @param x The x coordinate of the Point
     * @param y The y coordinate of the Point
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Point from an XY object
     *
     * @param xy The XY object to copy from
     */
    public Point(XY xy) {
        this.x = xy.getX();
        this.y = xy.getY();
    }

    /**
     * Returns the origin (0, 0) Point, where each component
     * is zero
     *
     * @return The origin
     */
    public static Point origin() {
        return new Point(0, 0);
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

        Point other = (Point) o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return (int) (7.0 + 3.0 * x + 227.0 * y);
    }

    /**
     * Finds the {@link Vector Vector} offset between this Point and another
     *
     * @param p The Point to subtract from this one
     * @return The Vector offset between the Points
     */
    public Vector subtract(Point p) {
        return new Vector(x - p.x, y - p.y);
    }

    /**
     * Translates this Point by a {@link Vector Vector}
     *
     * @param translation The Vector to add to this Point
     * @return The translated Vector
     */
    public Point translate(Vector translation) {
        return new Point(x + translation.getX(), y + translation.getY());
    }

    /**
     * Rotates this Point by a specific angle
     *
     * @param angle  The angle in radians to rotate by
     * @param center The center of rotation
     * @return The rotated Point
     */
    public Point rotate(double angle, Point center) {
        Vector offset = subtract(center);
        Vector rotated = offset.rotate(angle);
        return center.translate(rotated);
    }

    /**
     * Transform this Point to be relative to a specific Pose
     *
     * @param pose The Pose that will effectively become the origin
     * @return The transformed Point
     */
    public Point relativeTo(Pose pose) {
        Vector translation = this.subtract(new Point(pose));
        Vector transformed = translation.rotate(0.5*Math.PI - pose.heading);

        return new Point(transformed);
    }
}
