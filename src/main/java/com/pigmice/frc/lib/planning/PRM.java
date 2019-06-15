package com.pigmice.frc.lib.planning;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.pigmice.frc.lib.planning.structures.Graph;
import com.pigmice.frc.lib.planning.structures.Point;
import com.pigmice.frc.lib.utils.Range;

public class PRM {
    public interface CSpace {
        public boolean isFree(Point p);
        public int dimensions();
        public Range getBounds(int dimension);
    }

    private CSpace cSpace;
    private Graph<Point> graph;
    private int dimensions;

    private Range[] mapBounds;

    private Random random;

    public PRM(CSpace cSpace) {
        this.cSpace = cSpace;
        this.dimensions = cSpace.dimensions();
        this.random = new Random();
        this.graph = new Graph<>();

        mapBounds = new Range[dimensions];

        for (int i = 0; i < dimensions; i++) {
            mapBounds[i] = cSpace.getBounds(i);
        }
    }

    public void addPoint(Point p) {
        if(cSpace.isFree(p)) {
            graph.addVertex(p);
        }
    }

    public void constructRoadmap(int iterations, int kNeighbors) {
        for (int i = 0; i < iterations; i++) {
            Point rand = sampleFree();
            graph.addVertex(rand);
        }

        List<Point> vertices = graph.getVertices();

        for (Point p : vertices) {
            List<Point> neighbors = kNearestNeighbors(p, graph, kNeighbors);
            for (Point n : neighbors) {
                if(free(p, n)) {
                    graph.addEdge(p, n, p.distance(n));
                }
            }
        }
    }

    public Graph<Point> getRoadmap() {
        return graph;
    }

    public List<Point> solvePath(Point start, Point target) {
        return graph.dijkstra(start, target, (a, b) -> a.distance(b));
    }

    private Point sampleFree() {
       boolean free = false;
       Point rand = new Point(dimensions);

       while(!free) {
           sample(rand);
           free = cSpace.isFree(rand);
       }

       return rand;
    }

    private void sample(Point p) {
        for (int i = 0; i < dimensions; i++) {
            double randomValue = mapBounds[i].min() + mapBounds[i].size() * random.nextDouble();
            p.set(i, randomValue);
        }
    }

    private boolean free(Point a, Point b) {
        double lerp = 0.0;
        while (lerp < 1.0) {
            if(!cSpace.isFree(a.lerp(b, lerp))) {
                return false;
            }
            lerp += 0.01;
        }

        return true;
    }

    private List<Point> kNearestNeighbors(Point p, Graph<Point> graph, int k) {
        List<Point> vertices = graph.getVertices();
        Collections.sort(vertices, (a, b) -> a.distance(p).compareTo(b.distance(p)));
        return vertices.subList(0, k);
    }
}
