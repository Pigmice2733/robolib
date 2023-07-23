package com.pigmice.frc.lib.pathfinder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.wpi.first.math.geometry.Translation2d;

public class NodeTest {
    private static final double epsilon = 1e-6;

    private static final Node node1 = new Node(10, 4, new Translation2d(10.2, -3.4), 1.2, 3, 0.2);
    private static final Node node2 = new Node(10, -21, new Translation2d(-10.2, 3.4), -2, 1, 1.1);

    @Test
    public void nodePositionTest() {
        Assertions.assertEquals(10, node1.gridX, epsilon);
        Assertions.assertEquals(4, node1.gridY, epsilon);
        Assertions.assertEquals(10.2, node1.fieldPos.getX(), epsilon);
        Assertions.assertEquals(-3.4, node1.fieldPos.getY(), epsilon);
    }

    @Test
    public void nodeDriveableTest() {
        Assertions.assertEquals(true, node1.driveable);
        Assertions.assertEquals(false, node2.driveable);
    }

    @Test
    public void nodeDistanceWeightTest() {
        Assertions.assertEquals(1 - 1.1 / 3d, node1.distanceWeight, epsilon);
        Assertions.assertEquals(0, node2.distanceWeight, epsilon);
    }
}
