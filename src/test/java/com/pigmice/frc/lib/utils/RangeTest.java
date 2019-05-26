package com.pigmice.frc.lib.utils;

import org.junit.Assert;
import org.junit.Test;

public class RangeTest {
    private static final double epsilon = 1e-6;

    @Test
    public void size() {
        Range bounds = new Range(0.0, 0.0);
        Assert.assertEquals(0.0, bounds.size(), epsilon);
        bounds = new Range(-2.0, 2.0);
        Assert.assertEquals(4.0, bounds.size(), epsilon);
        bounds = Range.noBounds();
        Assert.assertEquals(Double.POSITIVE_INFINITY, bounds.size(), epsilon);
    }

    @Test
    public void clamp() {
        Range bounds = new Range(0.0, 0.0);
        Assert.assertEquals(0.0, bounds.clamp(-6.3), epsilon);
        Assert.assertEquals(0.0, bounds.clamp(0.0), epsilon);
        bounds = new Range(-2.0, 2.0);
        Assert.assertEquals(1.0, bounds.clamp(1.0), epsilon);
        Assert.assertEquals(-2.0, bounds.clamp(-4), epsilon);
        bounds = Range.noBounds();
        Assert.assertEquals(-120.6, bounds.clamp(-120.6), epsilon);
        Assert.assertEquals(Double.MAX_VALUE, bounds.clamp(Double.MAX_VALUE), epsilon);
        Assert.assertEquals(Double.MIN_VALUE, bounds.clamp(Double.MIN_VALUE), epsilon);
    }

    @Test
    public void overlaps() {
        Range first = new Range(-1.0, 1.0);
        Range second = new Range(0.5, -5.0);
        Assert.assertTrue(first.overlaps(second));
        second = new Range(0.5, -0.0);
        Assert.assertTrue(first.overlaps(second));
        first = new Range(-1.0, -200.0);
        Assert.assertFalse(first.overlaps(second));
    }

    @Test
    public void min() {
        Range bounds = new Range(5, 7);
        Assert.assertEquals(5, bounds.min(), epsilon);
        bounds = new Range(7, -2);
        Assert.assertEquals(-2, bounds.min(), epsilon);
    }

    @Test
    public void max() {
        Range bounds = new Range(-6, 7);
        Assert.assertEquals(7, bounds.max(), epsilon);
        bounds = new Range(300, -0.0001);
        Assert.assertEquals(300, bounds.max(), epsilon);
    }

    @Test
    public void contains() {
        Range bounds = new Range(1, 3);
        Assert.assertTrue(bounds.contains(2));
        Assert.assertTrue(bounds.contains(3));
        Assert.assertTrue(bounds.contains(1));
        Assert.assertFalse(bounds.contains(0.99));
        Assert.assertFalse(bounds.contains(0));
        Assert.assertFalse(bounds.contains(3.001));
        Assert.assertFalse(bounds.contains(200));
    }
}
