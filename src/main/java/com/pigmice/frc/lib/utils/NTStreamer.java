package com.pigmice.frc.lib.utils;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * NTStreamer streams a value over NetworkTables
 *
 * @param <E>
 */
public class NTStreamer<E> {
    private NetworkTableEntry entry;

    private E value = null;

    private int sends = 0;
    private int sendRate;

    /**
     * Constructs a NTStreamer given the name of the table to stream in, and the
     * specific key to stream to. NetworkTables is only updated when the streamed
     * value changes.
     *
     * @param table The name of the table to place the streamed value in
     * @param key   The key to place the streamed value at
     */
    public NTStreamer(String table, String key) {
        this(table, key, 1);
    }

    /**
     * Constructs a NTStreamer given a <code>NetworkTableEntry</code> to stream to.
     * NetworkTables is only updated when the streamed value changes.
     *
     * @param entry The entry to stream to
     */
    public NTStreamer(NetworkTableEntry entry) {
        this(entry, 1);
    }

    /**
     * Constructs a NTStreamer given the name of the table to stream in, and the
     * specific key to stream to. NetworkTables is only updated when the streamed
     * value changes. Send rate is optionally limited to once per a
     * specified numbers of times <code>send</code> is called. If data needs to be
     * monitored that changes many times a second, this provides crude rate limiting
     * to reduce network traffic.
     *
     * @param table    The name of the table to place the streamed value in
     * @param key      The key to place the streamed value at
     * @param sendRate The number of times <code>send</code> must be called for the
     *                 value to be updated.
     */
    public NTStreamer(String table, String key, int sendRate) {
        this(NetworkTableInstance.getDefault().getTable(table).getEntry(key), sendRate);
    }

    /**
     * Constructs a NTStreamer given a <code>NetworkTableEntry</code> to stream to.
     * NetworkTables is only updated when the streamed value changes. Send rate is
     * optionally limited to once per a specified numbers of times <code>send</code>
     * is called. If data needs to be monitored that changes many times a second,
     * this provides crude rate limiting to reduce network traffic.
     * 
     * @param entry    The NT entry to stream to
     * @param sendRate The number of times <code>send</code> must be called for the
     *                 value to be updated.
     */
    public NTStreamer(NetworkTableEntry entry, int sendRate) {
        if (sendRate <= 0) {
            throw new IllegalArgumentException("sendRate must be positive");
        }

        this.sendRate = sendRate;
        this.entry = entry;
    }

    /**
     * Send a new value into the network
     *
     * @param value The value to stream
     */
    public void send(E value) {
        if (++sends % sendRate == 0) {
            if (value instanceof Boolean) {
                if (this.value == null || this.value != value) {
                    entry.setBoolean((boolean) value);
                }
            } else if (value instanceof Double) {
                if (this.value == null || Math.abs((double) this.value - (double) value) > 1e-6) {
                    entry.setDouble(Math.round((double) value * 1000.0) / 1000.0);
                }
            } else if (value instanceof String) {
                if (this.value == null || this.value != value) {
                    entry.setString(String.valueOf(value));
                }
            } else {
                throw new IllegalArgumentException("NTStream only supports Double, Boolean, and String");
            }
            this.value = value;
        }

        sends %= sendRate;
    }
}
