package com.pigmice.frc.lib.utils;

import com.pigmice.frc.lib.utils.Odometry.Pose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PointTest {
    private static final double epsilon = 1e-6;

    private static class TestXY implements XY {
        public double getX() {
            return 2.0;
        }

        public double getY() {
            return 2.5;
        }
    }

    @Test
    public void xyTest() {
        Point p = new Point(5.3, 6.0);
        Assertions.assertEquals(5.3, p.getX(), epsilon);
        Assertions.assertEquals(6.0, p.getY(), epsilon);

        TestXY original = new TestXY();
        Point clone = new Point(original);
        Assertions.assertEquals(original.getX(), clone.getX(), epsilon);
        Assertions.assertEquals(original.getY(), clone.getY(), epsilon);
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
        Point four  = new Point(5.2, 6.0);

        Assertions.assertFalse(one.equals(null));
        Assertions.assertFalse(one.equals(vector));
        Assertions.assertTrue(one.equals(one));

        Assertions.assertFalse(one.equals(two));
        Assertions.assertFalse(one.equals(three));
        Assertions.assertFalse(two.equals(three));

        Assertions.assertTrue(two.equals(four));
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
    public void subtractTest() {
        Point b = new Point(-32.0, 1.0);
        Point a = new Point(4.0, -12.0);
        Vector offset = b.subtract(a);

        Assertions.assertEquals(-36.0, offset.getX(), epsilon);
        Assertions.assertEquals(13.0, offset.getY(), epsilon);
    }

    @Test
    public void translateTest() {
        Vector translate = new Vector(0.0, 0.0);
        Point p = new Point(4.0, -12.0);
        Point translated = p.translate(translate);
        Assertions.assertEquals(4.0, translated.getX(), epsilon);
        Assertions.assertEquals(-12.0, translated.getY(), epsilon);

        translate = new Vector(-12.0, 3.0);
        translated = p.translate(translate);
        Assertions.assertEquals(-8.0, translated.getX(), epsilon);
        Assertions.assertEquals(-9.0, translated.getY(), epsilon);
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

    @Test
    public void relativeTest() {
        Pose pose = new Pose(-4.0, -4.0, 1.25*Math.PI);

        Point point = new Point(-5.0, -5.0);
        Point relative = point.relativeTo(pose);
        Assertions.assertEquals(0.0, relative.getX(), epsilon);
        Assertions.assertEquals(Math.sqrt(2.0), relative.getY(), epsilon);

        point = new Point(1.0, 1.0);
        relative = point.relativeTo(pose);
        Assertions.assertEquals(0.0, relative.getX(), epsilon);
        Assertions.assertEquals(-Math.sqrt(50.0), relative.getY(), epsilon);

        point = new Point(-8.0, 0.0);
        relative = point.relativeTo(pose);
        Assertions.assertEquals(Math.sqrt(32.0), relative.getX(), epsilon);
        Assertions.assertEquals(0, relative.getY(), epsilon);

        point = new Point(0.0, -8.0);
        relative = point.relativeTo(pose);
        Assertions.assertEquals(-Math.sqrt(32.0), relative.getX(), epsilon);
        Assertions.assertEquals(0, relative.getY(), epsilon);
    }
}
