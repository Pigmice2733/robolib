package com.pigmice.frc.lib.controls;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubstateToggleTest {
    @Test
    public void toggleTest() {
        InputMock input = new InputMock(new boolean[] { false, false, true, true, true, false });

        SubstateToggle toggle = new SubstateToggle(input);

        input.update();
        toggle.update();
        Assertions.assertFalse(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertTrue(toggle.isToggled());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
    }

    @Test
    public void exitTest() {
        InputMock input = new InputMock(new boolean[] { false, true, true, false, true });

        SubstateToggle toggle = new SubstateToggle(input);

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertTrue(toggle.isToggled());

        input.update();
        toggle.exit();
        toggle.update();
        Assertions.assertFalse(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
    }

    @Test
    public void setTest() {
        InputMock input = new InputMock(new boolean[] { true });

        SubstateToggle toggle = new SubstateToggle(input);

        input.update();
        toggle.exit();
        toggle.setToggled(true);
        Assertions.assertFalse(toggle.isEnabled());

        input.update();
        toggle.setEnabled(true);
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertTrue(toggle.isToggled());

        input.update();
        toggle.setToggled(false);
        Assertions.assertTrue(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());

        input.update();
        toggle.setEnabled(false);
        Assertions.assertFalse(toggle.isEnabled());

        input.update();
        toggle.exit();
        Assertions.assertFalse(toggle.isEnabled());
        Assertions.assertFalse(toggle.isToggled());
    }
}
