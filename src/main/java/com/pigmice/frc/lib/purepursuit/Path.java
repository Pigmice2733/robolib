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

    private final List<Point> positions;
    private final List<Double> velocities;

    public Path(List<Point> positions, List<Double> velocities) {
        this.positions = positions;
        this.velocities = velocities;
    }

    public Point end() {
        return positions.get(positions.size() - 1);
    }

    public Target closestPoint(Point position) {
        int closestSegment = 0;
        Point closestPoint = null;
        double closestPointCompletionFactor = 0.0;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < positions.size() - 1; i++) {
            final Vector delta = positions.get(i + 1).subtract(positions.get(i));
            final double projection = Utils.project(position, positions.get(i), delta);
            final Point point = positions.get(i).translate(delta.scale(projection));
            final double distance = position.subtract(point).magnitude();

            if (distance < closestDistance) {
                closestDistance = distance;
                closestPoint = point;
                closestSegment = i;
                closestPointCompletionFactor = projection;
            }
        }

        final double startVelocity = velocities.get(closestSegment);
        final double endVelocity = velocities.get(closestSegment + 1);
        final double velocity = Utils.lerp(closestPointCompletionFactor, 0.0, 1.0, startVelocity, endVelocity);
        return new Target(closestPoint, velocity, closestSegment);
    }

    public Target findTarget(Point position, double lookAhead, Target searchStart) {
        for (int i = searchStart.segment; i < positions.size() - 1; i++) {
            final Point start = (i == searchStart.segment) ? searchStart.position : positions.get(i);
            final double startVelocity = (i == searchStart.segment) ? searchStart.velocity : velocities.get(i);
            final Point end = positions.get(i + 1);
            final double endVelocity = velocities.get(i + 1);

            final Vector delta = end.subtract(start);

            if (delta.magnitude() < 1e-6) {
                continue;
            }

            final List<Double> intersections = Utils.circleLineIntersections(start, delta, position, lookAhead);

            intersections.removeIf((Double x) -> (x < 0.0 || x > 1.0));

            // If the robot is on the last segment of the path, and close enough to the end
            // that the look ahead distance is beyond the end of the path, the path is "extended"
            // in the same direction as the final path segment. The target is kept at one look
            // ahead from the robot's position, just as if the robot was not near the end of the
            // path. The velocity is kept the same as the final point on the path.
            if (intersections.size() == 0 && i == positions.size() - 2 && searchStart.segment == i) {
                Vector endDirection = end.subtract(positions.get(i)).normalize();
                // Since the robot is likely not exactly on the path, the actual look ahead
                // distance is longer than the distance on the path from the robot's projected
                // position to the chosen target.
                double projectionDistance = searchStart.position.subtract(position).magnitude();
                double lookAheadOnPath = Math.sqrt(lookAhead*lookAhead - projectionDistance * projectionDistance);
                Point target = searchStart.position.translate(endDirection.scale(lookAheadOnPath));
                return new Target(target, endVelocity, i);
            }

            if (intersections.size() == 0) {
                continue;
            }

            final double targetIntersection = intersections.get(intersections.size() - 1);
            final Point targetPosition = start.translate(delta.scale(targetIntersection));
            final double velocity = Utils.lerp(targetIntersection, 0.0, 1.0, startVelocity, endVelocity);
            return new Target(targetPosition, velocity, i);
        }

        return null;
    }
}
