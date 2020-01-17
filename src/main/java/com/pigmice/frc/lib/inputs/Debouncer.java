package com.pigmice.frc.lib.inputs;

/**
 * Debouncer wraps a raw boolean controller input and provides simple
 * debouncing.
 *
 * Debouncing is controlling or rate-limiting how often a button, switch, or
 * other signal fires. Often it is useful to perform on action only once when a
 * button is pressed, regardless of how long the button is held down.
 *
 * To use this to debounce a button or other source, construct a new Debouncer
 * object from a lambda to get the input state, and then regularly call
 * {@link #update()} several times per second to ensure button presses/releases
 * are registered.
 */
public class Debouncer implements IBooleanSource {
    private IBooleanSource source;

    private boolean previousState = false;
    private boolean currentState = false;

    /**
     * Constructs a Debouncer that debounces the boolean controller input. Note:
     * the controller input can be passed as a lambda.
     *
     * @param source     The controller input to debounce
     */
    public Debouncer(IBooleanSource source) {
        this.source = source;
    }

    /**
     * Gets whether the input has been activated. While the input is activated,
     * only returns <code>true</code> until the next {@link #update()}.
     *
     * @return Whether the input has been activated
     */
    public boolean get() {
        return currentState;
    }

    public void update() {
        boolean inputIsActivated = source.get();
        currentState = !previousState && inputIsActivated;
        previousState = inputIsActivated;
    }
}
