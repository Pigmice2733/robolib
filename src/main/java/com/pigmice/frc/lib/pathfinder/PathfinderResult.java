package com.pigmice.frc.lib.pathfinder;

import java.util.ArrayList;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPoint;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class PathfinderResult {
    private final boolean _pathFound;
    private final ArrayList<Translation2d> _simplifiedPath;

    /** A result returned by Pathfinder.FindPath() */
    public PathfinderResult(boolean pathFound, ArrayList<Translation2d> simplifiedPath) {
        this._pathFound = pathFound;
        this._simplifiedPath = simplifiedPath;
    }

    /** @return if a valid path was found */
    public boolean pathFound() {
        return _pathFound;
    }

    /**
     * Generate a PathPlannerTrajectory from this pathfiner result. null if there is
     * no valid path
     */
    public PathPlannerTrajectory getAsTrajectory(PathConstraints pathConstraints) {
        if (!_pathFound || _simplifiedPath.size() < 2)
            return null;

        ArrayList<PathPoint> points = new ArrayList<PathPoint>();
        for (int i = 0; i < _simplifiedPath.size() - 1; i++) {
            Translation2d current = _simplifiedPath.get(i);
            Translation2d next = _simplifiedPath.get(i + 1);
            Rotation2d angleToNext = new Rotation2d(Math.atan2(
                    next.getY() - current.getY(),
                    next.getX() - current.getX()));

            points.add(new PathPoint(current, angleToNext));
        }

        points.add(new PathPoint(
                _simplifiedPath.get(_simplifiedPath.size() - 1),
                points.get(points.size() - 1).heading));

        return PathPlanner.generatePath(pathConstraints, points);
    }

    public ArrayList<Translation2d> getPositionList() {
        return _simplifiedPath;
    }
}
