package com.pigmice.frc.lib.controllers;

import com.pigmice.frc.lib.motion.setpoint.ISetpoint;

public interface IController {
    public void initialize(double input, double currentOutput);
    public double calculateOutput(double input, ISetpoint setpoint);
}
