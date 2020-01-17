package com.pigmice.frc.lib.motion.execution;

import java.util.ArrayList;
import java.util.List;

import com.pigmice.frc.lib.motion.Setpoint;
import com.pigmice.frc.lib.utils.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfileExecutorTest {
    private boolean setpointEquals(Setpoint a, Setpoint b) {
        return Utils.almostEquals(a.getPosition(), b.getPosition()) &&
               Utils.almostEquals(a.getVelocity(), b.getVelocity()) &&
               Utils.almostEquals(a.getAcceleration(), b.getAcceleration()) &&
               Utils.almostEquals(a.getCurvature(), b.getCurvature()) &&
               Utils.almostEquals(a.getHeading(), b.getHeading());
    }

    @Test
    public void errorTest() {
        Setpoint[] data = new Setpoint[] { new Setpoint(5.0, 2.0, 0.0, 0.0, 0.0) };
        ProfileMock profile = new ProfileMock(data, 10);

        List<Double> input = new ArrayList<Double>();

        input.add(3.0);
        input.add(3.5);
        input.add(6.5);
        input.add(4.5);
        input.add(5.0);
        input.add(0.0);

        ProfileExecutor executor = new ProfileExecutor(
            profile, (Setpoint s) -> {}, () -> input.remove(0), 1.0
        );
        executor.initialize();

        Assertions.assertFalse(executor.update());
        Assertions.assertFalse(executor.update());
        Assertions.assertFalse(executor.update());
        Assertions.assertTrue(executor.update());
        Assertions.assertTrue(executor.update());
        Assertions.assertFalse(executor.update());
    }

    @Test
    public void outputTest() {
        Setpoint[] data = new Setpoint[] {
            new Setpoint(5.0, 2.0, 0.0, 0.0, 0.0),
            new Setpoint(7.0, 2.0, 0.0, 0.0, 0.0),
            new Setpoint(9.0, 2.0, 0.0, 0.0, 0.0),
            new Setpoint(11.0, 2.0, 0.0, 0.0, 0.0)
        };

        List<Setpoint> output = new ArrayList<Setpoint>();
        ProfileMock profile = new ProfileMock(data, 10);

        ProfileExecutor executor = new ProfileExecutor(
            profile, (Setpoint s) -> output.add(s), () -> 3, 1.0);
        executor.initialize();

        executor.update();
        executor.update();
        executor.update();
        executor.update();

        Assertions.assertTrue(setpointEquals(output.get(0), data[0]));
        Assertions.assertTrue(setpointEquals(output.get(1), data[1]));
        Assertions.assertTrue(setpointEquals(output.get(2), data[2]));
        Assertions.assertTrue(setpointEquals(output.get(3), data[3]));
    }
}
