package com.pigmice.frc.lib.pathfinder;

import java.util.ArrayList;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPoint;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class PathfinderResult {
    private final boolean pathFound;
    private final ArrayList<Translation2d> simplifiedPath;
    private final ArrayList<Node> path;

    /** A result returned by Pathfinder.FindPath() */
    public PathfinderResult(boolean pathFound, ArrayList<Translation2d> simplifiedPath, ArrayList<Node> path) {
        this.pathFound = pathFound;
        this.simplifiedPath = simplifiedPath;
        this.path = path;
    }

    /** @return if a valid path was found */
    public boolean pathFound() {
        return pathFound;
    }

    /** Generate a PathPlannerTrajectory from this pathfiner result. null if there is no valid path */
    public PathPlannerTrajectory getAsTrajectory(PathConstraints pathConstraints) {
        if (!pathFound || simplifiedPath.size() < 2) return null;

        ArrayList<PathPoint> points = new ArrayList<PathPoint>();
        for (int i = 0; i < simplifiedPath.size()-1; i++) {
            Translation2d current = simplifiedPath.get(i);
            Translation2d next = simplifiedPath.get(i+1);
            Rotation2d angleToNext = Rotation2d.fromRadians(Math.atan2(
                next.getY() - current.getY(),
                next.getX() - current.getX()));
        
            points.add(new PathPoint(current, angleToNext));
        }

        points.add(new PathPoint(
            simplifiedPath.get(simplifiedPath.size()-1), 
            points.get(points.size()-1).heading));

        return PathPlanner.generatePath(pathConstraints, points);
    }
}
