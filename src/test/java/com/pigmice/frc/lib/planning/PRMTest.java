package com.pigmice.frc.lib.planning;

import java.util.HashMap;
import java.util.List;

import com.pigmice.frc.lib.planning.structures.Graph;
import com.pigmice.frc.lib.planning.structures.Point;
import com.pigmice.frc.lib.plots.PRMPlot;
import com.pigmice.frc.lib.utils.Range;

import org.junit.jupiter.api.Test;

public class PRMTest {
    private class CSpace implements PRM.CSpace {
        public boolean isFree(Point p) {
            if (p.get(1) > 10.0) {
                return p.get(0) > 2 * (15 - p.get(1));
            } else {
                return p.get(0) > 2 * (p.get(1) - 5);
            }
        }

        public int dimensions() {
            return 2;
        }

        public Range getBounds(int dimension) {
            switch (dimension) {
            case 0:
                return new Range(0.0, 20.0);
            default:
                return new Range(0.0, 20.0);
            }
        }
    }

    @Test
    public void graphical() {
        PRM prm = new PRM(new CSpace());

        Point start = new Point(2);
        start.set(0, 0.0);
        start.set(1, 0.0);

        Point end = new Point(2);
        end.set(0, 0.0);
        end.set(1, 20.0);

        prm.addPoint(start);
        prm.addPoint(end);

        prm.constructRoadmap(75, 12);

        Graph<Point> roadmap = prm.getRoadmap();

        if (PRMPlot.shouldGraph("prm")) {
            PRMPlot prmPlot = new PRMPlot("PRM");

            HashMap<Graph<Point>.Key, Double> edges = roadmap.getEdges();

            List<Point> path = prm.solvePath(start, end);
            prmPlot.addSolution(path);

            for (Graph<Point>.Key key : edges.keySet()) {
                prmPlot.addEdge(key.getFirst().get(0), key.getFirst().get(1), key.getSecond().get(0), key.getSecond().get(1));
            }

            prmPlot.save("prm");
        }
    }
}
