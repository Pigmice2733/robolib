package com.pigmice.frc.lib.purepursuit;

import java.util.List;

import com.pigmice.frc.lib.purepursuit.PurePursuit.Output;
import com.pigmice.frc.lib.utils.Odometry.Pose;
import com.pigmice.frc.lib.utils.Point;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PurePursuitTest {
    private static double epsilon = 1e-2;

    @Test
    public void straightPathTest() {
        Path path = new Path(List.of(Point.origin(), new Point(0.0, 2.0), new Point(0.0, 8.0), new Point(0.0, 10.0)),
                List.of(2.0, 3.0, 2.0, 0.0));

        PurePursuit controller = new PurePursuit(path, 1e-4);

        Output output = controller.process(new Pose(0.0, 0.0, 0.5 * Math.PI), 1.0);
        Assertions.assertEquals(0.0, output.curvature, epsilon);
        Assertions.assertEquals(2.0, output.velocity, epsilon);
        Assertions.assertEquals(1.25, output.acceleration, epsilon);
        Assertions.assertEquals(0, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(0.0, -5.0, 0.5 * Math.PI), 6.0);
        Assertions.assertEquals(0.0, output.curvature, epsilon);
        Assertions.assertEquals(2.0, output.velocity, epsilon);
        Assertions.assertEquals(1.25, output.acceleration, epsilon);
        Assertions.assertEquals(0, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(0.0, 0.0, 0.5 * Math.PI), 7.0);
        Assertions.assertEquals(0.0, output.curvature, epsilon);
        Assertions.assertEquals(2.0, output.velocity, epsilon);
        Assertions.assertEquals(1.25, output.acceleration, epsilon);
        Assertions.assertEquals(0, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(1.0, 0.0, 0.5 * Math.PI), Math.sqrt(2.0));
        Assertions.assertEquals(-1.0, output.curvature, epsilon);
        Assertions.assertEquals(2.0, output.velocity, epsilon);
        Assertions.assertEquals(1.25, output.acceleration, epsilon);
        Assertions.assertEquals(0, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(2.0, 5.0, 0.5 * Math.PI), Math.sqrt(8.0));
        Assertions.assertEquals(-0.5, output.curvature, epsilon);
        Assertions.assertEquals(2.5, output.velocity, epsilon);
        Assertions.assertEquals(-2.5 / 6.0, output.acceleration, epsilon);
        Assertions.assertEquals(1, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(0.0, 10.5, 0.5 * Math.PI), 2.0);
        Assertions.assertEquals(0.0, output.curvature, epsilon);
        Assertions.assertEquals(0.0, output.velocity, epsilon);
        Assertions.assertEquals(-1.0, output.acceleration, epsilon);
        Assertions.assertEquals(2, output.pathSegment);
        Assertions.assertTrue(output.atEnd);
    }

    @Test
    public void halfCirclePathTest() {
        Path path = new Path(
                List.of(new Point(2.0, 0.0), new Point(1.0, Math.sqrt(3.0)), new Point(0.0, 2.0),
                        new Point(-1.0, Math.sqrt(3.0)), new Point(-2.0, 0.0), new Point(-2.0, -0.1)),
                List.of(0.0, 2.0, 5.0, 2.0, 0.0, 0.0));

        PurePursuit controller = new PurePursuit(path, 1e-4);

        Output output = controller.process(new Pose(1.0, 0.0, 0.0), 1.0);
        Assertions.assertEquals(-1.73207621135, output.curvature, epsilon);
        Assertions.assertEquals(0.5, output.velocity, epsilon);
        Assertions.assertEquals(2 / Math.sqrt(4.0), output.acceleration, epsilon);
        Assertions.assertEquals(0, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(-0.1, 2.0, Math.PI), 0.5);
        Assertions.assertEquals(-1.23373324, output.curvature, epsilon);
        Assertions.assertEquals(4.72009619, output.velocity, epsilon);
        Assertions.assertEquals(-10.5 / Math.sqrt(8 - 4 * Math.sqrt(3)), output.acceleration, epsilon);
        Assertions.assertEquals(2, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(-2.0, -0.5, 0.5 * Math.PI), 2.0);
        Assertions.assertEquals(0.0, output.curvature, epsilon);
        Assertions.assertEquals(0.0, output.velocity, epsilon);
        Assertions.assertEquals(0.0, output.acceleration, epsilon);
        Assertions.assertEquals(4, output.pathSegment);
        Assertions.assertTrue(output.atEnd);
    }

    @Test
    public void fullyOffPathTest() {
        Path path = new Path(List.of(new Point(-5.0, 10.0), new Point(5.0, 10.0), new Point(6.0, 10.0)),
                List.of(5.0, 1.0, 0.0));

        PurePursuit controller = new PurePursuit(path, 1e-4);

        Output output = controller.process(new Pose(0.0, 0.0, 0.5 * Math.PI), 9.0);
        Assertions.assertEquals(3.0, output.velocity, epsilon);
        Assertions.assertEquals(0.0, output.curvature, epsilon);
        Assertions.assertEquals(-1.2, output.acceleration, epsilon);
        Assertions.assertEquals(0, output.pathSegment);
        Assertions.assertFalse(output.atEnd);

        output = controller.process(new Pose(0.0, 0.0, 1.5 * Math.PI), 9.0);
        Assertions.assertEquals(3.0, output.velocity, epsilon);
        Assertions.assertEquals(0.0, output.curvature, epsilon);
        Assertions.assertEquals(-1.2, output.acceleration, epsilon);
        Assertions.assertEquals(0, output.pathSegment);
        Assertions.assertFalse(output.atEnd);
    }
}
