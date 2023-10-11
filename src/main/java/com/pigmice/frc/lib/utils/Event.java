package com.pigmice.frc.lib.utils;

import java.util.ArrayList;

public class Event {
    ArrayList<Runnable> listeners = new ArrayList<Runnable>();

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void run() {
        listeners.forEach((listener) -> listener.run());
    }
}
