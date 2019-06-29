package com.pigmice.frc.lib.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.wpi.first.networktables.NetworkTableEntry;

@ExtendWith(MockitoExtension.class)
public class NTStreamerTest {
    @Mock
    NetworkTableEntry entry;

    @InjectMocks
    NTStreamer<Double> nts = new NTStreamer<>("/root", "name");

    @InjectMocks
    NTStreamer<Double> rateLimited = new NTStreamer<>("/root", "name", 3);

    @Test()
    public void invalidRate() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            NTStreamer<Double> d = new NTStreamer<>(entry, 0);
        });
    }

    @Test()
    public void invalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            NTStreamer<Map<Integer, Integer>> o = new NTStreamer<>(entry, 1);
            o.send(new HashMap<>());
        });
    }

    @Test
    public void sendTest() {
        nts.send(1.0);
        nts.send(50.0);
        nts.send(50.0);
        nts.send(20.0);

        verify(entry).setDouble(1.0);
        verify(entry).setDouble(50.0);
        verify(entry).setDouble(20.0);
    }

    @Test
    public void rateLimitTest() {
        rateLimited.send(1.0);
        rateLimited.send(50.0);
        rateLimited.send(50.0);
        rateLimited.send(20.0);
        rateLimited.send(30.0);
        rateLimited.send(50.0);
        rateLimited.send(20.0);
        rateLimited.send(42.0);
        rateLimited.send(0.0);

        verify(entry).setDouble(50.0);
        verify(entry).setDouble(0.0);
    }

    @Test
    public void stringTest() {
        NTStreamer<String> s = new NTStreamer<>(entry);

        s.send("Hello");
        s.send("Hello");
        s.send("Hey!!");

        verify(entry).setString("Hello");
        verify(entry).setString("Hey!!");
    }

    @Test
    public void booleanTest() {
        NTStreamer<Boolean> b = new NTStreamer<>(entry, 2);

        b.send(true);
        b.send(false);
        b.send(false);
        b.send(false);
        b.send(true);
        b.send(true);

        verify(entry).setBoolean(false);
        verify(entry).setBoolean(true);
    }
}
