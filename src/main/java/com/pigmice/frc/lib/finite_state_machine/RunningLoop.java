package com.pigmice.frc.lib.finite_state_machine;

public abstract class RunningLoop<State extends Enum<?>, RobotData>
        extends Transition<State, RobotData> {

    /** A transition leading from a state back into the same state. */
    public RunningLoop(State runningState) {
        super(runningState);
    }

    @Override
    public final boolean shouldExecute(RobotData robotData) {
        return true;
    }

    @Override
    public final State execute(RobotData robotData) {
        run(robotData);
        return to;
    }

    /** Called periodically while the node is running. */
    protected abstract void run(RobotData robotData);
}
