package com.pigmice.frc.lib.utils;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Odometry tracking and odometry data streaming
 */
public class Odometry {
    /**
     * Odometry data streaming over network tables. Stores x, y position and angle.
     */
    public static class Streamer {
        private NetworkTableEntry xEntry;
        private NetworkTableEntry yEntry;
        private NetworkTableEntry angleEntry;

        private double x, y, angle;

        /**
         * Creates a new odometry data streamer for a specific table. All data default
         * to 0.0
         *
         * @param table The table to add the data to. Can be a subtable if given as
         *              follows: '/table/subtable'.
         */
        public Streamer(String table) {
            NetworkTableInstance root = NetworkTableInstance.getDefault();
            NetworkTable baseTable = root.getTable(table);
            xEntry = baseTable.getEntry("x");
            yEntry = baseTable.getEntry("y");
            angleEntry = baseTable.getEntry("angle");

            xEntry.setDouble(0.0);
            yEntry.setDouble(0.0);
            angleEntry.setDouble(0.0);
        }

        /**
         * Stream new data. If the data are the same as the last-sent data,
         * NetworkTables will not be updated
         *
         * @param x     The robot's x position
         * @param y     The robot's y position
         * @param angle The robot's angle
         */
        public void send(double x, double y, double angle) {
            if (Math.abs(this.x - x) > 1e-6) {
                xEntry.setDouble(x);
            }

            if (Math.abs(this.y - y) > 1e-6) {
                yEntry.setDouble(y);
            }

            if (Math.abs(this.angle - angle) > 1e-6) {
                angleEntry.setDouble(angle);
            }
        }
    }

    private double x, y;

    private double lastLeft, lastRight;
    private double lastAngle;

    /**
     * Get the x,y position of the robot
     *
     * @return The position of the robot as a Cartesian point
     */
    public Point getPosition() {
        return new Point(x, y);
    }

    /**
     * Get the robot's angle, counter-clockwise from x axis in radians
     *
     * @return The most recently recorded angle
     */
    public double getAngle() {
        return lastAngle;
    }

    /**
     * Create an odometry tracker with a specific starting position and angle
     *
     * @param x     Starting x position
     * @param y     Starting y position
     * @param angle Starting angle, counter-clockwise from x axis in radians
     */
    public Odometry(double x, double y, double angle) {
        lastLeft = 0;
        lastRight = 0;
        lastAngle = angle;
        this.x = x;
        this.y = y;
    }

    /**
     * Update odometry data, given new distances for each side of the drivetrain and
     * the current angle
     *
     * @param leftPosition  Total distance left side of the drivetrain has driven
     * @param rightPosition Total distance right side of the drivetrain has driven
     * @param angle         Current angle, counter-clockwise from x axis in radians
     */
    public void update(double leftPosition, double rightPosition, double angle) {
        double deltaLeft = leftPosition - lastLeft;
        double deltaRight = rightPosition - lastRight;
        double deltaAngle = (angle - lastAngle) * (Math.PI / 180.0);

        double distance = (deltaLeft + deltaRight) / 2.0;
        if (Math.abs(deltaAngle) > Math.PI) {
            deltaAngle = Math.copySign(2.0 * Math.PI - Math.abs(deltaAngle), -deltaAngle);
        }

        if (Math.abs(deltaAngle) < 1e-6) {
            double deltaX = distance * Math.cos(angle);
            double deltaY = distance * Math.sin(angle);
            x += deltaX;
            y += deltaY;

            lastAngle = angle;

            return;
        }

        double radius = distance / Math.abs(deltaAngle);
        double chordAngle = lastAngle + deltaAngle / 2.0;
        double chordLength = 2.0 * radius * Math.sin(Math.abs(deltaAngle) / 2.0);

        double deltaX = chordLength * Math.cos(chordAngle);
        double deltaY = chordLength * Math.sin(chordAngle);

        x += deltaX;
        y += deltaY;

        lastAngle = angle;

        return;
    }

    /**
     * Set odometry data to a specific position and angle, and set new total
     * distances driven for the drivetrain. Should be used whenever drivetrain
     * encoders are zeroed (or otherwise set to new values) to update drivetrain
     * distances.
     *
     * @param x             The new x coordinate of the position
     * @param y             The new y coordinate of the position
     * @param angle         The new angle, counter-clockwise from x axis in radians
     * @param leftPosition  The new total distance the left side of the drivetrain
     *                      has driven
     * @param rightPosition The new total distance the right side of the drivetrain
     *                      has driven
     */
    public void set(double x, double y, double angle, double leftPosition, double rightPosition) {
        this.x = x;
        this.y = y;
        this.lastAngle = angle;
        this.lastLeft = leftPosition;
        this.lastRight = rightPosition;
    }
}
