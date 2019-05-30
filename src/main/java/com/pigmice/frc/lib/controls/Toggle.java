package com.pigmice.frc.lib.controls;

import edu.wpi.first.wpilibj.GenericHID;

/**
 * Toggle is a control utility for toggling something, such as a specific
 * operation mode. The toggle is connected to a controlling button. The toggle
 * has two states: <code>true</code> and <code>false</code>. These states can
 * mean whatever you want them to mean, in whatever context.
 *
 * @see {@link #controls.SubstateToggle SubstateToggle}
 */
public class Toggle {
    private ButtonDebouncer button;
    private boolean state = false;

    /**
     * Constructs a toggle given a specific button. The button is internally
     * debounced to prevent the toggle toggling every frame while the button is
     * pressed. Default toggle state is <code>false</code>.
     *
     * @param joystick The controller the toggle button is on
     * @param button   The number of the toggle button
     */
    public Toggle(GenericHID joystick, int button) {
        this(new ButtonDebouncer(joystick, button));
    }

    /**
     * Constructs a toggle given a specific button. The button is internally
     * debounced to prevent the toggle toggling every frame while the button is
     * pressed.
     *
     * @param joystick     The controller the toggle button is on
     * @param button       The number of the toggle button
     * @param defaultState The default state of the toggle
     */
    public Toggle(GenericHID joystick, int button, boolean defaultState) {
        this(new ButtonDebouncer(joystick, button), defaultState);
    }

    /**
     * Constructs a toggle given a specific button. Default toggle state is
     * <code>false</code>.
     *
     * @param button The button that will control the toggle
     */
    public Toggle(ButtonDebouncer button) {
        this(button, false);
    }

    /**
     * Constructs a toggle given a specific button.
     *
     * @param button       The button that will control the toggle
     * @param defaultState The default state of the toggle
     */
    public Toggle(ButtonDebouncer button, boolean defaultState) {
        this.button = button;
        this.state = defaultState;
    }

    /**
     * Gets the current state of this toggle
     *
     * @return Toggle state, either <code>true</code> or <code>false</code>
     */
    public boolean get() {
        return state;
    }

    /**
     * Sets this toggle to a specific state
     *
     * @param state Desired toggle state
     */
    public void set(boolean state) {
        this.state = state;
    }

    /**
     * Update the toggle so the 'toggling' is tracked. This should be called every
     * frame while this Toggle is being actively used.
     */
    public void update() {
        if (button.get()) {
            state = !state;
        }
    }
}
