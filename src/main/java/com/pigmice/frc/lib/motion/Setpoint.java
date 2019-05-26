package com.pigmice.frc.lib.motion;

public class Setpoint {
    private final double position, velocity, acceleration;
    private final double curvature, heading;

    public Setpoint(double position, double velocity, double acceleration, double curvature, double heading) {
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.position = position;
        this.curvature = curvature;
        this.heading = heading;
    }

    public Setpoint(Chunk chunk, double time, double previousDistance, double curvature, double heading) {
        acceleration = chunk.getAcceleration();
        velocity = chunk.getVelocity(time);
        position = chunk.getPosition(time) + previousDistance;
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
     * Convert setpoint from being in terms of angle in radians to the arc length
     * distance of the robot's wheels.
     *
     * @param trackwidth The width of the robot from wheel to wheel
     * @return This setpoint expressed in arc length rather than angle
     */
    public Setpoint toArcLength(double trackwidth) {
        return toArcLength(trackwidth, true);
    }

    /***
     * Convert setpoint from being in terms of angle to the arc length distance of
     * the robot's wheels.
     *
     * @param radius  The turning radius
     * @param radians Whether the original position is in radians (or in degrees).
     * @return This setpoint expressed in arc length rather than angle
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

    public Setpoint negate() {
        return negate(true);
    }

    public Setpoint negate(boolean radians) {
        if (radians) {
            return new Setpoint(-position, -velocity, -acceleration, curvature, heading - Math.PI);
        } else {
            return new Setpoint(-position, -velocity, -acceleration, curvature, heading - 180.0);
        }
    }
}
