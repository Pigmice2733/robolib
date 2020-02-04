package com.pigmice.frc.lib.motion.setpoint;

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
    public void arcLength() {
        Setpoint sp = new Setpoint(Math.PI, 0.2 * Math.PI, 1.0, 0.2, 0.0);
        Setpoint arc = sp.toArcLength(5.0);

        Assertions.assertEquals(5.0, arc.getAcceleration(), epsilon);
        Assertions.assertEquals(Math.PI, arc.getVelocity(), epsilon);
        Assertions.assertEquals(5.0 * Math.PI, arc.getPosition(), epsilon);
        Assertions.assertEquals(0.2, arc.getCurvature(), epsilon);
        Assertions.assertEquals(Math.PI, arc.getHeading(), epsilon);
    }

    @Test
    public void degreeArcLength() {
        Setpoint sp = new Setpoint(180, 0.2 * 180, 180.0 / Math.PI, 0.2, 0.0);
        Setpoint arc = sp.toArcLength(5.0, false);

        Assertions.assertEquals(5.0, arc.getAcceleration(), epsilon);
        Assertions.assertEquals(Math.PI, arc.getVelocity(), epsilon);
        Assertions.assertEquals(5.0 * Math.PI, arc.getPosition(), epsilon);
        Assertions.assertEquals(0.2, arc.getCurvature(), epsilon);
        Assertions.assertEquals(180, arc.getHeading(), epsilon);
    }

    @Test
    public void negate() {
        Setpoint sp = new Setpoint(4, 5, 6, 0.2, 1.5 * Math.PI);
        sp = sp.negate();

        Assertions.assertEquals(-6.0, sp.getAcceleration(), epsilon);
        Assertions.assertEquals(-5.0, sp.getVelocity(), epsilon);
        Assertions.assertEquals(-4.0, sp.getPosition(), epsilon);
        Assertions.assertEquals(0.2, sp.getCurvature(), epsilon);
        Assertions.assertEquals(1.5 * Math.PI, sp.getHeading(), epsilon);
    }
}
