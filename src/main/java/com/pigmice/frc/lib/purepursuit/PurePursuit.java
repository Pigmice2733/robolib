package com.pigmice.frc.lib.purepursuit;

import com.pigmice.frc.lib.purepursuit.Path.Target;
import com.pigmice.frc.lib.utils.Odometry.Pose;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.lib.utils.Vector;

public class PurePursuit {
    public static class Output {
        final double velocity;
        final double curvature;

        public Output(double velocity, double curvature) {
            this.velocity = velocity;
            this.curvature = curvature;
        }
    }

    private Path path;

    public PurePursuit(Path path) {
        this.path = path;
    }

    public Output process(Pose robotPose, double lookAhead) {
        Point robotPosition = new Point(robotPose);
        Target closest = path.closestPoint(robotPosition);
        Target target = path.findTarget(robotPosition, lookAhead, closest);

        if (target == null) {
            target = closest;
        }

        Point relativeTargetPosition = target.position.relativeTo(robotPose);
        Vector targetDelta = relativeTargetPosition.subtract(Point.origin());
        double curvature = (2 * relativeTargetPosition.getX()) / (targetDelta.dot(targetDelta));

        return new Output(closest.velocity, curvature);
    }
}
