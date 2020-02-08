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
        double deltaAngle = angle - pose.heading;

        lastLeft = leftPosition;
        lastRight = rightPosition;

        double distance = (deltaLeft + deltaRight) / 2.0;
        if (Math.abs(deltaAngle) > Math.PI) {
            deltaAngle = Math.copySign(2.0 * Math.PI - Math.abs(deltaAngle), -deltaAngle);
        }

        if (Math.abs(deltaAngle) < 1e-6) {
            double deltaX = distance * Math.cos(angle);
            double deltaY = distance * Math.sin(angle);
            pose.x += deltaX;
            pose.y += deltaY;
            pose.heading = angle;

            return;
        }

        double radius = distance / Math.abs(deltaAngle);
        double chordAngle = pose.heading + deltaAngle / 2.0;
        double chordLength = 2.0 * radius * Math.sin(Math.abs(deltaAngle) / 2.0);

        double deltaX = chordLength * Math.cos(chordAngle);
        double deltaY = chordLength * Math.sin(chordAngle);

        pose.x += deltaX;
        pose.y += deltaY;

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
