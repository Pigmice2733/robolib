package com.pigmice.frc.lib.controls;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Selector<E> {
    private NetworkTableEntry selected;
    private NetworkTableEntry options;

    private Map<String, E> values;

    private E defaultValue;

    public Selector(String tableName, String defaultOption, E defaultValue) {
        NetworkTable table = NetworkTableInstance.getDefault().getTable(tableName);
        options = table.getEntry("options");
        selected = table.getEntry("selected");

        values = new HashMap<>();

        setDefault(defaultOption, defaultValue);
    }

    public void setDefault(String name, E value) {
        defaultValue = value;
        selected.setString(name);
        addOption(name, value);
    }

    public void addOption(String name, E value) {
        values.put(name, value);
        Set<String> keys = values.keySet();
        options.setStringArray(keys.toArray(new String[keys.size()]));
    }

    public E getSelected() {
        String name = selected.getString("");
        if (!values.containsKey(name)) {
            return defaultValue;
        }

        return values.get(name);
    }
}
