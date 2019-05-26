package com.pigmice.frc.lib.utils;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
    public static class LerpTest {
        private static final double epsilon = 1e-6;

        @Test
        public void lerpEqualRanges() {
            Assert.assertEquals(2.0, Utils.lerp(2.0, 1.0, 3.0, 1.0, 3.0), epsilon);
        }

        @Test
        public void lerpOffsetEqualRanges() {
            Assert.assertEquals(7.0, Utils.lerp(6.0, 5.0, 7.0, 6.0, 8.0), epsilon);
        }

        @Test
        public void lerpUnequalPositiveRanges() {
            Assert.assertEquals(3.0, Utils.lerp(3.0, 2.0, 6.0, 1.0, 9.0), epsilon);
        }

        @Test
        public void lerpEqualNegativeRanges() {
            Assert.assertEquals(-4.0, Utils.lerp(-1.0, 0.0, -2.0, -3.0, -5.0), epsilon);
        }

        @Test
        public void lerpUnequalNegativeRanges() {
            Assert.assertEquals(-5.0, Utils.lerp(-1.0, 0.0, -2.0, -3.0, -7.0), epsilon);
        }

        @Test
        public void lerpMixedSignRanges() {
            Assert.assertEquals(Utils.lerp(-1.0, 0.0, -2.0, 5.0, 9.0), 7.0, epsilon);
        }
    }

    public static class AlmostEqualsTest {
        private static final double epsilon = 1e-6;

        @Test
        public void equal() {
            Assert.assertTrue(Utils.almostEquals(1.0000001, 1.000000, epsilon));
            Assert.assertTrue(Utils.almostEquals(1 / 3, 1 / 3 + 1e-7, epsilon));

            Assert.assertTrue(Utils.almostEquals(-1.0000001, -1.000000, epsilon));
            Assert.assertTrue(Utils.almostEquals(-0.0, -0.000000001, epsilon));

            Assert.assertTrue(Utils.almostEquals(-0.0, +0.0, epsilon));
        }

        @Test
        public void notEqual() {
            Assert.assertFalse(Utils.almostEquals(1.0000001, 2, epsilon));
            Assert.assertFalse(Utils.almostEquals(-0.0, 5.0, epsilon));
            Assert.assertFalse(Utils.almostEquals(-2.0, 2.0, epsilon));

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

            Assert.assertEquals(6, Utils.binarySearch(data, 12.0));
            Assert.assertEquals(0, Utils.binarySearch(data, -1.0));
            Assert.assertEquals(3, Utils.binarySearch(data, 9.0));
            Assert.assertEquals(6, Utils.binarySearch(data, 50.0));
        }
    }
}
