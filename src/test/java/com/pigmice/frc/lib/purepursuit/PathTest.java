package com.pigmice.frc.lib.purepursuit;

import java.util.List;

import com.pigmice.frc.lib.purepursuit.Path.Target;
import com.pigmice.frc.lib.utils.Point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathTest {
    private static final double epsilon = 1e-6;
    private static final Path path = createTestPath();

    @Test
    public void closestTest() {
        Target closest = path.closestPoint(Point.origin());

        Assertions.assertEquals(0.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(0.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(0.0, closest.velocity, epsilon);

        closest = path.closestPoint(new Point(5.0, 5.0));

        Assertions.assertEquals(0.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(5.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(1.0, closest.velocity, epsilon);

        closest = path.closestPoint(new Point(10.0, 10.0));

        Assertions.assertEquals(5.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(15.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(2.5, closest.velocity, epsilon);

        closest = path.closestPoint(new Point(5.0, 27.0));

        Assertions.assertEquals(5.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(30.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(1.0, closest.velocity, epsilon);

        closest = path.closestPoint(new Point(-1.0, 29.0));

        Assertions.assertEquals(0.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(30.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(0.0, closest.velocity, epsilon);
    }

    @Test
    public void singleTargetTest() {
        Target target = path.findTarget(Point.origin(), 5.0, path.new Target(Point.origin(), 0.0, 0));

        Assertions.assertEquals(0.0, target.position.getX(), epsilon);
        Assertions.assertEquals(5.0, target.position.getY(), epsilon);
        Assertions.assertEquals(1.0, target.velocity, epsilon);

        target = path.findTarget(Point.origin(), 10.0, path.new Target(new Point(0.0, 10.0), 2.0, 1));

        Assertions.assertEquals(0.0, target.position.getX(), epsilon);
        Assertions.assertEquals(10.0, target.position.getY(), epsilon);
        Assertions.assertEquals(2.0, target.velocity, epsilon);

        target = path.findTarget(new Point(10.0, 10.0), 11.0, path.new Target(new Point(0.0, 10.0), 2.0, 1));

        Assertions.assertEquals(10.0, target.position.getX(), epsilon);
        Assertions.assertEquals(21.0, target.position.getY(), epsilon);
        Assertions.assertEquals(2.9, target.velocity, epsilon);

        target = path.findTarget(new Point(10.0, 10.0), Math.sqrt(20 * 20 + 5 * 5),
                path.new Target(new Point(0.0, 10.0), 2.0, 1));

        Assertions.assertEquals(5.0, target.position.getX(), epsilon);
        Assertions.assertEquals(30.0, target.position.getY(), epsilon);
        Assertions.assertEquals(1.0, target.velocity, epsilon);

        target = path.findTarget(new Point(2.0, 30.0), 5.0,
                path.new Target(new Point(2.0, 30.0), 1.0, 3));

        Assertions.assertEquals(0.0, target.position.getX(), epsilon);
        Assertions.assertEquals(30.0, target.position.getY(), epsilon);
        Assertions.assertEquals(0.0, target.velocity, epsilon);
    }

    @Test
    public void multipleTargets() {
        Target target = path.findTarget(new Point(10.0, 10.0), Math.sqrt(62.5),
                path.new Target(new Point(0.0, 10.0), 2.0, 1));

        Assertions.assertEquals(7.5, target.position.getX(), epsilon);
        Assertions.assertEquals(17.5, target.position.getY(), epsilon);
        Assertions.assertEquals(2.75, target.velocity, epsilon);

        target = path.findTarget(new Point(5.0, 27.0), Math.sqrt(18), path.new Target(new Point(0.0, 10.0), 2.0, 1));

        Assertions.assertEquals(2.0, target.position.getX(), epsilon);
        Assertions.assertEquals(30.0, target.position.getY(), epsilon);
        Assertions.assertEquals(0.4, target.velocity, epsilon);
    }

    @Test
    public void noTargets() {
        Target target = path.findTarget(new Point(10.0, 10.0), 6.0, path.new Target(new Point(0.0, 10.0), 2.0, 1));

        Assertions.assertNull(target);

        target = path.findTarget(new Point(5.0, 25.0), 4.0, path.new Target(new Point(0.0, 10.0), 2.0, 1));

        Assertions.assertNull(target);
    }

    private static Path createTestPath() {
        List<Point> waypoints = List.of(
            Point.origin(),
            new Point(0.0, 10.0),
            new Point(10.0, 20.0),
            new Point(10.0, 30.0),
            new Point(0.0, 30.0)
        );

        List<Double> velocities = List.of(0.0, 2.0, 3.0, 2.0, 0.0);

        return new Path(waypoints, velocities);
    }
}
