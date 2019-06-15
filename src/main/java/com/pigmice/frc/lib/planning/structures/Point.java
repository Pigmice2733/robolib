package com.pigmice.frc.lib.planning.structures;

import com.pigmice.frc.lib.utils.Utils;

public class Point {
    private double[] data;
    private int dimensions;

    public Point(int dimensions) {
        this.dimensions = dimensions;
        data = new double[dimensions];
    }

    public double get(int i) {
        if(i >= 0 && i < dimensions) {
            return data[i];
        } else {
            String msg = String.format("Dimension index %d isn't a valid dimension", i);
            throw new IllegalArgumentException(msg);
        }
    }

    public void set(int i, double val) {
        if(i >= 0 && i < dimensions) {
            data[i] = val;
        } else {
            String msg = String.format("Dimension index %d isn't a valid dimension", i);
            throw new IllegalArgumentException(msg);
        }
    }

    public Point lerp(Point other, double lerp) {
        if(other.dimensions != this.dimensions) {
            throw new IllegalArgumentException("Dimensions don't match");
        }

        Point lerped = new Point(dimensions);

        for (int i = 0; i < dimensions; i++) {
            lerped.set(i, Utils.lerp(lerp, 0, 1, data[i], other.data[i]));
        }

        return lerped;
    }

    public Double distance(Point other) {
        if(other.dimensions != this.dimensions) {
            throw new IllegalArgumentException("Dimensions don't match");
        }

        double sum = 0.0;

        for (int i = 0; i < dimensions; i++) {
            sum += Math.pow(this.data[i] - other.data[i], 2);
        }

        return Math.sqrt(sum);
    }
}
