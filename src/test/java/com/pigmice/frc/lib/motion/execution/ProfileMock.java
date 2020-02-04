package com.pigmice.frc.lib.motion.execution;

import com.pigmice.frc.lib.motion.profile.IProfile;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;

public class ProfileMock implements IProfile {
    private ISetpoint[] data;
    private int current;
    private double duration;

    public ProfileMock(ISetpoint[] data, double duration) {
        this.data = data;
        this.current = 0;
        this.duration = duration;
    }

    public ISetpoint getSetpoint(double time) {
        if(time == duration) {
            return data[data.length - 1];
        }

        ISetpoint sp = data[current];
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
