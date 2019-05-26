package com.pigmice.frc.lib.motion;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.pigmice.frc.lib.plots.TimePlot;

public class StaticProfileTest {

    private static final double epsilon = 1e-6;

    private static void plotProfile(StaticProfile profile, String name) {
        plotProfile(profile, name, profile.getDuration(), 0.025);
    }

    private static void plotProfile(StaticProfile profile, String name, double duration, double step) {
        if (TimePlot.shouldGraph("profiles")) {
            TimePlot profilePlot = new TimePlot(name, "Velocity", profile::getVelocity, duration, step);
            profile.reset();
            profilePlot.addSeries("Position", profile::getPosition, step);
            profile.reset();
            profilePlot.addSeries("Acceleration", profile::getAcceleration, step);
            profilePlot.save("profiles");
        }
    }

    public static class MaxVelocityEqualityTest {
        // Check to make sure comparisons to maximum velocity handled floating point
        // error
        private static StaticProfile fpe = new StaticProfile(0.0, 0.7789, 0.9, 0.65, 1.1, 1.1);

        @Before
        public void setup() {
            fpe.reset();
        }

        @Test
        public void test() {
            Assert.assertEquals(0.198, fpe.getVelocity(0.18), epsilon);
            Assert.assertEquals(0.663599, fpe.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            fpe.reset();
            plotProfile(fpe, "fpe");
        }
    }

    public static class TrapezoidalTest {
        // Pure trapezoid
        private static StaticProfile trapezoidalProfile = new StaticProfile(0.0, 0.0, 16.0, 4.0, 2.0, 1.0);

        @Before
        public void setup() {
            trapezoidalProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Acceleration
            Assert.assertEquals(0.0, trapezoidalProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(3.0, trapezoidalProfile.getVelocity(1.5), epsilon);

            // Max velocity
            Assert.assertEquals(4.0, trapezoidalProfile.getVelocity(2.0), epsilon);
            Assert.assertEquals(4.0, trapezoidalProfile.getVelocity(3.0), epsilon);

            // Deceleration
            Assert.assertEquals(1.0, trapezoidalProfile.getVelocity(6.0), epsilon);
            Assert.assertEquals(0.5, trapezoidalProfile.getVelocity(6.5), epsilon);
            Assert.assertEquals(0.0, trapezoidalProfile.getVelocity(7.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Acceleration
            Assert.assertEquals(0.0, trapezoidalProfile.getPosition(0.0), epsilon);
            Assert.assertEquals(2.25, trapezoidalProfile.getPosition(1.5), epsilon);

            // Max velocity
            Assert.assertEquals(4.0, trapezoidalProfile.getPosition(2.0), epsilon);
            Assert.assertEquals(8.0, trapezoidalProfile.getPosition(3.0), epsilon);

            // Deceleration
            Assert.assertEquals(15.5, trapezoidalProfile.getPosition(6.0), epsilon);
            Assert.assertEquals(15.875, trapezoidalProfile.getPosition(6.5), epsilon);
            Assert.assertEquals(16.0, trapezoidalProfile.getPosition(7.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Acceleration
            Assert.assertEquals(2.0, trapezoidalProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(2.0, trapezoidalProfile.getAcceleration(1.5), epsilon);

            // Max velocity
            Assert.assertEquals(0.0, trapezoidalProfile.getAcceleration(2.0), epsilon);
            Assert.assertEquals(0.0, trapezoidalProfile.getAcceleration(2.99), epsilon);

            // Deceleration
            Assert.assertEquals(-1.0, trapezoidalProfile.getAcceleration(4.0), epsilon);
            Assert.assertEquals(-1.0, trapezoidalProfile.getAcceleration(6.5), epsilon);
            Assert.assertEquals(0.0, trapezoidalProfile.getAcceleration(7.0), epsilon);
        }

        @Test
        public void getSetpoint() {
            Setpoint actual = trapezoidalProfile.getSetpoint(6.5);

            Assert.assertEquals(-1.0, actual.getAcceleration(), epsilon);
            Assert.assertEquals(0.5, actual.getVelocity(), epsilon);
            Assert.assertEquals(15.875, actual.getPosition(), epsilon);
            Assert.assertEquals(0.0, actual.getCurvature(), epsilon);
            Assert.assertEquals(0.0, actual.getHeading(), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(7.0, trapezoidalProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            trapezoidalProfile.reset();
            plotProfile(trapezoidalProfile, "trapezoid");
        }
    }

    public static class WrongDirectionTest {
        // Trapezoid with leading wrong direction
        private static StaticProfile wrongDirectionProfile = new StaticProfile(-1, 0.5, 16.0, 4.0, 2.0, 1.0);

        @Before
        public void setup() {
            wrongDirectionProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Direction correction
            Assert.assertEquals(-1.0, wrongDirectionProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(-0.5, wrongDirectionProfile.getVelocity(0.5), epsilon);

            // Acceleration
            Assert.assertEquals(0.0, wrongDirectionProfile.getVelocity(1.0), epsilon);
            Assert.assertEquals(3.0, wrongDirectionProfile.getVelocity(2.5), epsilon);

            // Max velocity
            Assert.assertEquals(4.0, wrongDirectionProfile.getVelocity(3.0), epsilon);
            Assert.assertEquals(4.0, wrongDirectionProfile.getVelocity(4.0), epsilon);

            // Deceleration
            Assert.assertEquals(1.0, wrongDirectionProfile.getVelocity(7.0), epsilon);
            Assert.assertEquals(0.5, wrongDirectionProfile.getVelocity(7.5), epsilon);
            Assert.assertEquals(0.0, wrongDirectionProfile.getVelocity(8.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Direction correction
            Assert.assertEquals(0.5, wrongDirectionProfile.getPosition(0.0), epsilon);
            Assert.assertEquals(0.125, wrongDirectionProfile.getPosition(0.5), epsilon);

            // Acceleration
            Assert.assertEquals(0.0, wrongDirectionProfile.getPosition(1.0), epsilon);
            Assert.assertEquals(2.25, wrongDirectionProfile.getPosition(2.5), epsilon);

            // Max velocity
            Assert.assertEquals(4.0, wrongDirectionProfile.getPosition(3.0), epsilon);
            Assert.assertEquals(8.0, wrongDirectionProfile.getPosition(4.0), epsilon);

            // Deceleration
            Assert.assertEquals(15.5, wrongDirectionProfile.getPosition(7.0), epsilon);
            Assert.assertEquals(15.875, wrongDirectionProfile.getPosition(7.5), epsilon);
            Assert.assertEquals(16.0, wrongDirectionProfile.getPosition(8.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Direction correction
            Assert.assertEquals(1.0, wrongDirectionProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(1.0, wrongDirectionProfile.getAcceleration(0.5), epsilon);

            // Acceleration
            Assert.assertEquals(2.0, wrongDirectionProfile.getAcceleration(1.0), epsilon);
            Assert.assertEquals(2.0, wrongDirectionProfile.getAcceleration(2.5), epsilon);

            // Max velocity
            Assert.assertEquals(0.0, wrongDirectionProfile.getAcceleration(3.0), epsilon);
            Assert.assertEquals(0.0, wrongDirectionProfile.getAcceleration(3.99), epsilon);

            // Deceleration
            Assert.assertEquals(-1.0, wrongDirectionProfile.getAcceleration(7.0), epsilon);
            Assert.assertEquals(-1.0, wrongDirectionProfile.getAcceleration(7.5), epsilon);
            Assert.assertEquals(0.0, wrongDirectionProfile.getAcceleration(8.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(8.0, wrongDirectionProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            wrongDirectionProfile.reset();
            plotProfile(wrongDirectionProfile, "wrongDir");
        }
    }

    public static class TriangularTest {
        // Pure triangle
        private static StaticProfile triangularProfile = new StaticProfile(0.0, 0.0, 35.69, 9.0, 2.0, 2.15);

        @Before
        public void setup() {
            triangularProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Acceleration
            Assert.assertEquals(0.0, triangularProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(8.0, triangularProfile.getVelocity(4.0), epsilon);

            // Max velocity
            Assert.assertEquals(8.6, triangularProfile.getVelocity(4.3), epsilon);

            // Deceleration
            Assert.assertEquals(4.3, triangularProfile.getVelocity(6.3), epsilon);
            Assert.assertEquals(0.0, triangularProfile.getVelocity(8.3), epsilon);
        }

        @Test
        public void getPosition() {
            // Acceleration
            Assert.assertEquals(0.0, triangularProfile.getPosition(0.0), epsilon);

            // Max velocity
            Assert.assertEquals(18.49, triangularProfile.getPosition(4.3), epsilon);

            // Deceleration
            Assert.assertEquals(35.69, triangularProfile.getPosition(8.3), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Acceleration
            Assert.assertEquals(2.0, triangularProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(2.0, triangularProfile.getAcceleration(4.2), epsilon);

            // Deceleration
            Assert.assertEquals(-2.15, triangularProfile.getAcceleration(4.4), epsilon);
            Assert.assertEquals(-2.15, triangularProfile.getAcceleration(8.0), epsilon);
            Assert.assertEquals(0.0, triangularProfile.getAcceleration(8.3), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(8.3, triangularProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            triangularProfile.reset();
            plotProfile(triangularProfile, "triangle");
        }
    }

    public static class PartialTriangleTest {
        // Starts partway into triangle, leading corner chopped off
        private static StaticProfile quadrilateralProfile = new StaticProfile(1.0, 0.0, 5.5, 5.0, 1.0, 0.5);

        @Before
        public void setup() {
            quadrilateralProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Acceleration
            Assert.assertEquals(1.0, quadrilateralProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(1.5, quadrilateralProfile.getVelocity(0.5), epsilon);

            // Max velocity
            Assert.assertEquals(2.0, quadrilateralProfile.getVelocity(1.0), epsilon);

            // Deceleration
            Assert.assertEquals(1.0, quadrilateralProfile.getVelocity(3.0), epsilon);
            Assert.assertEquals(0.0, quadrilateralProfile.getVelocity(5.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Acceleration
            Assert.assertEquals(0.0, quadrilateralProfile.getPosition(0.0), epsilon);

            // Max velocity
            Assert.assertEquals(1.5, quadrilateralProfile.getPosition(1.0), epsilon);

            // Deceleration
            Assert.assertEquals(5.5, quadrilateralProfile.getPosition(5.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Acceleration
            Assert.assertEquals(1.0, quadrilateralProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(1.0, quadrilateralProfile.getAcceleration(0.9), epsilon);

            // Deceleration
            Assert.assertEquals(-0.5, quadrilateralProfile.getAcceleration(1.1), epsilon);
            Assert.assertEquals(-0.5, quadrilateralProfile.getAcceleration(4.9), epsilon);
            Assert.assertEquals(0.0, quadrilateralProfile.getAcceleration(5.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(5.0, quadrilateralProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            quadrilateralProfile.reset();
            plotProfile(quadrilateralProfile, "partialTriangle");
        }
    }

    public static class ReverseTriangularTest {
        // Negative triangle profile, reverse direction
        private static StaticProfile reverseTriangleProfile = new StaticProfile(0.0, 0.0, -35.69, 9.0, 2.0, 2.15);

        @Before
        public void setup() {
            reverseTriangleProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Acceleration
            Assert.assertEquals(0.0, reverseTriangleProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(-8.0, reverseTriangleProfile.getVelocity(4.0), epsilon);

            // Max velocity
            Assert.assertEquals(-8.6, reverseTriangleProfile.getVelocity(4.3), epsilon);

            // Deceleration
            Assert.assertEquals(-4.3, reverseTriangleProfile.getVelocity(6.3), epsilon);
            Assert.assertEquals(-0.0, reverseTriangleProfile.getVelocity(8.3), epsilon);
        }

        @Test
        public void getPosition() {
            // Acceleration
            Assert.assertEquals(0.0, reverseTriangleProfile.getPosition(0.0), epsilon);

            // Max velocity
            Assert.assertEquals(-18.49, reverseTriangleProfile.getPosition(4.3), epsilon);

            // Deceleration
            Assert.assertEquals(-35.69, reverseTriangleProfile.getPosition(8.3), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Acceleration
            Assert.assertEquals(-2.0, reverseTriangleProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(-2.0, reverseTriangleProfile.getAcceleration(4.2), epsilon);

            // Deceleration
            Assert.assertEquals(2.15, reverseTriangleProfile.getAcceleration(4.4), epsilon);
            Assert.assertEquals(2.15, reverseTriangleProfile.getAcceleration(4.9), epsilon);
            Assert.assertEquals(0.0, reverseTriangleProfile.getAcceleration(8.3), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(8.3, reverseTriangleProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            reverseTriangleProfile.reset();
            plotProfile(reverseTriangleProfile, "reverseTriangle");
        }
    }

    public static class PartialTrapezoidTest {
        // Starts partway into trapezoid, leading corner chopped off
        private static StaticProfile pentagonProfile = new StaticProfile(2.5, 0.0, 11.25, 5.0, 2.5, 5.0);

        @Before
        public void setup() {
            pentagonProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Acceleration
            Assert.assertEquals(2.5, pentagonProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(3.75, pentagonProfile.getVelocity(0.5), epsilon);

            // Max velocity
            Assert.assertEquals(5.0, pentagonProfile.getVelocity(1.0), epsilon);
            Assert.assertEquals(5.0, pentagonProfile.getVelocity(2.0), epsilon);

            // Deceleration
            Assert.assertEquals(0.0, pentagonProfile.getVelocity(3.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Acceleration
            Assert.assertEquals(0.0, pentagonProfile.getPosition(0.0), epsilon);
            Assert.assertEquals(1.5625, pentagonProfile.getPosition(0.5), epsilon);

            // Max velocity
            Assert.assertEquals(3.75, pentagonProfile.getPosition(1.0), epsilon);
            Assert.assertEquals(8.75, pentagonProfile.getPosition(2.0), epsilon);

            // Deceleration
            Assert.assertEquals(10.625, pentagonProfile.getPosition(2.5), epsilon);
            Assert.assertEquals(11.25, pentagonProfile.getPosition(3.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Acceleration
            Assert.assertEquals(2.5, pentagonProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(2.5, pentagonProfile.getAcceleration(0.5), epsilon);

            // Max velocity
            Assert.assertEquals(0.0, pentagonProfile.getAcceleration(1.0), epsilon);
            Assert.assertEquals(0.0, pentagonProfile.getAcceleration(1.99), epsilon);

            // Deceleration
            Assert.assertEquals(-5.0, pentagonProfile.getAcceleration(2.5), epsilon);
            Assert.assertEquals(0.0, pentagonProfile.getAcceleration(3.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(3.0, pentagonProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            pentagonProfile.reset();
            plotProfile(pentagonProfile, "partialTrapezoid");
        }
    }

    public static class DecelerationTest {
        // Needs to decelerate directly from start of profile
        private static StaticProfile decelerationProfile = new StaticProfile(4.0, 4.0, 12.0, 6.0, 2.0, 1.0);

        @Before
        public void setup() {
            decelerationProfile.reset();
        }

        @Test
        public void getVelocity() {

            // Deceleration
            Assert.assertEquals(4.0, decelerationProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(2.0, decelerationProfile.getVelocity(2.0), epsilon);
            Assert.assertEquals(0.0, decelerationProfile.getVelocity(4.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Deceleration
            Assert.assertEquals(4.0, decelerationProfile.getPosition(0.0), epsilon);
            Assert.assertEquals(10.0, decelerationProfile.getPosition(2.0), epsilon);
            Assert.assertEquals(12.0, decelerationProfile.getPosition(4.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Deceleration
            Assert.assertEquals(-1.0, decelerationProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(-1.0, decelerationProfile.getAcceleration(2.0), epsilon);
            Assert.assertEquals(0.0, decelerationProfile.getAcceleration(4.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(4.0, decelerationProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            decelerationProfile.reset();
            plotProfile(decelerationProfile, "deceleration");
        }
    }

    public static class MaxSpeedToDecelerationTest {
        // Starts at max speed, decelerates after max speed chunk
        private static StaticProfile maxSpeedToDecelerationProfile = new StaticProfile(8.0, 0.0, 40.0, 8.0, 2.0, 1.0);

        @Before
        public void setup() {
            maxSpeedToDecelerationProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Max velocity
            Assert.assertEquals(8.0, maxSpeedToDecelerationProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(8.0, maxSpeedToDecelerationProfile.getVelocity(1.0), epsilon);

            // Deceleration
            Assert.assertEquals(7.0, maxSpeedToDecelerationProfile.getVelocity(2.0), epsilon);
            Assert.assertEquals(2.5, maxSpeedToDecelerationProfile.getVelocity(6.5), epsilon);
            Assert.assertEquals(0.0, maxSpeedToDecelerationProfile.getVelocity(9.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Max velocity
            Assert.assertEquals(0.0, maxSpeedToDecelerationProfile.getPosition(0.0), epsilon);
            Assert.assertEquals(8.0, maxSpeedToDecelerationProfile.getPosition(1.0), epsilon);

            // Deceleration
            Assert.assertEquals(22.0, maxSpeedToDecelerationProfile.getPosition(3.0), epsilon);
            Assert.assertEquals(38.0, maxSpeedToDecelerationProfile.getPosition(7.0), epsilon);
            Assert.assertEquals(40.0, maxSpeedToDecelerationProfile.getPosition(9.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Max velocity
            Assert.assertEquals(0.0, maxSpeedToDecelerationProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(0.0, maxSpeedToDecelerationProfile.getAcceleration(0.99), epsilon);

            // Deceleration
            Assert.assertEquals(-1.0, maxSpeedToDecelerationProfile.getAcceleration(3.0), epsilon);
            Assert.assertEquals(-1.0, maxSpeedToDecelerationProfile.getAcceleration(7.0), epsilon);
            Assert.assertEquals(0.0, maxSpeedToDecelerationProfile.getAcceleration(9.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(9.0, maxSpeedToDecelerationProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            maxSpeedToDecelerationProfile.reset();
            plotProfile(maxSpeedToDecelerationProfile, "maxSpeedDecel");
        }
    }

    public static class HighSpeedDecelerationTest {
        // Starts above max speed, decelerates to max speed, and then to zero
        private static StaticProfile highSpeedDecelerationProfile = new StaticProfile(10.0, 0.0, 58.0, 8.0, 2.0, 1.0);

        @Before
        public void setup() {
            highSpeedDecelerationProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Decel to max speed
            Assert.assertEquals(10.0, highSpeedDecelerationProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(9.0, highSpeedDecelerationProfile.getVelocity(1.0), epsilon);

            // Max velocity
            Assert.assertEquals(8.0, highSpeedDecelerationProfile.getVelocity(2.0), epsilon);
            Assert.assertEquals(8.0, highSpeedDecelerationProfile.getVelocity(3.0), epsilon);

            // Deceleration
            Assert.assertEquals(7.0, highSpeedDecelerationProfile.getVelocity(4.0), epsilon);
            Assert.assertEquals(2.5, highSpeedDecelerationProfile.getVelocity(8.5), epsilon);
            Assert.assertEquals(0.0, highSpeedDecelerationProfile.getVelocity(11.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Decel to max speed
            Assert.assertEquals(0.0, highSpeedDecelerationProfile.getPosition(0.0), epsilon);
            Assert.assertEquals(9.5, highSpeedDecelerationProfile.getPosition(1.0), epsilon);

            // Max velocity
            Assert.assertEquals(18.0, highSpeedDecelerationProfile.getPosition(2.0), epsilon);
            Assert.assertEquals(26.0, highSpeedDecelerationProfile.getPosition(3.0), epsilon);

            // Deceleration
            Assert.assertEquals(40.0, highSpeedDecelerationProfile.getPosition(5.0), epsilon);
            Assert.assertEquals(56.0, highSpeedDecelerationProfile.getPosition(9.0), epsilon);
            Assert.assertEquals(58.0, highSpeedDecelerationProfile.getPosition(11.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Decel to max speed
            Assert.assertEquals(-1.0, highSpeedDecelerationProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(-1.0, highSpeedDecelerationProfile.getAcceleration(1.99), epsilon);

            // Max velocity
            Assert.assertEquals(0.0, highSpeedDecelerationProfile.getAcceleration(2.0), epsilon);
            Assert.assertEquals(0.0, highSpeedDecelerationProfile.getAcceleration(2.99), epsilon);

            // Deceleration
            Assert.assertEquals(-1.0, highSpeedDecelerationProfile.getAcceleration(5.0), epsilon);
            Assert.assertEquals(-1.0, highSpeedDecelerationProfile.getAcceleration(9.0), epsilon);
            Assert.assertEquals(0.0, highSpeedDecelerationProfile.getAcceleration(11.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(11.0, highSpeedDecelerationProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            highSpeedDecelerationProfile.reset();
            plotProfile(highSpeedDecelerationProfile, "highSpeedDecel");
        }
    }

    public static class OvershootFromMaxSpeedTest {
        // Starts at max speed, overshoots while decelerating - needs to backtrack
        private static StaticProfile overshootFromMaxSpeed = new StaticProfile(10.0, 0.0, 34.0, 10.0, 1.0, 1.0);

        @Before
        public void setup() {
            overshootFromMaxSpeed.reset();
        }

        @Test
        public void getVelocity() {
            // Deceleration
            Assert.assertEquals(10.0, overshootFromMaxSpeed.getVelocity(0.0), epsilon);
            Assert.assertEquals(0.0, overshootFromMaxSpeed.getVelocity(10.0), epsilon);

            // Backtrack triangle
            Assert.assertEquals(-4.0, overshootFromMaxSpeed.getVelocity(14.0), epsilon);
            Assert.assertEquals(0.0, overshootFromMaxSpeed.getVelocity(18.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Deceleration
            Assert.assertEquals(0.0, overshootFromMaxSpeed.getPosition(0.0), epsilon);
            Assert.assertEquals(50.0, overshootFromMaxSpeed.getPosition(10.0), epsilon);

            // Backtrack triangle
            Assert.assertEquals(42.0, overshootFromMaxSpeed.getPosition(14.0), epsilon);
            Assert.assertEquals(34.0, overshootFromMaxSpeed.getPosition(18.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Deceleration
            Assert.assertEquals(-1.0, overshootFromMaxSpeed.getAcceleration(0.0), epsilon);
            Assert.assertEquals(-1.0, overshootFromMaxSpeed.getAcceleration(9.99), epsilon);

            // Backtrack triangle
            Assert.assertEquals(-1.0, overshootFromMaxSpeed.getAcceleration(13.5), epsilon);
            Assert.assertEquals(1.0, overshootFromMaxSpeed.getAcceleration(17.9), epsilon);
            Assert.assertEquals(0.0, overshootFromMaxSpeed.getAcceleration(18.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(18.0, overshootFromMaxSpeed.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            overshootFromMaxSpeed.reset();
            plotProfile(overshootFromMaxSpeed, "overshootMaxSpeed");
        }
    }

    public static class HighSpeedOvershootTest {
        // Starts at max speed, overshoots while decelerating - needs to backtrack
        private static StaticProfile highSpeedOvershoot = new StaticProfile(50.0, 0.0, 1000.0, 10.0, 1.0, 1.0);

        @Before
        public void setup() {
            highSpeedOvershoot.reset();
        }

        @Test
        public void getVelocity() {
            // Deceleration
            Assert.assertEquals(50.0, highSpeedOvershoot.getVelocity(0.0), epsilon);
            Assert.assertEquals(0.0, highSpeedOvershoot.getVelocity(50.0), epsilon);

            // Backtrack trapezoid
            Assert.assertEquals(-10.0, highSpeedOvershoot.getVelocity(60.0), epsilon);
            Assert.assertEquals(-10.0, highSpeedOvershoot.getVelocity(75.0), epsilon);
            Assert.assertEquals(-5.0, highSpeedOvershoot.getVelocity(80.0), epsilon);
            Assert.assertEquals(0.0, highSpeedOvershoot.getVelocity(85.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Deceleration
            Assert.assertEquals(0.0, highSpeedOvershoot.getPosition(0.0), epsilon);
            Assert.assertEquals(1250.0, highSpeedOvershoot.getPosition(50.0), epsilon);

            // Backtrack trapezoid
            Assert.assertEquals(1200.0, highSpeedOvershoot.getPosition(60.0), epsilon);
            Assert.assertEquals(1050.0, highSpeedOvershoot.getPosition(75.0), epsilon);
            Assert.assertEquals(1000.0, highSpeedOvershoot.getPosition(85.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Deceleration
            Assert.assertEquals(-1.0, highSpeedOvershoot.getAcceleration(0.0), epsilon);
            Assert.assertEquals(-1.0, highSpeedOvershoot.getAcceleration(49.0), epsilon);

            // Backtrack trapezoid
            Assert.assertEquals(0.0, highSpeedOvershoot.getAcceleration(60.0), epsilon);
            Assert.assertEquals(1.0, highSpeedOvershoot.getAcceleration(80.0), epsilon);
            Assert.assertEquals(1.0, highSpeedOvershoot.getAcceleration(84.0), epsilon);
            Assert.assertEquals(0.0, highSpeedOvershoot.getAcceleration(85.0), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(85.0, highSpeedOvershoot.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            highSpeedOvershoot.reset();
            plotProfile(highSpeedOvershoot, "highspeedOvershoot");
        }
    }

    public static class OverrunTimeTest {
        // Pure trapezoid
        private static StaticProfile overrunTimeProfile = new StaticProfile(0.0, 0.0, 16.0, 4.0, 2.0, 1.0);

        @Before
        public void setup() {
            overrunTimeProfile.reset();
        }

        @Test
        public void getVelocity() {
            // Acceleration
            Assert.assertEquals(0.0, overrunTimeProfile.getVelocity(0.0), epsilon);
            Assert.assertEquals(3.0, overrunTimeProfile.getVelocity(1.5), epsilon);

            // Max velocity
            Assert.assertEquals(4.0, overrunTimeProfile.getVelocity(2.0), epsilon);
            Assert.assertEquals(4.0, overrunTimeProfile.getVelocity(3.0), epsilon);

            // Deceleration
            Assert.assertEquals(1.0, overrunTimeProfile.getVelocity(6.0), epsilon);
            Assert.assertEquals(0.5, overrunTimeProfile.getVelocity(6.5), epsilon);
            Assert.assertEquals(0.0, overrunTimeProfile.getVelocity(7.0), epsilon);

            // End
            Assert.assertEquals(0.0, overrunTimeProfile.getVelocity(10.0), epsilon);
            Assert.assertEquals(0.0, overrunTimeProfile.getVelocity(30.0), epsilon);
        }

        @Test
        public void getPosition() {
            // Acceleration
            Assert.assertEquals(0.0, overrunTimeProfile.getPosition(0.0), epsilon);
            Assert.assertEquals(2.25, overrunTimeProfile.getPosition(1.5), epsilon);

            // Max velocity
            Assert.assertEquals(4.0, overrunTimeProfile.getPosition(2.0), epsilon);
            Assert.assertEquals(8.0, overrunTimeProfile.getPosition(3.0), epsilon);

            // Deceleration
            Assert.assertEquals(15.5, overrunTimeProfile.getPosition(6.0), epsilon);
            Assert.assertEquals(15.875, overrunTimeProfile.getPosition(6.5), epsilon);
            Assert.assertEquals(16.0, overrunTimeProfile.getPosition(7.0), epsilon);

            // End
            Assert.assertEquals(16.0, overrunTimeProfile.getPosition(8.0), epsilon);
            Assert.assertEquals(16.0, overrunTimeProfile.getPosition(35.0), epsilon);
        }

        @Test
        public void getAcceleration() {
            // Acceleration
            Assert.assertEquals(2.0, overrunTimeProfile.getAcceleration(0.0), epsilon);
            Assert.assertEquals(2.0, overrunTimeProfile.getAcceleration(1.5), epsilon);

            // Max velocity
            Assert.assertEquals(0.0, overrunTimeProfile.getAcceleration(2.0), epsilon);
            Assert.assertEquals(0.0, overrunTimeProfile.getAcceleration(2.99), epsilon);

            // Deceleration
            Assert.assertEquals(-1.0, overrunTimeProfile.getAcceleration(4.0), epsilon);
            Assert.assertEquals(-1.0, overrunTimeProfile.getAcceleration(6.5), epsilon);
            Assert.assertEquals(0.0, overrunTimeProfile.getAcceleration(7.0), epsilon);

            // End
            Assert.assertEquals(0.0, overrunTimeProfile.getAcceleration(8.0), epsilon);
            Assert.assertEquals(0.0, overrunTimeProfile.getAcceleration(41.56), epsilon);
        }

        @Test
        public void getDuration() {
            Assert.assertEquals(7.0, overrunTimeProfile.getDuration(), epsilon);
        }

        @AfterClass
        public static void plot() {
            overrunTimeProfile.reset();
            plotProfile(overrunTimeProfile, "overrunTime", 12.0, 0.025);
        }
    }
}
