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
            Assertions.assertEquals(Utils.lerp(-1.0, 0.0, -2.0, 5.0, 9.0), 7.0, epsilon);
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
            List<Point> intersections = Utils.circleLineIntersections(Point.origin(), new Point(1.0, 0.0), Point.origin(), 5.0);

            Assertions.assertEquals(2, intersections.size());

            Assertions.assertEquals(-5.0, intersections.get(0).getX(), epsilon);
            Assertions.assertEquals(0.0, intersections.get(0).getY(), epsilon);
            Assertions.assertEquals(5.0, intersections.get(1).getX(), epsilon);
            Assertions.assertEquals(0.0, intersections.get(1).getY(), epsilon);

            intersections = Utils.circleLineIntersections(new Point(3.0, -2.0), new Point(1.0, -4.0),
                    new Point(2.0, -3.0), 4.0);

            Assertions.assertEquals(2, intersections.size());

            Assertions.assertEquals(Math.sqrt(8.0) + 2, intersections.get(0).getX(), epsilon);
            Assertions.assertEquals(Math.sqrt(8.0) - 3, intersections.get(0).getY(), epsilon);
            Assertions.assertEquals(-Math.sqrt(8.0) + 2, intersections.get(1).getX(), epsilon);
            Assertions.assertEquals(-Math.sqrt(8.0) - 3, intersections.get(1).getY(), epsilon);
        }

        @Test
        public void noIntersections() {
            List<Point> intersections = Utils.circleLineIntersections(Point.origin(), new Point(1.0, 0.0),
                    new Point(0.0, 6.0), 5.0);

            Assertions.assertEquals(0, intersections.size());

            intersections = Utils.circleLineIntersections(new Point(5.0, 5.0), new Point(5.0, -5.0), Point.origin(), 4.0);

            Assertions.assertEquals(0, intersections.size());
        }
    }
}
