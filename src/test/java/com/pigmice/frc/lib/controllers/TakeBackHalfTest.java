package com.pigmice.frc.lib.controllers;

import com.pigmice.frc.lib.motion.setpoint.Setpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TakeBackHalfTest {
    private static Setpoint sp(double velocity) {
        return new Setpoint(0.0, velocity, 0.0, 0.0, 0.0);
    }

    @Test
    public void testOutput() {
        TakeBackHalf tbh = new TakeBackHalf(0.0002, 0.5);

        tbh.initialize(0.0, 0.0);
        Assertions.assertEquals(0.4, tbh.calculateOutput(0.0, sp(2000.0)), 1e-6);
        Assertions.assertEquals(0.8, tbh.calculateOutput(0.0, sp(2000.0)), 1e-6);
        Assertions.assertEquals(1.0, tbh.calculateOutput(0.0, sp(2000.0)), 1e-6);
        Assertions.assertEquals(0.5, tbh.calculateOutput(3000.0, sp(2000.0)), 1e-6);
        Assertions.assertEquals(0.5, tbh.calculateOutput(1000.0, sp(2000.0)), 1e-6);
        Assertions.assertEquals(0.7, tbh.calculateOutput(1000.0, sp(2000.0)), 1e-6);
    }

    @Test
    public void testOptimalTakeBackHalf() {
        TakeBackHalf tbh = new TakeBackHalf(0.0002, 0.5);

        tbh.initialize(0.0, 1.0);
        Assertions.assertEquals(1.0, tbh.calculateOutput(1000.0, sp(2000.0)), 1e-6);
        Assertions.assertEquals(0.5, tbh.calculateOutput(2500.0, sp(2000.0)), 1e-6);
        Assertions.assertEquals(0.48, tbh.calculateOutput(2100.0, sp(2000.0)), 1e-6);
    }
}
