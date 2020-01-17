package com.pigmice.frc.lib.inputs;

/**
 * Toggle is a control utility for toggling something, such as a specific
 * operation mode. The toggle is connected to a controlling input, such as a
 * button. The toggle has two states: <code>true</code> and <code>false</code>.
 * These states can mean whatever you want them to mean, in whatever context. If
 * the input is a raw controller input such as a button, you will likely want to
 * wrap it in a {@link com.pigmice.frc.lib.controls.Debouncer Debouncer} to
 * ensure that pressing the button once doesn't switch this Toggle multiple
 * times.
 *
 * @see {@link com.pigmice.frc.lib.controls.SubstateToggle
 *      SubstateToggle}
 */
public class Toggle implements IBooleanSource {
    private IBooleanSource source;
    private boolean state = false;

    /**
     * Constructs a Toggle given boolean source, such as a lambda to retrieve a
     * controller input, or a Debouncer wrapping a button.
     *
     * @param button The button that will control the toggle
     */
    public Toggle(IBooleanSource source) {
        this.source = source;
    }

    /**
     * Gets the current state of this toggle
     *
     * @return Toggle activation state
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
     * Update the toggle so the 'toggling' is tracked. This should be called
     * regularly while this Toggle is being actively used.
     */
    public void update() {
        if (source.get()) {
            state = !state;
        }
    }
}
