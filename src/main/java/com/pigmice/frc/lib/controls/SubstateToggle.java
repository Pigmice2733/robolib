package com.pigmice.frc.lib.controls;

/**
 * SubstateToggle is a control utility for toggling within another mode. For
 * example, having a button that toggles the position of an arm between two
 * positions, but where hitting other buttons should move the arm to other
 * positions.
 *
 * When the button is pressed, the 'substate' is enabled, and hitting
 * the button additional times triggers the toggle. Once enabled, the
 * 'substate' must be explicitly exited. The substate can be queried to
 * determine whether it is enabled. The toggle has two states:
 * <code>true</code> and <code>false</code>. These states can mean whatever you
 * want them to mean, in whatever context.
 *
 * Whenever the substate is first enabled, the state of the toggle is set to
 * <code>false</code>. If this is not desired, use the
 * {@link #setToggled(boolean)} method to set the toggle to specific state.
 *
 * @see {@link #controls.Toggle Toggle}
 */
public class SubstateToggle {
    private boolean enabled = false;
    private boolean toggled = false;
    private ButtonDebouncer button;

    /**
     * Constructs a SubstateToggle given the control button
     *
     * @param button The button that will control the toggle
     */
    public SubstateToggle(ButtonDebouncer button) {
        this.button = button;
    }

    /**
     * Update the substate and the toggle so the 'toggling' is tracked. This should
     * be called every frame while this SubstateToggle is being actively used.
     */
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

    /**
     * Gets whether the substate of this SubstateToggle is enabled - that is,
     * whether this toggle is activated at all.
     *
     * @return <code>true></code> if enabled, <code>false</code> otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the substate is enabled. If the substate is not enabled, and is
     * set to enabled, the toggle state will be set to <code>false</code>.
     *
     * @param enabled Whether the substate is enabled
     */
    public void setEnabled(boolean enabled) {
        if (!this.enabled && enabled) {
            toggled = false;
        }
        this.enabled = enabled;
    }

    /**
     * Gets whether this toggle is 'toggled' or not. This value is only meaningful
     * while the substate is enabled, which can be checked via {@link #isEnabled()}
     *
     * @return The current state of this toggle, either <code>true</code> or
     *         <code>false</code>
     */
    public boolean isToggled() {
        return toggled;
    }

    /**
     * Sets whether this toggle is 'toggled' or not. This will not affect whether
     * the substate is enabled or not.
     *
     * @param toggled Whether this toggle is 'toggled'
     */
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    /**
     * Exits (disables) the substate. For example, if this toggle is toggling
     * between two specific arm positions, the substate should be exited via this
     * method whenever the arm is moved to any position other than those two.
     */
    public void exit() {
        enabled = false;
        toggled = false;
    }
}
