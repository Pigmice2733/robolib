package com.pigmice.frc.lib.inputs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DebouncerTest {
    @Test
    public void getTest() {
        InputMock input = new InputMock(new boolean[]{ false, false, true, true, false, false, true });

        Debouncer debouncer = new Debouncer(input);

        input.update();
        debouncer.update();
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());

        input.update();
        debouncer.update();
        Assertions.assertTrue(debouncer.get());
        Assertions.assertTrue(debouncer.get());
        Assertions.assertTrue(debouncer.get());

        input.update();
        debouncer.update();
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());

        input.update();
        debouncer.update();
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());

        input.update();
        debouncer.update();
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());

        input.update();
        debouncer.update();
        Assertions.assertTrue(debouncer.get());
        Assertions.assertTrue(debouncer.get());
        Assertions.assertTrue(debouncer.get());
    }
}
