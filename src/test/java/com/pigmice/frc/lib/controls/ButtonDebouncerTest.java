package com.pigmice.frc.lib.controls;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.GenericHID;

public class ButtonDebouncerTest {
    @Test
    public void getTest() {
        GenericHID joystick = mock(GenericHID.class);
        final int button = 5;
        when(joystick.getRawButton(button)).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(false).thenReturn(true);

        ButtonDebouncer debouncer = new ButtonDebouncer(joystick, button);
        Assertions.assertFalse(debouncer.get());
        Assertions.assertTrue(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertFalse(debouncer.get());
        Assertions.assertTrue(debouncer.get());
    }
}
