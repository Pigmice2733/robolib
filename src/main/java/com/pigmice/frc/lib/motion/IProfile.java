package com.pigmice.frc.lib.motion;

public interface IProfile {
    public Setpoint getSetpoint(double time);
    public double getDuration();
    public void reset();

    default double getVelocity(double time) {
        return getSetpoint(time).getVelocity();
    }

    default double getPosition(double time) {
        return getSetpoint(time).getPosition();
    }

    default double getAcceleration(double time) {
        return getSetpoint(time).getAcceleration();
    }
}
