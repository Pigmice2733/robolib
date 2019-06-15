package com.pigmice.frc.lib.planning.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph<T> {
    public class Key {
        private T first, second;

        public Key(T first, T second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if ((o == null) ||!(o instanceof Graph<?>.Key)) {
                return false;
            }

            Graph<?>.Key other = (Graph<?>.Key) o;
            return first == other.first && second == other.second;
        }

        @Override
        public int hashCode() {
            return (int) (7.0 - 12.0 * first.hashCode() + 227.0 * second.hashCode());
        }
    }

    private List<T> vertices;
    private HashMap<Key, Double> edges;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new HashMap<>();
    }

    public void addVertex(T vertex) {
        vertices.add(vertex);
    }

    public void addEdge(T first, T second, Double weight) {
        edges.put(new Key(first, second), weight);
        edges.put(new Key(second, first), weight);
    }

    public List<T> getVertices() {
        return new ArrayList<>(vertices);
    }

    public HashMap<Key, Double> getEdges() {
        return new HashMap<>(edges);
    }

    public interface DistanceFunction<U> {
        public double get(U a, U b);
    }

    public List<T> dijkstra(T start, T target, DistanceFunction<T> distFunc) {
        Set<T> unseen = new HashSet<>(vertices);

        HashMap<T, Double> distances = new HashMap<>();
        HashMap<T, T> previous = new HashMap<>();

        distances.put(start, 0.0);

        while(!unseen.isEmpty()) {
            Double minDist = Double.POSITIVE_INFINITY;
            T minVertex = null;
            for (Map.Entry<T, Double> e : distances.entrySet()) {
                if(unseen.contains(e.getKey()) && e.getValue() < minDist) {
                    minDist = e.getValue();
                    minVertex = e.getKey();
                }
            }

            if(minVertex == target) {
                break;
            }

            unseen.remove(minVertex);

            for (Map.Entry<Key, Double> e : edges.entrySet()) {
                T v;
                if(e.getKey().first == minVertex) {
                   v = e.getKey().second;
                } else if(e.getKey().second == minVertex) {
                    v = e.getKey().first;
                } else {
                    continue;
                }
                if(!unseen.contains(v)) {
                    continue;
                }
                Double alt = distances.get(minVertex) + distFunc.get(minVertex, v);
                if(alt < distances.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    distances.put(v, alt);
                    previous.put(v, minVertex);
                }
            }
        }

        List<T> path = new ArrayList<>();
        T p = target;
        while(previous.containsKey(p)) {
            path.add(0, p);
            p = previous.get(p);
        }
        path.add(0, p);

        return path;
    }
}
