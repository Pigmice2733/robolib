package com.pigmice.frc.lib.purepursuit;

import com.pigmice.frc.lib.purepursuit.Path.Target;
import com.pigmice.frc.lib.utils.Point;

public class PurePursuit {

    private Path path;

    public PurePursuit(Path path) {
        this.path = path;
    }

    public double getCurvature(Point robotPosition, double lookAhead) {
        Target closest= path.closestPoint(robotPosition);
        Target target = path.findTarget(robotPosition, lookAhead, closest.segment);

        return 0.0;
    }
}
