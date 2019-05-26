package com.pigmice.frc.lib.controls;

import edu.wpi.first.wpilibj.GenericHID;

public class Toggle {
    private ButtonDebouncer button;
    private boolean state = false;

    public Toggle(GenericHID joystick, int button) {
        this(new ButtonDebouncer(joystick, button));
    }

    public Toggle(GenericHID joystick, int button, boolean defaultState) {
        this(new ButtonDebouncer(joystick, button), defaultState);
    }

    public Toggle(ButtonDebouncer button) {
        this(button, false);
    }

    public Toggle(ButtonDebouncer button, boolean defaultState) {
        this.button = button;
        this.state = defaultState;
    }

    public boolean get() {
        return state;
    }

    public void set(boolean state) {
        this.state = state;
    }

    public void update() {
        if (button.get()) {
            state = !state;
        }
    }
}
