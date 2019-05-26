package com.pigmice.frc.lib.motion;

import org.junit.Assert;
import org.junit.Test;

public class SetpointTest {
    private static final double epsilon = 1e-6;

    @Test
    public void passThroughConstructor() {
        Setpoint sp = new Setpoint(20.0, -2.0, 0.0, 2.0, Math.PI / 2.0);
        Assert.assertEquals(0.0, sp.getAcceleration(), epsilon);
        Assert.assertEquals(-2.0, sp.getVelocity(), epsilon);
        Assert.assertEquals(20.0, sp.getPosition(), epsilon);
        Assert.assertEquals(2.0, sp.getCurvature(), epsilon);
        Assert.assertEquals(Math.PI / 2.0, sp.getHeading(), epsilon);
    }

    @Test
    public void chunkConstructor() {
        Chunk chunk = Chunk.createConstantVelocity(5.0, 50.0);
        Setpoint sp = new Setpoint(chunk, 5.0, 5.0, 0.0, 0.0);
        Assert.assertEquals(0.0, sp.getAcceleration(), epsilon);
        Assert.assertEquals(5.0, sp.getVelocity(), epsilon);
        Assert.assertEquals(30.0, sp.getPosition(), epsilon);
        Assert.assertEquals(0.0, sp.getCurvature(), epsilon);
        Assert.assertEquals(0.0, sp.getHeading(), epsilon);
    }
}
