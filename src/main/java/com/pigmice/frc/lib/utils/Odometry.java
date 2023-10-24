package com.pigmice.frc.lib.utils;

/**
 * Odometry tracking
 */
public class Odometry {
    public static class Pose implements XY {
        protected double x, y;
        protected double heading;

        public Pose(double x, double y, double heading) {
            this.x = x;
            this.y = y;
            this.heading = heading;
        }

        public double getHeading() {
            return heading;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    private Pose pose;
    private double lastLeft, lastRight;
    private double deltaLeft, deltaRight, deltaAngle, distance, radius, chordAngle, chordLength;

    /**
     * Get the pose of the robot
     *
     * @return The position and heading of the robot as a Pose
     */
    public Pose getPose() {
        return pose;
    }

    /**
     * Create an odometry tracker with a specific starting position and angle
     *
     * @param initialPose Starting pose of the robot
     */
    public Odometry(Pose initialPose) {
        lastLeft = 0.0;
        lastRight = 0.0;
        pose = initialPose;
        deltaAngle = deltaLeft = deltaRight = distance = radius = chordAngle = chordLength = 0;
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
        deltaLeft = leftPosition - lastLeft;
        deltaRight = rightPosition - lastRight;
        deltaAngle = angle - pose.heading;

        lastLeft = leftPosition;
        lastRight = rightPosition;

        distance = (deltaLeft + deltaRight) / 2.0;

        if (Math.abs(deltaAngle) > Math.PI) {
            deltaAngle = Math.copySign(2.0 * Math.PI - Math.abs(deltaAngle), -deltaAngle);
        }

        if (Math.abs(deltaAngle) < 1e-6) {
            pose.x += distance * Math.cos(angle);
            pose.y += distance * Math.sin(angle);
            pose.heading = angle;

            return;
        }

        radius = distance / Math.abs(deltaAngle);
        chordAngle = pose.heading + deltaAngle / 2.0;
        chordLength = 2.0 * radius * Math.sin(Math.abs(deltaAngle) / 2.0);

        pose.x += chordLength * Math.cos(chordAngle);
        pose.y += chordLength * Math.sin(chordAngle);
        pose.heading = angle;

        return;
    }

    /**
     * Set odometry data to a specific position and angle, and set new total
     * distances driven for the drivetrain. Should be used whenever drivetrain
     * encoders are zeroed (or otherwise set to new values) to update drivetrain
     * distances.
     *
     * @param newPose       The new position and heading of the robot
     * @param leftPosition  The new total distance the left side of the drivetrain
     *                      has driven
     * @param rightPosition The new total distance the right side of the drivetrain
     *                      has driven
     */
    public void set(Pose newPose, double leftPosition, double rightPosition) {
        pose = newPose;
        lastLeft = leftPosition;
        lastRight = rightPosition;
    }
}
