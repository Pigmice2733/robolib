package com.pigmice.frc.lib.motion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SetpointTest {
    private static final double epsilon = 1e-6;

    @Test
    public void passThroughConstructor() {
        Setpoint sp = new Setpoint(20.0, -2.0, 0.0, 2.0, Math.PI / 2.0);
        Assertions.assertEquals(0.0, sp.getAcceleration(), epsilon);
        Assertions.assertEquals(-2.0, sp.getVelocity(), epsilon);
        Assertions.assertEquals(20.0, sp.getPosition(), epsilon);
        Assertions.assertEquals(2.0, sp.getCurvature(), epsilon);
        Assertions.assertEquals(Math.PI / 2.0, sp.getHeading(), epsilon);
    }

    @Test
    public void chunkConstructor() {
        Chunk chunk = Chunk.createConstantVelocity(5.0, 50.0);
        Setpoint sp = new Setpoint(chunk, 5.0, 5.0, 0.0, 0.0);
        Assertions.assertEquals(0.0, sp.getAcceleration(), epsilon);
        Assertions.assertEquals(5.0, sp.getVelocity(), epsilon);
        Assertions.assertEquals(30.0, sp.getPosition(), epsilon);
        Assertions.assertEquals(0.0, sp.getCurvature(), epsilon);
        Assertions.assertEquals(0.0, sp.getHeading(), epsilon);
    }
}
