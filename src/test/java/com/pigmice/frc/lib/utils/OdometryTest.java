package com.pigmice.frc.lib.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OdometryTest {
    private static final double epsilon = 1e-6;

    @Test
    public void straightLineTest() {
        // At origin, facing forward
        Odometry odometer = new Odometry(0.0, 0.0, 0.5 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(0.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getAngle(), epsilon);

        // Drive forwards 10
        odometer.update(10.0, 10.0, 0.5 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(10.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getAngle(), epsilon);

        // Drive backwards 15
        odometer.update(-15.0, -15.0, 0.5 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(-5.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(0.5 * Math.PI, odometer.getAngle(), epsilon);
    }

    @Test
    public void circleTest() {
        // At origin, facing right
        Odometry odometer = new Odometry(0.0, 0.0, 0.0 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(0.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(0.0 * Math.PI, odometer.getAngle(), epsilon);

        // Drive forward half-circle, end facing left, to (0, 10)
        odometer.update(Math.PI * 4, Math.PI * 6, Math.PI);

        Assertions.assertEquals(0.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(10.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(Math.PI, odometer.getAngle(), epsilon);

        // Drive backward quarter-circle, end facing backwards, to (10, 20)
        odometer.update(Math.PI * -5.5, Math.PI * -4.5, -0.5 * Math.PI);

        Assertions.assertEquals(10.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(20.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(-0.5 * Math.PI, odometer.getAngle(), epsilon);
    }

    @Test
    public void setTest() {
        Odometry odometer = new Odometry(0.0, 0.0, 0.25 * Math.PI);

        Assertions.assertEquals(0.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(0.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(0.25 * Math.PI, odometer.getAngle(), epsilon);

        odometer.set(50.0, -20.0, -1.5 * Math.PI, 25, -25);

        Assertions.assertEquals(50.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(-20.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(-1.5 * Math.PI, odometer.getAngle(), epsilon);

        // Check wheel distances were set by updating
        odometer.update(50, 0, -1.5 * Math.PI);

        Assertions.assertEquals(50.0, odometer.getPosition().getX(), epsilon);
        Assertions.assertEquals(5.0, odometer.getPosition().getY(), epsilon);
        Assertions.assertEquals(-1.5 * Math.PI, odometer.getAngle(), epsilon);
    }
}
