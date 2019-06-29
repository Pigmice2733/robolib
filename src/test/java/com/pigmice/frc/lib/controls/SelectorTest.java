package com.pigmice.frc.lib.controls;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

@ExtendWith(MockitoExtension.class)
public class SelectorTest {
    @Mock
    NetworkTableEntry selected;

    @Mock
    NetworkTableEntry options;

    @Captor
    ArgumentCaptor<String[]> keysCaptor;

    @InjectMocks
    Selector<Integer> injectedSelector = new Selector<Integer>("/main/auto", "back", 0);

    @Test
    public void testInjectedSelector() {
        when(selected.getString("")).thenReturn("farScale");

        injectedSelector.setDefault("forward", 1);
        injectedSelector.addOption("farScale", 2);

        verify(options, times(2)).setStringArray(keysCaptor.capture());

        List<String[]> keys = keysCaptor.getAllValues();

        assertThat("Options keys equality unordered", Arrays.asList(keys.get(0)),
                containsInAnyOrder("back", "forward"));
        assertThat("Options keys equality unordered", Arrays.asList(keys.get(1)),
            containsInAnyOrder("back", "forward", "farScale"));

        verify(selected).setString("forward");

        Assertions.assertEquals(2, injectedSelector.getSelected());

        verify(selected).getString("");
    }

    @Test
    public void testSelector() {
        when(selected.getString("")).thenReturn("farScale");

        NetworkTable table = mock(NetworkTable.class);

        when(table.getEntry("options")).thenReturn(options);
        when(table.getEntry("selected")).thenReturn(selected);

        Selector<Integer> selector = new Selector<>(table, "back", 0);

        selector.setDefault("forward", 1);
        selector.addOption("farScale", 2);

        verify(options, times(3)).setStringArray(keysCaptor.capture());

        List<String[]> keys = keysCaptor.getAllValues();

        assertThat("Options keys equality unordered", Arrays.asList(keys.get(0)),
                containsInAnyOrder("back"));
        assertThat("Options keys equality unordered", Arrays.asList(keys.get(1)),
                containsInAnyOrder("back", "forward"));
        assertThat("Options keys equality unordered", Arrays.asList(keys.get(2)),
            containsInAnyOrder("back", "forward", "farScale"));

        verify(selected).setString("back");
        verify(selected).setString("forward");

        Assertions.assertEquals(2, selector.getSelected());

        verify(selected).getString("");
    }

    @Test
    public void testDefault() {
        when(selected.getString("")).thenReturn("Gravitas Free Zone");

        NetworkTable table = mock(NetworkTable.class);

        when(table.getEntry("options")).thenReturn(options);
        when(table.getEntry("selected")).thenReturn(selected);

        Selector<Integer> selector = new Selector<>(table, "back", 0);

        selector.setDefault("forward", 1);
        selector.addOption("farScale", 2);

        Assertions.assertEquals(1, selector.getSelected());
    }
}
