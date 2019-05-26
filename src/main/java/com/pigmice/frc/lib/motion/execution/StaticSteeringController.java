package com.pigmice.frc.lib.motion.execution;

import com.pigmice.frc.lib.pidf.PIDF;

import edu.wpi.first.wpilibj.Timer;

public class StaticSteeringController {
    private Input input;
    private PIDF pid;

    private double target;
    private double startTime;

    private double bias;

    public interface Input {
        double get();
    }

    public StaticSteeringController(Input input, PIDF pid) {
        this(input, pid, 0.0);
    }

    public StaticSteeringController(Input input, PIDF pid, double target) {
        this.input = input;
        this.pid = pid;
        this.target = target;
    }

    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        pid.initialize(input.get(), startTime, 0.0);
        bias = 0.0;
    }

    public void initialize(double target) {
        this.target = target;
        initialize();
    }

    // Returning a wheel distance correction to steer to target heading
    public double correct() {
        return correct(target);
    }

    // Returning a wheel distance correction to steer to target heading
    public double correct(double target) {
        this.target = target;
        double correction = pid.calculateOutput(input.get(), target, Timer.getFPGATimestamp() - startTime);

        bias += correction;

        return bias;
    }

}
