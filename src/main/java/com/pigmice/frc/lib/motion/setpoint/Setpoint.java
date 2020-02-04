package com.pigmice.frc.lib.motion.setpoint;

public class Setpoint implements ISetpoint{
    private final double position, velocity, acceleration;
    private final double curvature, heading;

    public Setpoint(double position, double velocity, double acceleration, double curvature, double heading) {
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.position = position;
        this.curvature = curvature;
        this.heading = heading;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getPosition() {
        return position;
    }

    public double getCurvature() {
        return curvature;
    }

    public double getHeading() {
        return heading;
    }

    /***
     * Converts angular setpoint from being in terms of angle (<b>in radians</b>) to the arc length
     * distance of the robot's wheels.
     *
     * The arc length is the distance covered while turning
     * from 0 to the angle specified by this setpoint, using the specified turning radius.
     *
     * The position, velocity, and acceleration are converted from radians per time to position per
     * time. Curvature is calculated as the inverse of the turning radius, and heading is the angle.
     *
     * @param radius The turning radius
     * @return This setpoint expressed as an arc length rather than angle
     */
    public Setpoint toArcLength(double radius) {
        return toArcLength(radius, true);
    }

   /***
     * Converts angular setpoint from being in terms of angle to the arc length
     * distance of the robot's wheels.
     *
     * The arc length is the distance covered while turning
     * from 0 to the angle specified by this setpoint, using the specified turning radius.
     *
     * The position, velocity, and acceleration are converted from radians/degree per time to position per
     * time. Curvature is calculated as the inverse of the turning radius, and heading is the angle.
     *
     * @param radius  The turning radius
     * @param radians Whether the original position is in radians (or in degrees)
     * @return This setpoint expressed as an arc length rather than angle
     */
    public Setpoint toArcLength(double radius, boolean radians) {
        double ratio;

        if (radians) {
            ratio = radius;
        } else {
            ratio = radius * Math.PI / 180.0;
        }

        // Position, velocity, and acceleration can be converted with the arc length
        // formula. Curvature is the inverse of radius, heading is the angle (old
        // position).
        return new Setpoint(position * ratio, velocity * ratio, acceleration * ratio, 1.0 / radius, position);
    }

    /**
     * Negates this setpoint. Position, velocity, and acceleration are negated, curvature and heading
     * are unchanged.
     *
     * @return The negated setpoint
     */
    public Setpoint negate() {
        return new Setpoint(-position, -velocity, -acceleration, curvature, heading);
    }
}
