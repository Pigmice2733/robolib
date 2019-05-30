package com.pigmice.frc.lib.controls;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubstateToggleTest {
    @Test
    public void toggleTest() {
        ButtonDebouncer debouncer = mock(ButtonDebouncer.class);
        when(debouncer.get()).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(true)
                .thenReturn(false);
        SubstateToggle toggle = new SubstateToggle(debouncer);

        toggle.update();
        Assertions.assertFalse(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertTrue(toggle.isToggled());
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
    }

    @Test
    public void exitTest() {
        ButtonDebouncer debouncer = mock(ButtonDebouncer.class);
        when(debouncer.get()).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true);
        SubstateToggle toggle = new SubstateToggle(debouncer);

        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertTrue(toggle.isToggled());
        toggle.exit();
        toggle.update();
        Assertions.assertFalse(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
    }

    @Test
    public void setTest() {
        ButtonDebouncer debouncer = mock(ButtonDebouncer.class);
        when(debouncer.get()).thenReturn(true);
        SubstateToggle toggle = new SubstateToggle(debouncer);

        toggle.exit();
        toggle.setToggled(true);
        Assertions.assertFalse(toggle.isEnabled());
        toggle.setEnabled(true);
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertTrue(toggle.isToggled());
        toggle.setToggled(false);
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
        toggle.setEnabled(false);
        Assertions.assertFalse(toggle.isEnabled());
        toggle.exit();
        Assertions.assertFalse(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
    }
}
