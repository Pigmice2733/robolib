package com.pigmice.frc.lib.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransitionTest {
    private static final double epsilon = 1e-6;
    private static final Transition transition = new Transition(2.0, -6.0, 2.0);

    @Test
    public void getRate() {
        Assertions.assertEquals(-4.0, transition.getRate(), epsilon);
    }

    @Test
    public void get() {
        Assertions.assertEquals(2.0, transition.get(-1.0), epsilon);
        Assertions.assertEquals(2.0, transition.get(0.0), epsilon);
        Assertions.assertEquals(0.0, transition.get(0.5), epsilon);
        Assertions.assertEquals(-6.0, transition.get(2.0), epsilon);
        Assertions.assertEquals(-6.0, transition.get(3.15), epsilon);
    }

    @Test
    public void integrate() {
        Assertions.assertEquals(0.0, transition.integrate(-20.25), epsilon);
        Assertions.assertEquals(0.0, transition.integrate(0.0), epsilon);
        Assertions.assertEquals(0.5, transition.integrate(0.5), epsilon);
        Assertions.assertEquals(-4.0, transition.integrate(2.0), epsilon);
        Assertions.assertEquals(-4.0, transition.integrate(2.2), epsilon);
    }

    @Test
    public void zero() {
        Transition zero = Transition.zero();

        Assertions.assertEquals(0.0, zero.get(-2.0), epsilon);
        Assertions.assertEquals(0.0, zero.get(0.0), epsilon);
        Assertions.assertEquals(0.0, zero.get(2.0), epsilon);

        Assertions.assertEquals(0.0, zero.getRate(), epsilon);

        Assertions.assertEquals(0.0, zero.integrate(-2.0), epsilon);
        Assertions.assertEquals(0.0, zero.integrate(0.5), epsilon);
        Assertions.assertEquals(0.0, zero.integrate(200.0), epsilon);
    }
}
