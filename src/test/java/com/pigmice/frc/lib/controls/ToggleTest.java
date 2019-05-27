package com.pigmice.frc.lib.controls;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.GenericHID;

public class ToggleTest {
    @Test
    public void getTest() {
        GenericHID joystick = mock(GenericHID.class);
        final int button = 5;
        when(joystick.getRawButton(button)).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false)
                .thenReturn(false).thenReturn(true);

        Toggle toggle = new Toggle(joystick, button);
        toggle.update();
        Assertions.assertFalse(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertFalse(toggle.get());
    }

    @Test
    public void fromDebouncerGetTest() {
        ButtonDebouncer debouncer = mock(ButtonDebouncer.class);
        when(debouncer.get()).thenReturn(false).thenReturn(true).thenReturn(false).thenReturn(false).thenReturn(true)
                .thenReturn(true);

        Toggle toggle = new Toggle(debouncer);
        toggle.update();
        Assertions.assertFalse(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertFalse(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
    }

    @Test
    public void setTest() {
        GenericHID joystick = mock(GenericHID.class);
        final int button = 5;
        when(joystick.getRawButton(button)).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false)
                .thenReturn(false).thenReturn(true);

        Toggle toggle = new Toggle(joystick, button, true);
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertFalse(toggle.get());
        toggle.update();
        toggle.set(true);
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertFalse(toggle.get());
    }

    @Test
    public void fromDebouncerSetTest() {
        ButtonDebouncer debouncer = mock(ButtonDebouncer.class);
        when(debouncer.get()).thenReturn(false).thenReturn(true).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(true);

        Toggle toggle = new Toggle(debouncer, true);
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertFalse(toggle.get());
        toggle.set(true);
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
        toggle.update();
        Assertions.assertFalse(toggle.get());
        toggle.update();
        Assertions.assertTrue(toggle.get());
    }
}
