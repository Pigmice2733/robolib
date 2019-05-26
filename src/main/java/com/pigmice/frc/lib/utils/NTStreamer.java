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

    private E value;

    private int last = 0;

    /**
     * Constructs a NTStreamer given the name of the table to stream in, and the
     * specific key to stream to.
     *
     * @param table The name of the table to place the streamed value in
     * @param key   The key to place the streamed value at
     */
    public NTStreamer(String table, String key) {
        NetworkTableInstance root = NetworkTableInstance.getDefault();
        entry = root.getTable(table).getEntry(key);
    }

    /**
     * Send a new value into the network, value only updates every three times it is
     * sent as a crude form of rate limiting.
     *
     * @param value The value to stream
     */
    public void send(E value) {
        if ((last++) == 0) {
            if (value instanceof Boolean) {
                if (this.value == null || this.value != value) {
                    entry.setBoolean((boolean) value);
                }
            } else if (value instanceof Double) {
                if (this.value == null || Math.abs((double) this.value - (double) value) < 1e-6) {
                    entry.setDouble(Math.round((double) value * 1000.0) / 1000.0);
                }
            } else if (value instanceof String) {
                if (this.value == null || this.value != value) {
                    entry.setString(String.valueOf(value));
                }
            }
        } else {
            last = (++last) % 3;
        }
    }
}
