package com.pigmice.frc.lib.motion.execution;

import com.pigmice.frc.lib.motion.IProfile;
import com.pigmice.frc.lib.motion.Setpoint;

public class ProfileMock implements IProfile {
    private Setpoint[] data;
    private int current;
    private double duration;

    public ProfileMock(Setpoint[] data, double duration) {
        this.data = data;
        this.current = 0;
        this.duration = duration;
    }

    public Setpoint getSetpoint(double time) {
        if(time == duration) {
            return data[data.length - 1];
        }

        Setpoint sp = data[current];
        current = (current + 1) % data.length;
        return sp;
    }

    public double getDuration() {
        return duration;
    }

    public void reset() {
        current = 0;
    }
}
