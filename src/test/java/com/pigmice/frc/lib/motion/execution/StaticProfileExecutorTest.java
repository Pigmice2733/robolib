package com.pigmice.frc.lib.motion.execution;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.pigmice.frc.lib.motion.Setpoint;
import com.pigmice.frc.lib.motion.StaticProfile;

import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.hamcrest.beans.SamePropertyValuesAs;

public class StaticProfileExecutorTest {
    private final StaticProfile profile = Mockito.mock(StaticProfile.class);

    @Test
    public void updateTest() {
        List<Setpoint> setpoints = Arrays.asList(new Setpoint(0.0, 0.0, 1.0, 0.0, 0.0),
                new Setpoint(0.5, 1.0, 0.0, 0.0, 0.0), new Setpoint(1.5, 1.0, 0.0, 0.0, 0.0),
                new Setpoint(2.5, 1.0, -1.0, 0.0, 0.0), new Setpoint(3.0, 0.0, 0.0, 0.0, 0.0));

        Iterator<Setpoint> it = setpoints.iterator();
        when(profile.getSetpoint(anyDouble())).then((InvocationOnMock m) -> {
            if (it.hasNext()) {
                return it.next();
            } else {
                return setpoints.get(setpoints.size() - 1);
            }
        });

        List<Setpoint> executedSetpoints = new ArrayList<>();
        StaticProfileExecutor executor = new StaticProfileExecutor(profile, (Setpoint sp) -> {
            executedSetpoints.add(sp);
        }, () -> {
            return it.hasNext() ? 2.9 : 0.0;
        }, 0.2);
        executor.initialize();

        Assertions.assertFalse(executor.update());
        Assertions.assertFalse(executor.update());
        Assertions.assertFalse(executor.update());
        Assertions.assertFalse(executor.update());
        Assertions.assertFalse(executor.update());
        Assertions.assertTrue(executor.update());

        Assertions.assertEquals(setpoints.size(), executedSetpoints.size());
        for (int i = 0; i < setpoints.size(); i++) {
            assertThat(executedSetpoints.get(i), SamePropertyValuesAs.samePropertyValuesAs(setpoints.get(i)));
        }
    }

    @Test
    public void updateNoEndTest() {
        List<Setpoint> setpoints = Arrays.asList(new Setpoint(0.0, 0.0, 1.0, 0.0, 0.0),
                new Setpoint(0.5, 1.0, 0.0, 0.0, 0.0), new Setpoint(1.5, 1.0, 0.0, 0.0, 0.0),
                new Setpoint(2.5, 1.0, -1.0, 0.0, 0.0), new Setpoint(3.0, 0.0, 0.0, 0.0, 0.0));

        Iterator<Setpoint> it = setpoints.iterator();
        when(profile.getSetpoint(anyDouble())).then((InvocationOnMock m) -> {
            if (it.hasNext()) {
                return it.next();
            } else {
                return setpoints.get(setpoints.size() - 1);
            }
        });

        List<Setpoint> executedSetpoints = new ArrayList<>();
        StaticProfileExecutor executor = new StaticProfileExecutor(profile, (Setpoint sp) -> {
            executedSetpoints.add(sp);
        }, () -> {
            return it.hasNext() ? 2.9 : 0.0;
        }, 0.2);
        executor.initialize();

        for (int i = 0; i < 7; i++) {
            executor.updateNoEnd();
        }

        Assertions.assertEquals(7, executedSetpoints.size());
        for (int i = 0; i < executedSetpoints.size(); i++) {
            if (i < setpoints.size()) {
                assertThat(executedSetpoints.get(i), SamePropertyValuesAs.samePropertyValuesAs(setpoints.get(i)));
            } else {
                assertThat(executedSetpoints.get(i),
                        SamePropertyValuesAs.samePropertyValuesAs(setpoints.get(setpoints.size() - 1)));
            }
        }
    }
}
