package com.pigmice.frc.lib.motion.setpoint;

public interface ISetpoint {
    double getAcceleration();

    double getVelocity();

    double getPosition();

    double getCurvature();

    double getHeading();
}
