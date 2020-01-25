package com.pigmice.frc.lib.purepursuit;

import java.util.List;

import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.lib.utils.Utils;
import com.pigmice.frc.lib.utils.Vector;
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

        double segmentCompletion = (closestPoint.subtract(positions.get(closestSegment)).getMagnitude())
                / (positions.get(closestSegment + 1).subtract(positions.get(closestSegment)).getMagnitude());
        double velocity = Utils.lerp(segmentCompletion, 0.0, 1.0, velocities.get(closestSegment), velocities.get(closestSegment + 1));
        return new Target(closestPoint, velocity, closestSegment);
    }

    public Target findTarget(Point position, double lookAhead, Target searchStart) {
        for (int i = searchStart.segment; i < positions.size() - 1; i++) {
            Point start = (i == searchStart.segment) ? searchStart.position : positions.get(i);
            double startVelocity = (i == searchStart.segment) ? searchStart.velocity : velocities.get(i);
            Point end = positions.get(i + 1);
            double endVelocity = velocities.get(i + 1);

            Vector delta = end.subtract(start);
            if(delta.getMagnitude() < 1e-6) {
                continue;
            }

            List<Double> intersections = Utils.circleLineIntersections(start, delta, position, lookAhead);

            intersections.removeIf((Double x) -> (x < 0.0 || x > 1.0));

            if (intersections.size() == 0) {
                continue;
            }

            double targetIntersection = intersections.get(intersections.size() - 1);
            Point targetPosition = start.translate(delta.scale(targetIntersection));
            double velocity = Utils.lerp(targetIntersection, 0.0, 1.0, startVelocity, endVelocity);
            return new Target(targetPosition, velocity, i);
        }

        return null;
    }
}
