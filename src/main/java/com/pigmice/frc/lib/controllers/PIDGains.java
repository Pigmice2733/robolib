package com.pigmice.frc.lib.controllers;

public class PIDGains {
    private double kP, kI, kD;
    private double kF, kV, kA;

    public PIDGains(double kP, double kI, double kD) {
        this(kP, kI, kD, 0.0, 0.0, 0.0);
    }

    public PIDGains(double kP, double kI, double kD, double kF, double kV, double kA) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
        this.kV = kV;
        this.kA = kA;
    }

    public double kP() {
        return kP;
    }

    public double kI() {
        return kI;
    }

    public double kD() {
        return kD;
    }

    public double kF() {
        return kF;
    }

    public double kV() {
        return kV;
    }

    public double kA() {
        return kA;
    }
}
