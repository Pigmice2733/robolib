package com.pigmice.frc.lib.controls;

import com.pigmice.frc.lib.utils.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JoysticksTest {
    @Test
    public void testDeadzone() {
        Assertions.assertEquals(0, Joysticks.deadzone(0.0, 0.0));
        Assertions.assertEquals(0, Joysticks.deadzone(0.05, 0.1));
        Assertions.assertEquals(0, Joysticks.deadzone(-0.05, 0.1));
        Assertions.assertTrue(Utils.almostEquals(0.11111111, Joysticks.deadzone(0.2, 0.1)));
        Assertions.assertTrue(Utils.almostEquals(-0.11111111, Joysticks.deadzone(-0.2, 0.1)));
    }
}
