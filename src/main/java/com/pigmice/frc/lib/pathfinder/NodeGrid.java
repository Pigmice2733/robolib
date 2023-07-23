package com.pigmice.frc.lib.pathfinder;

import java.util.ArrayList;

import com.pigmice.frc.lib.pathfinder.field_loading.Field;
import com.pigmice.frc.lib.pathfinder.field_loading.SDFGenerator;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;

public class NodeGrid {
    private final Field _field;

    private final Node[][] _nodes;

    private final int _numNodesX;
    private final int _numNodesY;

    /**
     * Creates a new {@link NodeGrid}
     * 
     * @param robotWidth the width of the robot including the bumper
     * @param field      the field to generate nodes from
     */
    public NodeGrid(double robotWidth, Field field) {
        this._field = field;

        _numNodesX = (int) (field.config.fieldSizeMeters.getX() / field.config.nodeSpacingMeters);
        _numNodesY = (int) (field.config.fieldSizeMeters.getY() / field.config.nodeSpacingMeters);

        // TODO: Center nodes within the field
        // Translation2d centerOffset = new Translation2d(fieldSize.getX() %
        // nodeSpacing, fieldSize.getY() % nodeSpacing);
        // this.bottomRightPosition = bottomRightPosition.plus(centerOffset.div(2d));

        _nodes = new Node[_numNodesX][_numNodesY];

        for (int x = 0; x < _numNodesX; x++) {
            for (int y = 0; y < _numNodesY; y++) {
                Translation2d fieldPos = GridToFieldPos(x, y);
                double distanceFromEdge = SDFGenerator.getDistanceFromNearestObstacle(field, fieldPos);
                _nodes[x][y] = new Node(x, y, fieldPos, distanceFromEdge, Pathfinder.DISTANCE_CUTOFF, robotWidth);
            }
        }
    }

    /** @return the field position of the given grid position */
    private Translation2d GridToFieldPos(int gridX, int gridY) {
        return _field.config.bottomLeftPositionMeters
                .plus(new Translation2d(gridX, gridY).times(_field.config.nodeSpacingMeters));
    }

    /** @return the closest node to the given position (clamped to be in bounds) */
    public Node FindCloseNode(Translation2d position) {
        int x = (int) Math.round(
                (position.getX() - _field.config.bottomLeftPositionMeters.getX()) / _field.config.nodeSpacingMeters);
        int y = (int) Math.round(
                (position.getY() - _field.config.bottomLeftPositionMeters.getY()) / _field.config.nodeSpacingMeters);

        // Clamp position to be in bounds
        x = MathUtil.clamp(x, 0, _nodes.length - 1);
        y = MathUtil.clamp(y, 0, _nodes[0].length - 1);
        return _nodes[x][y];
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

                if (checkX >= 0 && checkX < _nodes.length && checkY >= 0 && checkY < _nodes[0].length)
                    neighbors.add(_nodes[checkX][checkY]);
            }
        }
        return neighbors;
    }

    /** @return the width of this grid */
    public int getWidth() {
        return _numNodesX;
    }

    /** @return the height of this grid */
    public int getHeight() {
        return _numNodesY;
    }

    /** @return the node at (x, y) clamped to be instide the grid */
    public Node getNodeAt(int x, int y) {
        x = MathUtil.clamp(x, 0, _numNodesX - 1);
        y = MathUtil.clamp(y, 0, _numNodesY - 1);

        return _nodes[x][y];
    }
}
