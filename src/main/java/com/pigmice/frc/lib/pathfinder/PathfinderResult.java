package com.pigmice.frc.lib.pathfinder;

import java.util.ArrayList;
import java.util.List;

import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPoint;
import com.pathplanner.lib.path.RotationTarget;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class PathfinderResult {
    private final boolean _pathFound;
    private final List<Translation2d> _simplifiedPath;

    /** A result returned by Pathfinder.FindPath() */
    public PathfinderResult(boolean pathFound, List<Translation2d> simplifiedPath) {
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

        List<PathPoint> points = new ArrayList<PathPoint>();
        for (int i = 0; i < _simplifiedPath.size() - 1; i++) {
            Translation2d current = _simplifiedPath.get(i);
            Translation2d next = _simplifiedPath.get(i + 1);
            double rotation = Math.atan2(
                    next.getY() - current.getY(),
                    next.getX() - current.getX());

            if (i != 0) {
                rotation += points.get(i - 1).rotationTarget.getTarget().getRadians();
                rotation /= 2;
            }
            Rotation2d angleToNext = new Rotation2d(rotation);

            points.add(new PathPoint(current, new RotationTarget(0.0, angleToNext), pathConstraints));
        }

        points.add(new PathPoint(
                _simplifiedPath.get(_simplifiedPath.size() - 1),
                points.get(points.size() - 1).rotationTarget));

        return PathPlannerPath
                .fromPathPoints(points, pathConstraints,
                        new GoalEndState(0.0, points.get(points.size() - 1).rotationTarget.getTarget(), false))
                .getTrajectory(new ChassisSpeeds(), points.get(0).rotationTarget.getTarget());
    }

    public List<Translation2d> getPositionList() {
        return _simplifiedPath;
    }
}
