package com.pigmice.frc.lib.controls;

public class SubstateToggle {
    private boolean enabled = false;
    private boolean toggled = false;
    private ButtonDebouncer button;

    public SubstateToggle(ButtonDebouncer button) {
        this.button = button;
    }

    public void update() {
        if (button.get()) {
            if (enabled) {
                toggled = !toggled;
            } else {
                enabled = true;
                toggled = false;
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void exit() {
        enabled = false;
        toggled = false;
    }
}
