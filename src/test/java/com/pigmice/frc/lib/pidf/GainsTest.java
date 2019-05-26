package com.pigmice.frc.lib.pidf;

import org.junit.Assert;
import org.junit.Test;

public class GainsTest {
    private static final double epsilon = 1e-6;

    @Test
    public void PIDTest() {
        Gains gains = new Gains(1.0, 0.5, 2.0);
        Assert.assertEquals(1.0, gains.kP(), epsilon);
        Assert.assertEquals(0.5, gains.kI(), epsilon);
        Assert.assertEquals(2.0, gains.kD(), epsilon);
    }

    @Test
    public void PIDFVATest() {
        Gains gains = new Gains(-1.0, -0.5, -2.0, -0.33, 4.0, -0.1);
        Assert.assertEquals(-1.0, gains.kP(), epsilon);
        Assert.assertEquals(-0.5, gains.kI(), epsilon);
        Assert.assertEquals(-2.0, gains.kD(), epsilon);
        Assert.assertEquals(-0.33, gains.kF(), epsilon);
        Assert.assertEquals(4.0, gains.kV(), epsilon);
        Assert.assertEquals(-0.1, gains.kA(), epsilon);
    }
}
