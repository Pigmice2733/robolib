package com.pigmice.frc.lib.pathfinder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;

public class NodeGrid {
    private Node[][] nodes;
    private double nodeSpacing;

    private Translation2d botLeftBound;
    private Translation2d fieldSize;

    private int numNodesX;
    private int numNodesY;

    public NodeGrid(Translation2d botLeftBound, Translation2d topRightBound, double robotWidth,
            BufferedImage distanceMap, Pathfinder pathfinder) {
        numNodesX = distanceMap.getWidth();
        numNodesY = distanceMap.getHeight();

        this.botLeftBound = botLeftBound;
        this.fieldSize = topRightBound.minus(botLeftBound);
        this.nodeSpacing = fieldSize.getX() / numNodesX;

        // Center nodes within the field
        Translation2d centerOffset = new Translation2d(fieldSize.getX() % nodeSpacing, fieldSize.getY() % nodeSpacing);
        this.botLeftBound = botLeftBound.plus(centerOffset.div(2d));

        nodes = new Node[numNodesX][numNodesY];

        for (int x = 0; x < numNodesX; x++) {
            for (int y = 0; y < numNodesY; y++) {
                Translation2d worldPos = GridToWorldPos(x, y);
                double distanceFromEdge = new Color(distanceMap.getRGB(x, y)).getRed() / 255d * fieldSize.getX()
                        - robotWidth / 2;
                nodes[x][y] = new Node(x, y, worldPos, distanceFromEdge, pathfinder);
            }
        }

        GridVisualizer.visualizePathAsPNG(this, "C:/Users/lucah/Desktop/test");
    }

    /** @return the field position of the given grid position */
    private Translation2d GridToWorldPos(int gridX, int gridY) {
        return botLeftBound.plus(new Translation2d(gridX, gridY).times(nodeSpacing));
    }

    /** @return the closest node to the given position */
    public Node FindCloseNode(Translation2d pos) {
        int x = (int) Math.round((pos.getX() - botLeftBound.getX()) / nodeSpacing);
        int y = (int) Math.round((pos.getY() - botLeftBound.getY()) / nodeSpacing);

        // Clamp position to be in bounds
        x = MathUtil.clamp(x, 0, nodes.length - 1);
        y = MathUtil.clamp(y, 0, nodes[0].length - 1);
        return nodes[x][y];
    }

    /** @return a list of all the nodes within a 3x3 square of the center */
    public ArrayList<Node> GetNeighbors(Node center) {
        ArrayList<Node> neighbors = new ArrayList<Node>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;

                int checkX = center.gridX + x;
                int checkY = center.gridY + y;

                if (checkX >= 0 && checkX < nodes.length && checkY >= 0 && checkY < nodes[0].length)
                    neighbors.add(nodes[checkX][checkY]);
            }
        }
        return neighbors;
    }

    /** @return the width of this grid */
    public int getWidth() {
        return numNodesX;
    }

    /** @return the height of this grid */
    public int getHeight() {
        return numNodesY;
    }

    /** @return the node at (x, y) clamped to be instide the grid */
    public Node getNodeAt(int x, int y) {
        x = MathUtil.clamp(x, 0, numNodesX-1);
        y = MathUtil.clamp(y, 0, numNodesY-1);

        return nodes[x][y];
    }
}
