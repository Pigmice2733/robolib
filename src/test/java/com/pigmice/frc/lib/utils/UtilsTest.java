package com.pigmice.frc.lib.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {
    public static class LerpTest {
        private static final double epsilon = 1e-6;

        @Test
        public void lerpEqualRanges() {
            Assertions.assertEquals(2.0, Utils.lerp(2.0, 1.0, 3.0, 1.0, 3.0), epsilon);
        }

        @Test
        public void lerpOffsetEqualRanges() {
            Assertions.assertEquals(7.0, Utils.lerp(6.0, 5.0, 7.0, 6.0, 8.0), epsilon);
        }

        @Test
        public void lerpUnequalPositiveRanges() {
            Assertions.assertEquals(3.0, Utils.lerp(3.0, 2.0, 6.0, 1.0, 9.0), epsilon);
        }

        @Test
        public void lerpEqualNegativeRanges() {
            Assertions.assertEquals(-4.0, Utils.lerp(-1.0, 0.0, -2.0, -3.0, -5.0), epsilon);
        }

        @Test
        public void lerpUnequalNegativeRanges() {
            Assertions.assertEquals(-5.0, Utils.lerp(-1.0, 0.0, -2.0, -3.0, -7.0), epsilon);
        }

        @Test
        public void lerpMixedSignRanges() {
            Assertions.assertEquals(7.0, Utils.lerp(-1.0, 0.0, -2.0, 5.0, 9.0), epsilon);
        }

        @Test
        public void lerpHalf1d() {
            Assertions.assertEquals(4, Utils.lerp(2.0, 6.0, 0.5), epsilon);
        }

        @Test
        public void lerpBackwards1d() {
            Assertions.assertEquals(1, Utils.lerp(2, -3, 0.2), epsilon);
        }

        @Test
        public void lerpMixedSign1d() {
            Assertions.assertEquals(0, Utils.lerp(-5, 5, 0.5), epsilon);
        }
    }

    public static class AlmostEqualsTest {
        private static final double epsilon = 1e-6;

        @Test
        public void equal() {
            Assertions.assertTrue(Utils.almostEquals(1.0000001, 1.000000, epsilon));
            Assertions.assertTrue(Utils.almostEquals(1 / 3, 1 / 3 + 1e-7, epsilon));

            Assertions.assertTrue(Utils.almostEquals(-1.0000001, -1.000000, epsilon));
            Assertions.assertTrue(Utils.almostEquals(-0.0, -0.000000001, epsilon));

            Assertions.assertTrue(Utils.almostEquals(-0.0, +0.0, epsilon));
        }

        @Test
        public void notEqual() {
            Assertions.assertFalse(Utils.almostEquals(1.0000001, 2, epsilon));
            Assertions.assertFalse(Utils.almostEquals(-0.0, 5.0, epsilon));
            Assertions.assertFalse(Utils.almostEquals(-2.0, 2.0, epsilon));

        }
    }

    public static class BinarySearchTest {
        @Test
        public void search() {
            ArrayList<Double> data = new ArrayList<>();
            data.add(0.0);
            data.add(1.0);
            data.add(6.0);
            data.add(7.0);
            data.add(9.0);
            data.add(9.0);
            data.add(10.0);
            data.add(40.0);

            Assertions.assertEquals(6, Utils.binarySearch(data, 12.0));
            Assertions.assertEquals(0, Utils.binarySearch(data, -1.0));
            Assertions.assertEquals(3, Utils.binarySearch(data, 9.0));
            Assertions.assertEquals(6, Utils.binarySearch(data, 50.0));
        }
    }

    public static class CircleLineIntersectionTest {
        private static final double epsilon = 1e-6;

        @Test
        public void intersections() {
            Point start = Point.origin();
            Vector direction = new Vector(6.0, 0.0);

            List<Double> intersections = Utils.circleLineIntersections(
                    start, direction, Point.origin(), 5.0);

            Assertions.assertEquals(2, intersections.size());

            Point firstIntersection = start.translate(direction.scale(intersections.get(0)));
            Point secondIntersection = start.translate(direction.scale(intersections.get(1)));

            Assertions.assertEquals(-5.0, firstIntersection.getX(), epsilon);
            Assertions.assertEquals(0.0, firstIntersection.getY(), epsilon);
            Assertions.assertEquals(5.0, secondIntersection.getX(), epsilon);
            Assertions.assertEquals(0.0, secondIntersection.getY(), epsilon);

            start = new Point(5.0, 0.0);
            direction = new Vector(-6.0, -6.0);

            intersections = Utils.circleLineIntersections(
                    start, direction, new Point(2.0, -3.0), 4.0);

            Assertions.assertEquals(2, intersections.size());

            firstIntersection = start.translate(direction.scale(intersections.get(0)));
            secondIntersection = start.translate(direction.scale(intersections.get(1)));

            Assertions.assertEquals(Math.sqrt(8.0) + 2, firstIntersection.getX(), epsilon);
            Assertions.assertEquals(Math.sqrt(8.0) - 3, firstIntersection.getY(), epsilon);
            Assertions.assertEquals(-Math.sqrt(8.0) + 2, secondIntersection.getX(), epsilon);
            Assertions.assertEquals(-Math.sqrt(8.0) - 3, secondIntersection.getY(), epsilon);
        }

        @Test
        public void noIntersections() {
            List<Double> intersections = Utils.circleLineIntersections(
                    Point.origin(), new Vector(1.0, 0.0), new Point(0.0, 6.0), 5.0);

            Assertions.assertEquals(0, intersections.size());

            intersections = Utils.circleLineIntersections(
                    new Point(5.0, 5.0), new Vector(0.0, -10.0), Point.origin(), 4.0);

            Assertions.assertEquals(0, intersections.size());
        }
    }

    public static class ProjectionTest {
        private static final double epsilon = 1e-6;

        @Test
        public void projectTest() {
            Point start = new Point(5.0, 5.0);
            Vector delta = new Vector(-10.0, -10.0);
            double projection = Utils.project(Point.origin(), start, delta);
            Point point = start.translate(delta.scale(projection));
            Assertions.assertEquals(0.0, point.getX(), epsilon);
            Assertions.assertEquals(0.0, point.getY(), epsilon);

            start = new Point(2.0, 5.0);
            delta = new Vector(0.0, -12.0);
            projection = Utils.project(Point.origin(), start, delta);
            point = start.translate(delta.scale(projection));
            Assertions.assertEquals(2.0, point.getX(), epsilon);
            Assertions.assertEquals(0.0, point.getY(), epsilon);

            start = new Point(12.0, -1.0);
            delta = new Vector(-36.0, 0.0);
            projection = Utils.project(new Point(-20, -18), start, delta);
            point = start.translate(delta.scale(projection));
            Assertions.assertEquals(-20.0, point.getX(), epsilon);
            Assertions.assertEquals(-1.0, point.getY(), epsilon);

            start = new Point(-25.0, -5.0);
            delta = new Vector(10.0, 10.0);
            projection = Utils.project(new Point(-25, 5.0), start, delta);
            point = start.translate(delta.scale(projection));
            Assertions.assertEquals(-20.0, point.getX(), epsilon);
            Assertions.assertEquals(0.0, point.getY(), epsilon);
        }
    }
}
