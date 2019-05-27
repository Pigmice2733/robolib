package com.pigmice.frc.lib.controls;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * NetworkTables-based Selector. Useful for allowing selection of autonomous
 * mode from dashboard on DriverStation, for example, or for selecting a victory
 * dance move.
 *
 * In a specified table, Selector will create two entries - an string array of
 * options names under the key <code>options</code>, and a string that holds the
 * current option under the key <code>selected</code>. The option will be
 * initialized with the default option. To make a selection over NetworkTables,
 * change the option string to any of the names in the options array.
 *
 * @param <E> Type of what is being selected - this can be an interface or base
 *            class for autonomous modes, or just Integer, String, etc.
 */
public class Selector<E> {
    private NetworkTableEntry selected;
    private NetworkTableEntry options;

    private Map<String, E> values;

    private E defaultValue;

    /**
     * Constructs a Selector given a table to place options in, a default option
     * name and the value to associate with that name.
     *
     * @param tableName     The name of the table to put the options and selected
     *                      option entries in. The table can be a subtable of
     *                      another, to do this specify the name in the following
     *                      form: '/table/subtable'.
     * @param defaultOption The name of the default option
     * @param defaultValue  The default option value
     */
    public Selector(String tableName, String defaultOption, E defaultValue) {
        this(NetworkTableInstance.getDefault().getTable(tableName), defaultOption, defaultValue);
    }

    /**
     * Constructs a Selector given a table to place options in, a default option
     * name and the value to associate with that name.
     *
     * @param table         The table to put the options and selected option entries
     *                      in
     * @param defaultOption The name of the default option
     * @param defaultValue  The default option value
     */
    public Selector(NetworkTable table, String defaultOption, E defaultValue) {
        options = table.getEntry("options");
        selected = table.getEntry("selected");

        values = new HashMap<>();

        setDefault(defaultOption, defaultValue);
    }

    /**
     * Sets a new default option
     *
     * @param name  The name of the new default
     * @param value The value of the new default
     */
    public void setDefault(String name, E value) {
        defaultValue = value;
        selected.setString(name);
        addOption(name, value);
    }

    /**
     * Adds a new option to this selector
     *
     * @param name  The name of the new option
     * @param value The value of the new option
     */
    public void addOption(String name, E value) {
        values.put(name, value);
        Set<String> keys = values.keySet();
        options.setStringArray(keys.toArray(new String[keys.size()]));
    }

    /**
     * Retrieves the value of the currently selected option. If the selected option
     * name is not a valid option name, the default value will be returned.
     *
     * @return The selected option value
     */
    public E getSelected() {
        String name = selected.getString("");
        if (!values.containsKey(name)) {
            return defaultValue;
        }

        return values.get(name);
    }
}
