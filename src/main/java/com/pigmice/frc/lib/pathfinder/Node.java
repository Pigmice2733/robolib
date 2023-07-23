package com.pigmice.frc.lib.pathfinder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;

public class Node {
    public final int gridX;
    public final int gridY;

    public final Translation2d fieldPos;

    public final double distanceWeight;
    public final double distanceToNearestObstacle;

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
     * @param gridX                     the x position within the grid
     * @param gridY                     the y position within the grid
     * @param fieldPos                  the real life position this node represents
     * @param distanceToNearestObstacle the distance from this node to the nearest
     *                                  obstacle
     * @param distanceCutoff            when this node is within this distance of a
     *                                  wall, it will be negatively weighted in
     *                                  pathfinding
     * @param robotWidth                the robot's width including bumpers
     */
    public Node(int gridX, int gridY, Translation2d fieldPos, double distanceToNearestObstacle, double distanceCutoff,
            double robotWidth) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.fieldPos = fieldPos;

        this.distanceToNearestObstacle = distanceToNearestObstacle;

        distanceToNearestObstacle -= robotWidth / 2d; // account for width of robot
        this.driveable = (distanceToNearestObstacle) > 0;

        double cutoff = distanceCutoff;
        if (distanceToNearestObstacle < cutoff) // if the node is near a wall, scale distanceWeight from 0-1 based on
                                                // how close it is
            distanceWeight = MathUtil.interpolate(1, 0, distanceToNearestObstacle / cutoff);
        else
            distanceWeight = 0;
    }
}
