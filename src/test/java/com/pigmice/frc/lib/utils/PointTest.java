package com.pigmice.frc.lib.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PointTest {
    private static final double epsilon = 1e-6;

    @Test
    public void xyTest() {
        Point p = new Point(5.3, 6.0);
        Assertions.assertEquals(5.3, p.getX(), epsilon);
        Assertions.assertEquals(6.0, p.getY(), epsilon);
    }

    @Test
    public void originTest() {
        Point o = Point.origin();
        Assertions.assertEquals(0.0, o.getX(), epsilon);
        Assertions.assertEquals(0.0, o.getY(), epsilon);
    }

    @Test
    public void equalsTest() {
        Point one = new Point(5.3, 6.0);
        Vector vector = new Vector(5.3, 6.0);
        Point two = new Point(5.2, 6.0);
        Point three = new Point(5.3, -6.0);

        Assertions.assertFalse(one.equals(null));
        Assertions.assertFalse(one.equals(vector));
        Assertions.assertTrue(one.equals(one));

        Assertions.assertFalse(one.equals(two));
        Assertions.assertFalse(one.equals(three));
        Assertions.assertFalse(two.equals(three));
    }

    @Test
    public void hashCodeTest() {
        Point one = new Point(5.3, 6.0);
        Point two = new Point(5.6, 6.0);

        int codeOne = one.hashCode();
        int codeTwo = one.hashCode();
        int codeThree = two.hashCode();

        Assertions.assertEquals(codeOne, codeTwo);
        Assertions.assertNotEquals(codeOne, codeThree);
    }

    @Test
    public void transformTest() {
        Vector transform = new Vector(0.0, 0.0);
        Point p = new Point(4.0, -12.0);
        Point transformed = p.translate(transform);
        Assertions.assertEquals(4.0, transformed.getX(), epsilon);
        Assertions.assertEquals(-12.0, transformed.getY(), epsilon);

        transform = new Vector(-12.0, 3.0);
        transformed = p.translate(transform);
        Assertions.assertEquals(-8.0, transformed.getX(), epsilon);
        Assertions.assertEquals(-9.0, transformed.getY(), epsilon);
    }

    @Test
    public void subtractTest() {
        Point b = new Point(-32.0, 1.0);
        Point a = new Point(4.0, -12.0);
        Vector offset = b.subtract(a);

        Assertions.assertEquals(-36.0, offset.getX(), epsilon);
        Assertions.assertEquals(13.0, offset.getY(), epsilon);
    }

    @Test
    public void rotateTest() {
        Point orginal = new Point(-1.0, -1.0);
        Point center = new Point(0.0, 0.0);
        Point rotated = orginal.rotate(-0.5 * Math.PI, center);

        Assertions.assertEquals(-1.0, rotated.getX(), epsilon);
        Assertions.assertEquals(1.0, rotated.getY(), epsilon);

        orginal = new Point(-4.0, 4.0);
        center = new Point(-5.0, 5.0);
        rotated = orginal.rotate(1.5 * Math.PI, center);

        Assertions.assertEquals(-6.0, rotated.getX(), epsilon);
        Assertions.assertEquals(4.0, rotated.getY(), epsilon);
    }
}
