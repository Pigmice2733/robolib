package com.pigmice.frc.lib.finite_state_machine;

public abstract class Transition<State extends Enum<?>, RobotData> {
    protected final State to;

    /** A transition between two states */
    public Transition(State to) {
        this.to = to;
    }

    /** Returns true if the requirement for this transition to run is met */
    public abstract boolean shouldExecute(RobotData robotData);

    /**
     * Any code in execute is run when this transition is used
     * 
     * @return the new state that is transitioned to
     **/
    public abstract State execute(RobotData robotData);
}
