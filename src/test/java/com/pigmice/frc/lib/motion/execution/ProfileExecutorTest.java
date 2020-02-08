package com.pigmice.frc.lib.motion.execution;

import java.util.ArrayList;
import java.util.List;

import com.pigmice.frc.lib.controllers.IController;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.pigmice.frc.lib.motion.setpoint.Setpoint;
import com.pigmice.frc.lib.utils.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfileExecutorTest {
    private static boolean setpointEquals(ISetpoint a, ISetpoint b) {
        return Utils.almostEquals(a.getPosition(), b.getPosition())
                && Utils.almostEquals(a.getVelocity(), b.getVelocity())
                && Utils.almostEquals(a.getAcceleration(), b.getAcceleration())
                && Utils.almostEquals(a.getCurvature(), b.getCurvature())
                && Utils.almostEquals(a.getHeading(), b.getHeading());
    }

    private static class PassThroughController implements IController {
        public List<ISetpoint> outputs = new ArrayList<>();

        public void initialize(double input, double currentOutput) { }

        public double calculateOutput(double input, ISetpoint setpoint) {
            outputs.add(setpoint);
            return setpoint.getPosition();
        }
    }

    private static class Input implements ProfileExecutor.Input {
        public double value;

        public double get() {
            return value;
        }
    }

    @Test
    public void errorTest() {
        Setpoint[] data = new Setpoint[] { new Setpoint(5.0, 2.0, 0.0, 0.0, 0.0) };
        ProfileMock profile = new ProfileMock(data, 10);
        Input timeInput = new Input();
        PassThroughController controller = new PassThroughController();

        Input input = new Input();

        ProfileExecutor executor = new ProfileExecutor(
            profile, controller, (double sp) -> {}, input, 1.0, 0.25, timeInput
        );

        timeInput.value = 0.0;
        input.value = 0.0;

        executor.initialize();

        // Too far away
        Assertions.assertFalse(executor.update());

        // Still too far
        timeInput.value = 1.0;
        input.value = 0.0;
        Assertions.assertFalse(executor.update());

        // At target, but too fast
        timeInput.value = 2.0;
        input.value = 5.0;
        Assertions.assertFalse(executor.update());

        // Close to target, slow enough
        timeInput.value = 3.0;
        input.value = 5.1;
        Assertions.assertTrue(executor.update());

        // Too far, too fast
        timeInput.value = 4.0;
        input.value = 10.0;
        Assertions.assertFalse(executor.update());

        // Close enough, too fast
        timeInput.value = 5.0;
        input.value = 5.0;
        Assertions.assertFalse(executor.update());

        // Close to target, slow enough
        timeInput.value = 10.0;
        input.value = 4.1;
        Assertions.assertTrue(executor.update());
    }

    @Test
    public void outputTest() {
        Setpoint[] data = new Setpoint[] {
            new Setpoint(5.0, 2.0, 0.0, 0.0, 0.0),
            new Setpoint(7.0, 2.0, 0.0, 0.0, 0.0),
            new Setpoint(9.0, 2.0, 0.0, 0.0, 0.0),
            new Setpoint(11.0, 2.0, 0.0, 0.0, 0.0)
        };

        ProfileMock profile = new ProfileMock(data, 10);
        PassThroughController controller = new PassThroughController();

        Input timeInput = new Input();
        timeInput.value = 0.0;

        ProfileExecutor executor = new ProfileExecutor(
            profile, controller, (double sp) -> {}, () -> 3, 1.0, 0.1, timeInput);
        executor.initialize();

        executor.update();
        executor.update();
        executor.update();
        executor.update();

        Assertions.assertTrue(setpointEquals(controller.outputs.get(0), data[0]));
        Assertions.assertTrue(setpointEquals(controller.outputs.get(1), data[1]));
        Assertions.assertTrue(setpointEquals(controller.outputs.get(2), data[2]));
        Assertions.assertTrue(setpointEquals(controller.outputs.get(3), data[3]));
    }
}
