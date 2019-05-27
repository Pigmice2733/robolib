package com.pigmice.frc.lib.controls;

import edu.wpi.first.wpilibj.GenericHID;

/**
 * ButtonDebouncer wraps a raw controller button and provides simple debouncing.
 * Debouncing is controlling or rate-limiting how often a button, switch, or
 * other signal fires. When a key on a keyboard is pressed, it is either
 * registered as pressed just one time until it is released and pressed again,
 * or is only registered as pressed around once per second. If your keyboard
 * wasn't debounced, holding down a key would type that character potentially
 * hundreds of times per second.
 *
 * ButtonDebouncer only registers a button as being pressed once per
 * press-hold-release cycle.
 */
public class ButtonDebouncer {
    private GenericHID joystick;
    private int buttonNumber;

    private boolean previousState = false;

    /**
     * Constructs a ButtonDebouncer that wraps the specified button on the supplied
     * joystick or other controller with debouncing.
     *
     * @param joystick     The joystick (or suitable controller) to debounce a
     *                     button on
     * @param buttonNumber The number of the button to debounce
     */
    public ButtonDebouncer(GenericHID joystick, int buttonNumber) {
        this.joystick = joystick;
        this.buttonNumber = buttonNumber;
    }

    /**
     * Gets whether this button has been pressed. If this button was pressed the
     * last time <code>get()</code> was called, it will return <code>false</code>
     * until the button is released (and <code>get()</code> is called while it is
     * released) and pressed again.
     *
     * @return Whether this button has been pressed
     */
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
