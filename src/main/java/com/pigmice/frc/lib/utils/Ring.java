package com.pigmice.frc.lib.utils;

/**
 * A Ring is a circular FIFO structure with a fixed size. Writing to the Ring
 * will overwrite the oldest data when the Ring is full. Reading from a Ring
 * gives the oldest data point that hasn't been overwritten, and replaces it
 * with 0.0.
 */
public class Ring {
    private final double[] storage;

    private int writePos = 0;
    private int readPos = 0;

    private final int size;

    private double total = 0.0;
    private double average = 0.0;

    private double readValue;

    private boolean empty = true;

    /**
     *
     * @param size the maximum number of elements the Ring can store (must be
     *             greater than zero)
     * @throws IllegalArgumentException if Ring size is not greater than zero
     */
    public Ring(int size) throws IllegalArgumentException {
        if (size < 1) {
            throw new IllegalArgumentException("Size of ring must be positive");
        }

        empty = true;
        this.size = size;
        storage = new double[size];
    }

    /**
     * Adds an element to the next empty space in this Ring, or overwrites the
     * oldest data if this Ring is full.
     *
     * @param value the number to add to this Ring
     */
    public void put(double value) {
        if (empty) {
            empty = false;
        } else if (writePos == readPos) {
            readPos = (readPos + 1) % size;
        }

        total = total - storage[writePos] + value;
        average = total / size;

        storage[writePos] = value;

        writePos = (writePos + 1) % size;
    }

    /**
     * Returns the oldest data point from this Ring and replaces it with 0.0. If
     * this Ring is empty, returns 0.0.
     *
     * @return the oldest data point from this Ring
     */
    public double pop() {
        if (empty) {
            return 0.0;
        }

        readValue = storage[readPos];
        storage[readPos] = 0.0;
        readPos = (readPos + 1) % size;

        total -= readValue;
        average = total / size;

        if (readPos == writePos) {
            empty = true;
        }

        return readValue;
    }

    /**
     * Returns the current average of all the elements in this Ring.
     *
     * @return the current average
     */
    public double average() {
        return average;
    }

    /**
     * Returns the current sum of all the elements in this Ring.
     * 
     * @return the current total
     */
    public double total() {
        return total;
    }

    /**
     * Gets whether this Ring has no elements inside.
     *
     * @return <code>true</code> if this Ring has no elements, <code>false</code>
     *         otherwise
     */
    public boolean isEmpty() {
        return empty;
    }
}
