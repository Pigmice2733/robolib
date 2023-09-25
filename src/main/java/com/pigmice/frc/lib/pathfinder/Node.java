package com.pigmice.frc.lib.pathfinder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;

public class Node {
    public final double edgeToCenterDistance;
    public final double edgeToBumperDistance;

    public final int gridX;
    public final int gridY;

    public final Translation2d fieldPos;

    public final double distanceWeight;

    public final boolean driveable;

    public double gCost;
    public double hCost;

    public double fCost() {
        return gCost + hCost;
    }

    public Node parent;

    /**
     * A single node representing a real life position to use in pathfinding
     * 
     * @param gridX                the x position within the grid
     * @param gridY                the y position within the grid
     * @param fieldPos             the real life position this node represents
     * @param distanceToNearestObj the distance from this node to the nearest
     *                             obstacle
     * @param distanceCutoff       when this node is within this distance of a
     *                             wall, it will be negatively weighted in
     *                             pathfinding
     * @param robotWidth           the robot's width including bumpers
     */
    public Node(int gridX, int gridY, Translation2d fieldPos, double distanceToNearestObj, double distanceCutoff,
            double robotWidth) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.fieldPos = fieldPos;

        this.edgeToCenterDistance = distanceToNearestObj;

        this.edgeToBumperDistance = distanceToNearestObj - (robotWidth / 2d); // account for width of robot

        this.driveable = (edgeToBumperDistance) > 0;

        this.distanceWeight = (edgeToBumperDistance < distanceCutoff && driveable) // if the node is near a wall, scale
                // distanceWeight from 0-1 based on how close it is
                ? MathUtil.interpolate(1, 0, edgeToBumperDistance / distanceCutoff)
                : 0;
    }
}
