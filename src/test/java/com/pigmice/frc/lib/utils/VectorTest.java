package com.pigmice.frc.lib.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VectorTest {
    private static final double epsilon = 1e-6;

    @Test
    public void xyTest() {
        Vector v = new Vector(-5.3, 4.0);
        Assertions.assertEquals(-5.3, v.getX(), epsilon);
        Assertions.assertEquals(4.0, v.getY(), epsilon);
    }

    @Test
    public void equalsTest() {
        Vector one = new Vector(5.3, 6.0);
        Point point = new Point(5.3, 6.0);
        Vector two = new Vector(5.2, 6.0);
        Vector three = new Vector(5.3, -6.0);
        Vector four = new Vector(5.2, 6.0);

        Assertions.assertFalse(one.equals(null));
        Assertions.assertFalse(one.equals(point));
        Assertions.assertTrue(one.equals(one));

        Assertions.assertFalse(one.equals(two));
        Assertions.assertFalse(one.equals(three));
        Assertions.assertFalse(two.equals(three));

        Assertions.assertTrue(two.equals(four));
    }

    @Test
    public void hashCodeTest() {
        Vector one = new Vector(5.3, 6.0);
        Vector two = new Vector(5.6, 6.0);

        int codeOne = one.hashCode();
        int codeTwo = one.hashCode();
        int codeThree = two.hashCode();

        Assertions.assertEquals(codeOne, codeTwo);
        Assertions.assertNotEquals(codeOne, codeThree);
    }

    @Test
    public void zeroTest() {
        Vector zero = Vector.zero();
        Assertions.assertEquals(0.0, zero.getX(), epsilon);
        Assertions.assertEquals(0.0, zero.getY(), epsilon);
    }

    @Test
    public void scaleTest() {
        Vector original = new Vector(-2.0, 5.0);
        Vector scaled = original.scale(-10.0);
        Assertions.assertEquals(20.0, scaled.getX(), epsilon);
        Assertions.assertEquals(-50.0, scaled.getY(), epsilon);

        scaled = scaled.scale(-0.1);
        Assertions.assertEquals(-2.0, scaled.getX(), epsilon);
        Assertions.assertEquals(5.0, scaled.getY(), epsilon);
    }

    @Test
    public void addTest() {
        Vector a = new Vector(-2.0, 5.0);
        Vector b = new Vector(-10.0, -12.0);
        Vector c = a.add(b);

        Assertions.assertEquals(-12.0, c.getX(), epsilon);
        Assertions.assertEquals(-7.0, c.getY(), epsilon);
    }

    @Test
    public void dotTest() {
        Vector a = new Vector(0.0, 2.0);
        Assertions.assertEquals(4.0, a.dot(a), epsilon);

        Vector b = new Vector(-1.0, 0.0);
        Assertions.assertEquals(0.0, a.dot(b), epsilon);

        b = new Vector(-2.0, -2.0);
        Assertions.assertEquals(-4.0, a.dot(b), epsilon);

        a = new Vector(2.0, -2.0);
        Assertions.assertEquals(0.0, a.dot(b), epsilon);
    }

    @Test
    public void getMagnitudeTest() {
        Vector mixedSign = new Vector(-2.0, 5.0);
        Vector negative = new Vector(-3.0, -4.0);
        Vector zero = new Vector(0.0, 0.0);

        Assertions.assertEquals(Math.sqrt(29.0), mixedSign.magnitude(), epsilon);
        Assertions.assertEquals(5.0, negative.magnitude(), epsilon);
        Assertions.assertEquals(0.0, zero.magnitude(), epsilon);
    }

    @Test
    public void normalizeTest() {
        Vector vector = new Vector(-1.0, 0.0);
        Vector normalized = vector.normalize();

        Assertions.assertEquals(-1.0, normalized.getX(), epsilon);
        Assertions.assertEquals(0.0, normalized.getY(), epsilon);

        vector = new Vector(2.0, -3.0);
        normalized = vector.normalize();

        Assertions.assertEquals(2.0 / Math.sqrt(13.0), normalized.getX(), epsilon);
        Assertions.assertEquals(-3.0 / Math.sqrt(13.0), normalized.getY(), epsilon);
    }

    @Test
    public void getAngleTest() {
        Vector xAxis = new Vector(1.0, 0.0);
        Vector yAxis = new Vector(0.0, 1.0);
        Vector negativeY = new Vector(0.0, -1.0);
        Vector negativeHalf = new Vector(-1.0, -1.0);

        Assertions.assertEquals(0.0, xAxis.angle(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, yAxis.angle(), epsilon);
        Assertions.assertEquals(-0.5 * Math.PI, negativeY.angle(), epsilon);
        Assertions.assertEquals(-0.75 * Math.PI, negativeHalf.angle(), epsilon);
    }

    @Test
    public void rotateTest() {
        Vector original = new Vector(-1.0, 1.0);
        Vector rotated = original.rotate(-1.5 * Math.PI);

        Assertions.assertEquals(-1.0, rotated.getX(), epsilon);
        Assertions.assertEquals(-1.0, rotated.getY(), epsilon);

        rotated = Vector.zero().rotate(Math.PI);
        Assertions.assertEquals(0.0, rotated.getX(), epsilon);
        Assertions.assertEquals(0.0, rotated.getY(), epsilon);
    }
}
