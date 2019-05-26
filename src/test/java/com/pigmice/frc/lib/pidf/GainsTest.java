package com.pigmice.frc.lib.pidf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GainsTest {
    private static final double epsilon = 1e-6;

    @Test
    public void PIDTest() {
        Gains gains = new Gains(1.0, 0.5, 2.0);
        Assertions.assertEquals(1.0, gains.kP(), epsilon);
        Assertions.assertEquals(0.5, gains.kI(), epsilon);
        Assertions.assertEquals(2.0, gains.kD(), epsilon);
    }

    @Test
    public void PIDFVATest() {
        Gains gains = new Gains(-1.0, -0.5, -2.0, -0.33, 4.0, -0.1);
        Assertions.assertEquals(-1.0, gains.kP(), epsilon);
        Assertions.assertEquals(-0.5, gains.kI(), epsilon);
        Assertions.assertEquals(-2.0, gains.kD(), epsilon);
        Assertions.assertEquals(-0.33, gains.kF(), epsilon);
        Assertions.assertEquals(4.0, gains.kV(), epsilon);
        Assertions.assertEquals(-0.1, gains.kA(), epsilon);
    }
}
