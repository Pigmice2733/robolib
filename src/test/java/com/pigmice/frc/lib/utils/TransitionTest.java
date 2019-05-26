package com.pigmice.frc.lib.utils;

import org.junit.Assert;
import org.junit.Test;

public class TransitionTest {
    private static final double epsilon = 1e-6;
    private static final Transition transition = new Transition(2.0, -6.0, 2.0);

    @Test
    public void getRate() {
        Assert.assertEquals(-4.0, transition.getRate(), epsilon);
    }

    @Test
    public void get() {
        Assert.assertEquals(2.0, transition.get(-1.0), epsilon);
        Assert.assertEquals(2.0, transition.get(0.0), epsilon);
        Assert.assertEquals(0.0, transition.get(0.5), epsilon);
        Assert.assertEquals(-6.0, transition.get(2.0), epsilon);
        Assert.assertEquals(-6.0, transition.get(3.15), epsilon);
    }

    @Test
    public void integrate() {
        Assert.assertEquals(0.0, transition.integrate(-20.25), epsilon);
        Assert.assertEquals(0.0, transition.integrate(0.0), epsilon);
        Assert.assertEquals(0.5, transition.integrate(0.5), epsilon);
        Assert.assertEquals(-4.0, transition.integrate(2.0), epsilon);
        Assert.assertEquals(-4.0, transition.integrate(2.2), epsilon);
    }

    @Test
    public void zero() {
        Transition zero = Transition.zero();

        Assert.assertEquals(0.0, zero.get(-2.0), epsilon);
        Assert.assertEquals(0.0, zero.get(0.0), epsilon);
        Assert.assertEquals(0.0, zero.get(2.0), epsilon);

        Assert.assertEquals(0.0, zero.getRate(), epsilon);

        Assert.assertEquals(0.0, zero.integrate(-2.0), epsilon);
        Assert.assertEquals(0.0, zero.integrate(0.5), epsilon);
        Assert.assertEquals(0.0, zero.integrate(200.0), epsilon);
    }
}
