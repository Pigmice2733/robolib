package com.pigmice.frc.lib.motion.execution;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pigmice.frc.lib.pidf.PIDF;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class StaticSteeringControllerTest {
    private final PIDF pid = Mockito.mock(PIDF.class);

    @Test
    public void noTargetTest() {
        StaticSteeringController controller = new StaticSteeringController(() -> {
            return 0.24;
        }, pid);
        controller.initialize();

        when(pid.calculateOutput(eq(0.24), eq(0.0), anyDouble())).thenReturn(0.0).thenReturn(0.5).thenReturn(-1.0);

        Assertions.assertEquals(0.0, controller.correct());
        Assertions.assertEquals(0.5, controller.correct());
        Assertions.assertEquals(-0.5, controller.correct());
    }

    @Test
    public void targetTest() {
        StaticSteeringController controller = new StaticSteeringController(() -> {
            return -0.24;
        }, pid, 1.0);
        controller.initialize();

        when(pid.calculateOutput(eq(-0.24), eq(1.0), anyDouble())).thenReturn(0.0);
        when(pid.calculateOutput(eq(-0.24), eq(-1.0), anyDouble())).thenReturn(-1.0);
        when(pid.calculateOutput(eq(-0.24), eq(0.0), anyDouble())).thenReturn(5.0);

        Assertions.assertEquals(0.0, controller.correct());
        Assertions.assertEquals(-1.0, controller.correct(-1.0));

        controller.initialize(0.0);

        Assertions.assertEquals(5.0, controller.correct());

        verify(pid, times(2)).initialize(eq(-0.24), anyDouble(), eq(0.0));
    }
}
