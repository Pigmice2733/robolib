package com.pigmice.frc.lib.purepursuit;

import java.util.List;

import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.lib.utils.Utils;
public class Path {
    public class Target {
        public final int segment;
        public final double velocity;
        public final Point position;

        public Target(Point position, double velocity, int segment) {
            this.position = position;
            this.velocity = velocity;
            this.segment = segment;
        }
    }

    private List<Point> positions;
    private List<Double> velocities;

    public Path(List<Point> positions, List<Double> velocities) {
        this.positions = positions;
        this.velocities = velocities;
    }

    public Target closestPoint(Point position) {
        int closestSegment = 0;
        Point closestPoint = null;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < positions.size() - 1; i++) {
            Point projected = position.project(positions.get(i), positions.get(i + 1));
            double distance = position.subtract(projected).getMagnitude();

            if (distance < closestDistance) {
                closestDistance = distance;
                closestPoint = projected;
                closestSegment = i;
            }
        }

        return new Target(closestPoint, velocities.get(closestSegment), closestSegment);
    }

    public Target findTarget(Point position, double lookAhead, int startSegment) {
        for (int i = startSegment; i < positions.size() - 1; i++) {
            List<Point> intersections =  Utils.circleLineIntersections(positions.get(i), positions.get(i + 1), position, lookAhead);
            if(intersections.size() == 0) {
                return null;
            }

            Point targetPosition = intersections.get(1);
            double segmentCompletion = (targetPosition.subtract(positions.get(i)).getMagnitude()) / (positions.get(i + 1).subtract(positions.get(i)).getMagnitude());
            double velocity = Utils.lerp(segmentCompletion, 0.0, 1.0, velocities.get(i), velocities.get(i + 1));
            return new Target(targetPosition, velocity, i);
        }

        return null;
    }
}
