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
    public void endPointTest() {
        Point end = path.end();

        Assertions.assertEquals(0.0, end.getX(), epsilon);
        Assertions.assertEquals(30.0, end.getY(), epsilon);
    }

    @Test
    public void closestTest() {
        Target closest = path.closestPoint(Point.origin());

        Assertions.assertEquals(0.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(0.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(0.0, closest.velocity, epsilon);
        Assertions.assertEquals(0.2, closest.acceleration, epsilon);
        Assertions.assertEquals(0, closest.segment);

        closest = path.closestPoint(new Point(5.0, 5.0));

        Assertions.assertEquals(0.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(5.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(1.0, closest.velocity, epsilon);
        Assertions.assertEquals(0.2, closest.acceleration, epsilon);
        Assertions.assertEquals(0, closest.segment);

        closest = path.closestPoint(new Point(10.0, 10.0));

        Assertions.assertEquals(5.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(15.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(2.5, closest.velocity, epsilon);
        Assertions.assertEquals(2.5 / Math.sqrt(200), closest.acceleration, epsilon);
        Assertions.assertEquals(1, closest.segment);

        closest = path.closestPoint(new Point(5.0, 27.0));

        Assertions.assertEquals(5.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(30.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(1.0, closest.velocity, epsilon);
        Assertions.assertEquals(-0.2, closest.acceleration, epsilon);
        Assertions.assertEquals(3, closest.segment);

        closest = path.closestPoint(new Point(-1.0, 29.0));

        Assertions.assertEquals(0.0, closest.position.getX(), epsilon);
        Assertions.assertEquals(30.0, closest.position.getY(), epsilon);
        Assertions.assertEquals(0.0, closest.velocity, epsilon);
        Assertions.assertEquals(-0.2, closest.acceleration, epsilon);
        Assertions.assertEquals(3, closest.segment);
    }

    @Test
    public void singleTargetTest() {
        Target target = path.findTarget(Point.origin(), 5.0, path.new Target(Point.origin(), 0.0, 0.0, 0));

        Assertions.assertEquals(0.0, target.position.getX(), epsilon);
        Assertions.assertEquals(5.0, target.position.getY(), epsilon);
        Assertions.assertEquals(1.0, target.velocity, epsilon);
        Assertions.assertEquals(0.2, target.acceleration, epsilon);
        Assertions.assertEquals(0, target.segment);

        target = path.findTarget(Point.origin(), 10.0, path.new Target(new Point(0.0, 10.0), 2.0, 0.0, 1));

        Assertions.assertEquals(0.0, target.position.getX(), epsilon);
        Assertions.assertEquals(10.0, target.position.getY(), epsilon);
        Assertions.assertEquals(2.0, target.velocity, epsilon);
        Assertions.assertEquals(2.5 / Math.sqrt(200), target.acceleration, epsilon);
        Assertions.assertEquals(1, target.segment);

        target = path.findTarget(new Point(10.0, 10.0), 11.0, path.new Target(new Point(0.0, 10.0), 2.0, 0.0, 1));

        Assertions.assertEquals(10.0, target.position.getX(), epsilon);
        Assertions.assertEquals(21.0, target.position.getY(), epsilon);
        Assertions.assertEquals(2.9, target.velocity, epsilon);
        Assertions.assertEquals(-0.25, target.acceleration, epsilon);
        Assertions.assertEquals(2, target.segment);

        target = path.findTarget(new Point(10.0, 10.0), Math.sqrt(20 * 20 + 5 * 5),
                path.new Target(new Point(0.0, 10.0), 2.0, 0.0, 1));

        Assertions.assertEquals(5.0, target.position.getX(), epsilon);
        Assertions.assertEquals(30.0, target.position.getY(), epsilon);
        Assertions.assertEquals(1.0, target.velocity, epsilon);
        Assertions.assertEquals(-0.2, target.acceleration, epsilon);
        Assertions.assertEquals(3, target.segment);

        target = path.findTarget(new Point(2.0, 28.0), 5.0, path.new Target(new Point(2.0, 30.0), 1.0, 0.0, 3));

        Assertions.assertEquals(2 - Math.sqrt(5 * 5 - 2 * 2), target.position.getX(), epsilon);
        Assertions.assertEquals(30.0, target.position.getY(), epsilon);
        Assertions.assertEquals(0.0, target.velocity, epsilon);
        Assertions.assertEquals(0.0, target.acceleration, epsilon);
        Assertions.assertEquals(3, target.segment);
    }

    @Test
    public void multipleTargets() {
        Target target = path.findTarget(new Point(10.0, 10.0), Math.sqrt(62.5),
                path.new Target(new Point(0.0, 10.0), 2.0, 0.0, 1));

        Assertions.assertEquals(7.5, target.position.getX(), epsilon);
        Assertions.assertEquals(17.5, target.position.getY(), epsilon);
        Assertions.assertEquals(2.75, target.velocity, epsilon);
        Assertions.assertEquals(2.5 / Math.sqrt(200), target.acceleration, epsilon);
        Assertions.assertEquals(1, target.segment);

        target = path.findTarget(new Point(5.0, 27.0), Math.sqrt(18), path.new Target(new Point(0.0, 10.0), 2.0, 0.0, 1));

        Assertions.assertEquals(2.0, target.position.getX(), epsilon);
        Assertions.assertEquals(30.0, target.position.getY(), epsilon);
        Assertions.assertEquals(0.4, target.velocity, epsilon);
        Assertions.assertEquals(-0.2, target.acceleration, epsilon);
        Assertions.assertEquals(3, target.segment);
    }

    @Test
    public void noTargets() {
        Target target = path.findTarget(new Point(10.0, 10.0), 6.0, path.new Target(new Point(0.0, 10.0), 2.0, 0.0, 1));

        Assertions.assertNull(target);

        target = path.findTarget(new Point(5.0, 25.0), 4.0, path.new Target(new Point(0.0, 10.0), 2.0, 0.0, 1));

        Assertions.assertNull(target);
    }

    @Test
    public void nearEnd() {
        List<Point> waypoints = List.of(Point.origin(), new Point(0.1, 0.0), new Point(0.2, 0.0),
                new Point(0.3, 0.0), new Point(0.4, 0.0), new Point(0.5, 0.0));

        List<Double> velocities = List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        Path path = new Path(waypoints, velocities);

        Target target = path.findTarget(new Point(0.05, 0.0), 1.0,
                path.new Target(new Point(0.05, 0.0), 1.0, 0.0, 0));

        Assertions.assertEquals(1.05, target.position.getX(), epsilon);
        Assertions.assertEquals(0.0, target.position.getY(), epsilon);
        Assertions.assertEquals(1.0, target.velocity, epsilon);
        Assertions.assertEquals(0.0, target.acceleration, epsilon);
        Assertions.assertEquals(4, target.segment);
    }

    private static Path createTestPath() {
        List<Point> waypoints = List.of(Point.origin(), new Point(0.0, 10.0), new Point(10.0, 20.0),
                new Point(10.0, 30.0), new Point(0.0, 30.0));

        List<Double> velocities = List.of(0.0, 2.0, 3.0, 2.0, 0.0);

        return new Path(waypoints, velocities);
    }
}
