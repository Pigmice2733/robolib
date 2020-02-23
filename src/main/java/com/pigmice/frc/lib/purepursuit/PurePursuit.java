package com.pigmice.frc.lib.purepursuit;

import com.pigmice.frc.lib.purepursuit.Path.Target;
import com.pigmice.frc.lib.utils.Odometry.Pose;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.lib.utils.Vector;

public class PurePursuit {
    public static class Output {
        public final double velocity;
        public final double acceleration;
        public final double curvature;
        public final int pathSegment;
        public final boolean atEnd;

        public Output(double velocity, double acceleration, double curvature, int pathSegment, boolean atEnd) {
            this.velocity = velocity;
            this.acceleration = acceleration;
            this.curvature = curvature;
            this.pathSegment = pathSegment;
            this.atEnd = atEnd;
        }
    }

    private final Path path;
    private final double allowableError;

    public PurePursuit(Path path, double allowableError) {
        this.path = path;
        this.allowableError = allowableError;
    }

    public Output process(Pose robotPose, double lookAhead) {
        Point robotPosition = new Point(robotPose);
        Target closest = path.closestPoint(robotPosition);
        Target target = path.findTarget(robotPosition, lookAhead, closest);

        // Path is done once the closest point on the path is the end
        boolean done = closest.position.subtract(path.end()).magnitude() < allowableError;

        if (target == null) {
            target = closest;
        }

        Point relativeTargetPosition = target.position.relativeTo(robotPose);
        Vector targetDelta = relativeTargetPosition.subtract(Point.origin());
        double curvature = (2 * relativeTargetPosition.getX()) / (targetDelta.dot(targetDelta));

        return new Output(closest.velocity, closest.acceleration, curvature, closest.segment, done);
    }
}
