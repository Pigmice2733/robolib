package com.pigmice.frc.lib.controls;

import edu.wpi.first.wpilibj.GenericHID;

public class ButtonDebouncer {
    private GenericHID joystick;
    private int buttonNumber;

    private boolean previousState = false;

    public ButtonDebouncer(GenericHID joystick, int buttonNumber) {
        this.joystick = joystick;
        this.buttonNumber = buttonNumber;
    }

    public boolean get() {
        boolean currentState = joystick.getRawButton(buttonNumber);
        if (!previousState && currentState) {
            previousState = currentState;
            return true;
        } else {
            previousState = currentState;
            return false;
        }
    }
}
