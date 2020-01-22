package com.pigmice.frc.lib.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RangeTest {
    private static final double epsilon = 1e-6;

    @Test
    public void size() {
        Range bounds = new Range(0.0, 0.0);
        Assertions.assertEquals(0.0, bounds.size(), epsilon);
        bounds = new Range(-2.0, 2.0);
        Assertions.assertEquals(4.0, bounds.size(), epsilon);
        bounds = Range.noBounds();
        Assertions.assertEquals(Double.POSITIVE_INFINITY, bounds.size(), epsilon);
        bounds = Range.natural();
        Assertions.assertEquals(1.0, bounds.size(), epsilon);
    }

    @Test
    public void clamp() {
        Range bounds = new Range(0.0, 0.0);
        Assertions.assertEquals(0.0, bounds.clamp(-6.3), epsilon);
        Assertions.assertEquals(0.0, bounds.clamp(0.0), epsilon);
        bounds = new Range(-2.0, 2.0);
        Assertions.assertEquals(1.0, bounds.clamp(1.0), epsilon);
        Assertions.assertEquals(-2.0, bounds.clamp(-4), epsilon);
        bounds = Range.noBounds();
        Assertions.assertEquals(-120.6, bounds.clamp(-120.6), epsilon);
        Assertions.assertEquals(Double.MAX_VALUE, bounds.clamp(Double.MAX_VALUE), epsilon);
        Assertions.assertEquals(Double.MIN_VALUE, bounds.clamp(Double.MIN_VALUE), epsilon);
        bounds = Range.natural();
        Assertions.assertEquals(0, bounds.clamp(-1), epsilon);
        Assertions.assertEquals(0, bounds.clamp(0), epsilon);
        Assertions.assertEquals(1, bounds.clamp(2), epsilon);
        Assertions.assertEquals(1, bounds.clamp(1), epsilon);
        Assertions.assertEquals(0.5, bounds.clamp(0.5), epsilon);
    }

    @Test
    public void overlaps() {
        Range first = new Range(-1.0, 1.0);
        Range second = new Range(0.5, -5.0);
        Assertions.assertTrue(first.overlaps(second));
        second = new Range(0.5, -0.0);
        Assertions.assertTrue(first.overlaps(second));
        first = new Range(-1.0, -200.0);
        Assertions.assertFalse(first.overlaps(second));
    }

    @Test
    public void min() {
        Range bounds = new Range(5, 7);
        Assertions.assertEquals(5, bounds.min(), epsilon);
        bounds = new Range(7, -2);
        Assertions.assertEquals(-2, bounds.min(), epsilon);
    }

    @Test
    public void max() {
        Range bounds = new Range(-6, 7);
        Assertions.assertEquals(7, bounds.max(), epsilon);
        bounds = new Range(300, -0.0001);
        Assertions.assertEquals(300, bounds.max(), epsilon);
    }

    @Test
    public void contains() {
        Range bounds = new Range(1, 3);
        Assertions.assertTrue(bounds.contains(2));
        Assertions.assertTrue(bounds.contains(3));
        Assertions.assertTrue(bounds.contains(1));
        Assertions.assertFalse(bounds.contains(0.99));
        Assertions.assertFalse(bounds.contains(0));
        Assertions.assertFalse(bounds.contains(3.001));
        Assertions.assertFalse(bounds.contains(200));
    }
}
