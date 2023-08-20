package com.pigmice.frc.lib.drivetrain.differential;

public class DifferentialConfig {
    public final int LEFT_DRIVE_PORT;
    public final int RIGHT_DRIVE_PORT;
    public final int LEFT_FOLLOW_PORT;
    public final int RIGHT_FOLLOW_PORT;

    public final boolean LEFT_INVERTED;
    public final boolean RIGHT_INVERTED;

    public final DifferentialFeedForward FEED_FORWARD;

    public final double DRIVETRAIN_WIDTH; // m
    public final double MAX_ACCELERATION; // m/s/s
    public final double MAX_ANGULAR_ACCELERATION; // rad/s/s

    public final double ROTATION_TO_DISTANCE_CONVERSION;
    public final double SLOW_MULTIPLIER;

    /**
     * <strong>Recommended use:</strong> create a DifferentialConfig object in
     * constants to pass into any subsystems and commands that need it
     * 
     * @param leftDrivePort                front left motor port
     * @param rightDrivePort               front right motor port
     * @param leftFollowPort               back left motor port
     * @param rightFollowPort              back right motor port
     * @param leftInverted                 invert the left motors
     * @param rightInverted                invert the right motors
     * @param feedForward                  feedforward constants used in
     *                                     pathfollowing and acceleration limiting
     * @param drivetrainWidth              the width from wheel to wheel (meters)
     * @param maxAcceleration              max allowed acceleration (meters / sec /
     *                                     sec)
     * @param maxAngularAcceleration       max allowed angular acceleration (rad /
     *                                     sec / sec)
     * @param rotationToDistanceConversion the conversion from wheel rotations to
     *                                     meters
     * @param slowMultiplier               a value between 0 and 1 multiplied by
     *                                     motor output in slow mode
     */
    public DifferentialConfig(int leftDrivePort, int rightDrivePort, int leftFollowPort, int rightFollowPort,
            boolean leftInverted, boolean rightInverted, DifferentialFeedForward feedForward, double drivetrainWidth,
            double maxAcceleration, double maxAngularAcceleration, double rotationToDistanceConversion,
            double slowMultiplier) {

        this.LEFT_DRIVE_PORT = leftDrivePort;
        this.RIGHT_DRIVE_PORT = rightDrivePort;
        this.LEFT_FOLLOW_PORT = leftFollowPort;
        this.RIGHT_FOLLOW_PORT = rightFollowPort;

        this.LEFT_INVERTED = leftInverted;
        this.RIGHT_INVERTED = rightInverted;

        this.FEED_FORWARD = feedForward;
        this.DRIVETRAIN_WIDTH = drivetrainWidth;
        this.MAX_ACCELERATION = maxAcceleration;
        this.MAX_ANGULAR_ACCELERATION = maxAngularAcceleration;

        this.ROTATION_TO_DISTANCE_CONVERSION = rotationToDistanceConversion; // TODO: take in things like gear ratio and
                                                                             // wheel diameter and calc this here
        this.SLOW_MULTIPLIER = slowMultiplier;
    }

    public class DifferentialFeedForward {
        public final double LINEAR_V;
        public final double LINEAR_A;

        public final double ANGULAR_S;
        public final double ANGULAR_V;
        public final double ANGULAR_A;

        public DifferentialFeedForward(double linearV, double linearA, double angularS, double angularV,
                double angularA) {
            this.LINEAR_V = linearV;
            this.LINEAR_A = linearA;

            this.ANGULAR_S = angularS;
            this.ANGULAR_V = angularV;
            this.ANGULAR_A = angularA;
        }
    }
}
