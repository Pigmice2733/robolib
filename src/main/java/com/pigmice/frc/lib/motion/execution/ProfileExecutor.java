package com.pigmice.frc.lib.motion.execution;

import com.pigmice.frc.lib.motion.IProfile;
import com.pigmice.frc.lib.motion.Setpoint;

import edu.wpi.first.wpilibj.Timer;

public class ProfileExecutor {
    public interface Output {
        void set(Setpoint sp);
    }

    public interface Input {
        double get();
    }

    private final IProfile profile;
    private final Output output;
    private final Input input;
    private final double allowableError;
    private final double finalTarget;

    private double startTime;

    public ProfileExecutor(IProfile profile, Output output, Input input, double allowableError) {
        this.profile = profile;
        this.output = output;
        this.input = input;
        this.allowableError = allowableError;

        finalTarget = profile.getPosition(profile.getDuration());
    }

    public void initialize() {
        startTime = Timer.getFPGATimestamp();
    }

    /**
     * Returns whether the error is within the allowable error, and outputs the
     * current setpoint
     */
    public boolean update() {
        double time = Timer.getFPGATimestamp() - startTime;
        Setpoint sp = profile.getSetpoint(time);
        output.set(sp);

        return Math.abs(finalTarget - input.get()) <= allowableError;
    }
}
