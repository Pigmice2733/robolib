package com.pigmice.frc.lib.motion.profile;

import com.pigmice.frc.lib.motion.setpoint.ISetpoint;

public interface IProfile {
    public ISetpoint getSetpoint(double time);
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
