package com.pigmice.frc.lib.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RingTest {
    private static final double epsilon = 1e-6;

    @Test
    public void noLoop() {
        Ring ring = new Ring(5);

        ring.put(0.0);
        ring.put(1.0);
        ring.put(2.0);
        ring.put(3.0);
        ring.put(4.0);

        Assertions.assertEquals(0.0, ring.pop(), epsilon);
        Assertions.assertEquals(1.0, ring.pop(), epsilon);
        Assertions.assertEquals(2.0, ring.pop(), epsilon);
        Assertions.assertEquals(3.0, ring.pop(), epsilon);
        Assertions.assertEquals(4.0, ring.pop(), epsilon);
    }

    @Test
    public void loop() {
        Ring ring = new Ring(4);

        ring.put(0.0);
        ring.put(1.0);
        ring.put(2.0);
        ring.put(3.0);
        ring.put(4.0);

        Assertions.assertEquals(1.0, ring.pop(), epsilon);
        Assertions.assertEquals(2.0, ring.pop(), epsilon);
        Assertions.assertEquals(3.0, ring.pop(), epsilon);
        Assertions.assertEquals(4.0, ring.pop(), epsilon);
        Assertions.assertEquals(0.0, ring.pop(), epsilon);
        Assertions.assertEquals(0.0, ring.pop(), epsilon);

        ring.put(5.0);
        ring.put(6.0);

        Assertions.assertEquals(5.0, ring.pop(), epsilon);
        Assertions.assertEquals(6.0, ring.pop(), epsilon);
        Assertions.assertEquals(0.0, ring.pop(), epsilon);

        ring.put(7.0);
        ring.put(8.0);
        ring.put(9.0);
        ring.put(10.0);
        ring.put(11.0);

        Assertions.assertEquals(8.0, ring.pop(), epsilon);
        Assertions.assertEquals(9.0, ring.pop(), epsilon);
        Assertions.assertEquals(10.0, ring.pop(), epsilon);
        Assertions.assertEquals(11.0, ring.pop(), epsilon);
        Assertions.assertEquals(0.0, ring.pop(), epsilon);
        Assertions.assertEquals(0.0, ring.pop(), epsilon);
    }

    @Test()
    public void invalidSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            Ring ring = new Ring(0);
        });
    }

    @Test
    public void average() {
        Ring ring = new Ring(3);

        Assertions.assertEquals(0.0, ring.average(), epsilon);

        ring.put(2.0);
        Assertions.assertEquals(2.0 / 3.0, ring.average(), epsilon);

        ring.put(1.0);
        Assertions.assertEquals(3.0 / 3.0, ring.average(), epsilon);

        ring.put(2.0);
        Assertions.assertEquals(5.0 / 3.0, ring.average(), epsilon);

        ring.put(1.0);
        Assertions.assertEquals(4.0 / 3.0, ring.average(), epsilon);

        ring.pop();
        Assertions.assertEquals(3.0 / 3.0, ring.average(), epsilon);
    }

    @Test
    public void isEmpty() {
        Ring ring = new Ring(2);

        Assertions.assertTrue(ring.isEmpty());

        ring.pop();

        Assertions.assertTrue(ring.isEmpty());

        ring.put(0.0);

        Assertions.assertFalse(ring.isEmpty());

        ring.pop();

        Assertions.assertTrue(ring.isEmpty());

        ring.put(1.0);
        ring.put(2.0);

        Assertions.assertFalse(ring.isEmpty());
    }

    @Test
    public void initialPop() {
        Ring ring = new Ring(2);

        Assertions.assertEquals(0.0, ring.pop());
        ring.put(1.5);
        Assertions.assertEquals(1.5, ring.pop());
    }
}
