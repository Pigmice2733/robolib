package com.pigmice.frc.lib.inputs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ToggleTest {
    @Test
    public void getTest() {
        InputMock input = new InputMock(new boolean[] { false, false, true, false, false, false, true });

        Toggle toggle = new Toggle(input);

        input.update();
        toggle.update();
        Assertions.assertFalse(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertFalse(toggle.get());
    }

    @Test
    public void setTest() {
        InputMock input = new InputMock(new boolean[] { false, false, true, false, false, true, true });

        Toggle toggle = new Toggle(input);

        toggle.set(true);

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertFalse(toggle.get());

        input.update();
        toggle.set(true);
        toggle.update();
        Assertions.assertTrue(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertFalse(toggle.get());

        input.update();
        toggle.update();
        Assertions.assertTrue(toggle.get());
    }
}
