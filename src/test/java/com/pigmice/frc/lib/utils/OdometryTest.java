package com.pigmice.frc.lib.utils;

import com.pigmice.frc.lib.utils.Odometry.Pose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class OdometryTest {
    private static final double epsilon = 1e-6;

    @Test
    public void straightLineTest() {
        // At origin, facing forward
        Pose startingPose = new Pose(0.0, 0.0, 0.5 * Math.PI);
        Odometry odometer = new Odometry(startingPose);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(0.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getPose().getHeading(), epsilon);

        // Drive forwards 10
        odometer.update(10.0, 10.0, 0.5 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(10.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getPose().getHeading(), epsilon);

        // Stopped
        odometer.update(10.0, 10.0, 0.5 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(10.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getPose().getHeading(), epsilon);

        // Drive backwards 15
        odometer.update(-15.0, -15.0, 0.5 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(-15.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getPose().getHeading(), epsilon);
    }

    @Test
    public void circleTest() {
        // At origin, facing right
        Pose startingPose = new Pose(0.0, 0.0, 0.0);
        Odometry odometer = new Odometry(startingPose);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(0.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(0.0 * Math.PI, odometer.getPose().getHeading(), epsilon);

        // Drive forward half-circle, end facing left, to (0, 10)
        odometer.update(Math.PI * 4, Math.PI * 6, Math.PI);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(10.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(Math.PI, odometer.getPose().getHeading(), epsilon);

        // Drive backward quarter-circle, end facing backwards, to (10, 20)
        odometer.update(Math.PI * (4 - 5.5), Math.PI * (6 - 4.5), 1.5 * Math.PI);

        Assertions.assertEquals(10.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(20.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(1.5 * Math.PI, odometer.getPose().getHeading(), epsilon);
    }

    @Test
    public void setTest() {
        Pose startingPose = new Pose(0.0, 0.0, 0.25 * Math.PI);
        Odometry odometer = new Odometry(startingPose);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(0.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(0.25 * Math.PI, odometer.getPose().getHeading(), epsilon);

        Pose newPose = new Pose(50.0, -20.0, -1.5 * Math.PI);
        odometer.set(newPose, 25, -25);

        Assertions.assertEquals(50.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(-20.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(-1.5 * Math.PI, odometer.getPose().getHeading(), epsilon);

        // Check wheel distances were set by updating
        odometer.update(50, 0, -1.5 * Math.PI);

        Assertions.assertEquals(50.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(5.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(-1.5 * Math.PI, odometer.getPose().getHeading(), epsilon);
    }

    @Test
    public void flipTest() {
        Pose startingPose = new Pose(0.0, 0.0, 0.5 * Math.PI);
        Odometry odometer = new Odometry(startingPose);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(0.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getPose().getHeading(), epsilon);

        odometer.update(10.0, 10.0, 2.5 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPose().getX(), epsilon);
        Assertions.assertEquals(10.0, odometer.getPose().getY(), epsilon);
        Assertions.assertEquals(2.5 * Math.PI, odometer.getPose().getHeading(), epsilon);
    }
}
