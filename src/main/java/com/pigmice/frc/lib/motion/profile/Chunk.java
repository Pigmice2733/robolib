package com.pigmice.frc.lib.motion.profile;

import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.pigmice.frc.lib.motion.setpoint.Setpoint;
import com.pigmice.frc.lib.utils.Transition;

class Chunk {
    private final double distance, acceleration, duration;
    private final Transition velocity;
    private final Transition curvature;
    private final Transition heading;

    private Chunk(double distance, Transition velocity, Transition curvature, Transition heading, double duration) {
        this.distance = distance;
        this.velocity = velocity;
        this.curvature = curvature;
        this.heading = heading;
        this.duration = duration;
        this.acceleration = velocity.getRate();
    }

    protected static Chunk createVelocityDistance(double distance, double startVelocity, double endVelocity,
            double startCurvature, double endCurvature, double startHeading, double endHeading) {
        double averageVelocity = 0.5 * (startVelocity + endVelocity);
        double duration = distance / averageVelocity;

        Transition velocity = new Transition(startVelocity, endVelocity, duration);
        Transition curvature = new Transition(startCurvature, endCurvature, duration);
        Transition heading = new Transition(startHeading, endHeading, duration);

        return new Chunk(distance, velocity, curvature, heading, duration);
    }

    protected static Chunk createConstantVelocity(double velocity, double distance) {
        double duration = distance / velocity;
        Transition v = new Transition(velocity, velocity, duration);
        return new Chunk(distance, v, Transition.zero(), Transition.zero(), duration);
    }

    protected static Chunk createVelocityTransition(double startVelocity, double endVelocity, double maxAccel,
            double maxDecel) {
        final double averageVelocity = (startVelocity + endVelocity) / 2.0;
        final double deltaVelocity = (endVelocity - startVelocity);

        double duration, distance;

        if (Math.abs(endVelocity) > Math.abs(startVelocity)) {
            duration = Math.abs(deltaVelocity) / maxAccel;
        } else {
            duration = Math.abs(deltaVelocity) / maxDecel;
        }

        distance = averageVelocity * duration;

        Transition velocity = new Transition(startVelocity, endVelocity, duration);

        return new Chunk(distance, velocity, Transition.zero(), Transition.zero(), duration);
    }

    protected ISetpoint getSetpoint(double time) {
        return new Setpoint(getPosition(time), getVelocity(time), acceleration, getCurvature(time), getHeading(time));
    }

    protected double getDuration() {
        return duration;
    }

    protected double getTotalDistance() {
        return distance;
    }

    protected double getEndVelocity() {
        return velocity.get(duration);
    }

    protected double getPosition(double time) {
        return velocity.integrate(time);
    }

    protected double getVelocity(double time) {
        return velocity.get(time);
    }

    protected double getAcceleration() {
        return acceleration;
    }

    protected double getCurvature(double time) {
        return curvature.get(time);
    }

    protected double getHeading(double time) {
        return heading.get(time);
    }
}
