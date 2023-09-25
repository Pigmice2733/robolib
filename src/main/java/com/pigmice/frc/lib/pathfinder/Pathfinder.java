package com.pigmice.frc.lib.pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.pigmice.frc.lib.pathfinder.field_loading.Field;
import com.pigmice.frc.lib.pathfinder.field_loading.FieldParser;

import edu.wpi.first.math.geometry.Translation2d;

public class Pathfinder {
    public static final double DISTANCE_CUTOFF = 1;
    public static final double BIAS_AWAY_FROM_EDGE = 1;

    public NodeGrid grid = null;
    public Field field;

    /**
     * @param robotWidthMeters the width of the robot including bumpers
     * @param distanceMapName  the name of the imported distance map to use when
     *                         pathfinding
     */
    public Pathfinder(double robotWidthMeters, String distanceMapName) {
        field = FieldParser.parseField(distanceMapName);

        grid = new NodeGrid(robotWidthMeters, field);
    }

    /**
     * @return PathfinderResult contains a list of waypoints from currentPos to
     *         goalPos avoiding obstacles
     */
    public PathfinderResult findPath(Translation2d currentPos, Translation2d goalPos) {
        Node start = grid.FindCloseNode(currentPos);
        Node end = grid.FindCloseNode(goalPos);

        if (start == end || !start.driveable || !end.driveable)
            return new PathfinderResult(false, null);

        boolean pathFound = false;
        ArrayList<Node> path = new ArrayList<Node>();
        ArrayList<Translation2d> simplifiedPath = new ArrayList<Translation2d>();

        ArrayList<Node> openSet = new ArrayList<Node>();
        HashSet<Node> closedSet = new HashSet<Node>();

        openSet.add(start);
        while (openSet.size() > 0) {
            Node currentNode = openSet.get(0);
            for (int i = 1; i < openSet.size(); i++) {
                if (openSet.get(i).fCost() < currentNode.fCost()
                        || openSet.get(i).fCost() == currentNode.fCost() && openSet.get(i).hCost < currentNode.hCost) {
                    currentNode = openSet.get(i);
                }
            }

            openSet.remove(currentNode);
            closedSet.add(currentNode);

            if (currentNode == end) {
                path = retracePath(start, end);

                simplifiedPath = simplifyPath(path);

                simplifiedPath.set(0, currentPos);
                simplifiedPath.set(simplifiedPath.size() - 1, goalPos);

                pathFound = true;
            }

            for (Node neighbor : grid.GetNeighbors(currentNode)) {
                if (!neighbor.driveable || closedSet.contains(neighbor))
                    continue;
                double newMovementCostToNeighbor = currentNode.gCost + getDistance(currentNode, neighbor);
                newMovementCostToNeighbor += neighbor.distanceWeight * BIAS_AWAY_FROM_EDGE;
                if (newMovementCostToNeighbor < neighbor.gCost || !openSet.contains(neighbor)) {
                    neighbor.gCost = newMovementCostToNeighbor;
                    neighbor.hCost = getDistance(neighbor, end);
                    neighbor.parent = currentNode;
                    if (!openSet.contains(neighbor))
                        openSet.add(neighbor);
                }
            }
        }
        return new PathfinderResult(pathFound, simplifiedPath);
    }

    /** @return the path traced back from the end node */
    static ArrayList<Node> retracePath(Node start, Node end) {
        ArrayList<Node> path = new ArrayList<Node>();
        Node currentNode = end;

        while (currentNode != start) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        path.add(start);

        Collections.reverse(path);
        return path;
    }

    /** @return a simplified version of the path that only contains turns */
    static ArrayList<Translation2d> simplifyPath(ArrayList<Node> path) {
        ArrayList<Translation2d> waypoints = new ArrayList<Translation2d>();
        Translation2d oldDirection = new Translation2d();

        waypoints.add(path.get(0).fieldPos);

        for (int i = 1; i < path.size() - 1; i++) {

            Translation2d newDirection = path.get(i).fieldPos.minus(path.get(i + 1).fieldPos);
            if (!newDirection.equals(oldDirection)) {
                waypoints.add(path.get(i).fieldPos);
                oldDirection = newDirection;
            }
        }
        waypoints.remove(path.get(1).fieldPos);
        waypoints.add(path.get(path.size() - 1).fieldPos);

        return waypoints;
    }

    /**
     * @return the smallest number of steps between nodeA and nodeB ignoring
     *         obstacles
     */
    static double getDistance(Node nodeA, Node nodeB) {
        double distX = Math.abs(nodeA.gridX - nodeB.gridX);
        double distY = Math.abs(nodeA.gridY - nodeB.gridY);

        if (distX > distY)
            return Math.sqrt(2d) * distY + (distX - distY);
        else
            return Math.sqrt(2d) * distX + (distY - distX);
    }
}