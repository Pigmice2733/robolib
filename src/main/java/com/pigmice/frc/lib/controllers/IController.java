package com.pigmice.frc.lib.controllers;

public interface IController {
    public void initialize(double input, double currentOutput);
    public double calculateOutput(double input, double setpoint);
}
