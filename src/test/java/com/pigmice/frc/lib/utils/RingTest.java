package com.pigmice.frc.lib.utils;

import org.junit.Test;
import org.junit.Assert;

public class RingTest {
    private static final double epsilon = 1e-6;

    @Test
    public void noLoop() {
        Ring ring = new Ring(5);

        ring.put(0.0);
        ring.put(1.0);
        ring.put(2.0);
        ring.put(3.0);
        ring.put(4.0);

        Assert.assertEquals(0.0, ring.pop(), epsilon);
        Assert.assertEquals(1.0, ring.pop(), epsilon);
        Assert.assertEquals(2.0, ring.pop(), epsilon);
        Assert.assertEquals(3.0, ring.pop(), epsilon);
        Assert.assertEquals(4.0, ring.pop(), epsilon);
    }

    @Test
    public void loop() {
        Ring ring = new Ring(4);

        ring.put(0.0);
        ring.put(1.0);
        ring.put(2.0);
        ring.put(3.0);
        ring.put(4.0);

        Assert.assertEquals(1.0, ring.pop(), epsilon);
        Assert.assertEquals(2.0, ring.pop(), epsilon);
        Assert.assertEquals(3.0, ring.pop(), epsilon);
        Assert.assertEquals(4.0, ring.pop(), epsilon);
        Assert.assertEquals(0.0, ring.pop(), epsilon);
        Assert.assertEquals(0.0, ring.pop(), epsilon);

        ring.put(5.0);
        ring.put(6.0);

        Assert.assertEquals(5.0, ring.pop(), epsilon);
        Assert.assertEquals(6.0, ring.pop(), epsilon);
        Assert.assertEquals(0.0, ring.pop(), epsilon);

        ring.put(7.0);
        ring.put(8.0);
        ring.put(9.0);
        ring.put(10.0);
        ring.put(11.0);

        Assert.assertEquals(8.0, ring.pop(), epsilon);
        Assert.assertEquals(9.0, ring.pop(), epsilon);
        Assert.assertEquals(10.0, ring.pop(), epsilon);
        Assert.assertEquals(11.0, ring.pop(), epsilon);
        Assert.assertEquals(0.0, ring.pop(), epsilon);
        Assert.assertEquals(0.0, ring.pop(), epsilon);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidSize() {
        @SuppressWarnings("unused")
        Ring ring = new Ring(0);
    }

    @Test
    public void average() {
        Ring ring = new Ring(3);

        Assert.assertEquals(0.0, ring.average(), epsilon);

        ring.put(2.0);
        Assert.assertEquals(2.0 / 3.0, ring.average(), epsilon);

        ring.put(1.0);
        Assert.assertEquals(3.0 / 3.0, ring.average(), epsilon);

        ring.put(2.0);
        Assert.assertEquals(5.0 / 3.0, ring.average(), epsilon);

        ring.put(1.0);
        Assert.assertEquals(4.0 / 3.0, ring.average(), epsilon);

        ring.pop();
        Assert.assertEquals(3.0 / 3.0, ring.average(), epsilon);
    }

    @Test
    public void isEmpty() {
        Ring ring = new Ring(2);

        Assert.assertTrue(ring.isEmpty());

        ring.pop();

        Assert.assertTrue(ring.isEmpty());

        ring.put(0.0);

        Assert.assertFalse(ring.isEmpty());

        ring.pop();

        Assert.assertTrue(ring.isEmpty());

        ring.put(1.0);
        ring.put(2.0);

        Assert.assertFalse(ring.isEmpty());
    }
}
