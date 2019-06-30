package com.pigmice.frc.lib.controls;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ButtonDebouncerTest {
    private static final int button = 5;

    @Test
    public void getTest() {
        JoyMock joystick = new JoyMock(button);

        ButtonDebouncer debouncer = new ButtonDebouncer(joystick, button);
        Assertions.assertFalse(debouncer.get());
        Assertions.assertTrue(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertTrue(debouncer.get());
    }
}
