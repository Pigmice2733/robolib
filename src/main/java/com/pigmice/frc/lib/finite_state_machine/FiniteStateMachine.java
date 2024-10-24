package com.pigmice.frc.lib.finite_state_machine;

import java.util.HashMap;
import java.util.Map;

public class FiniteStateMachine<State extends Enum<?>, RobotData> {
    private State currentState;
    private final Map<State, Transition<State, RobotData>[]> nodeTransitionMap;

    /**
     * A <a href="https://en.wikipedia.org/wiki/Finite-state_machine">state
     * machine</a> that
     * consists of nodes with transitions connecting them.
     */
    public FiniteStateMachine(State initialState,
            Map<State, Transition<State, RobotData>[]> nodeTransitionMap) {
        currentState = initialState;

        this.nodeTransitionMap = nodeTransitionMap;
    }

    /**
     * A <a href="https://en.wikipedia.org/wiki/Finite-state_machine">state
     * machine</a> that
     * consists of nodes with transitions connecting them.
     */
    public FiniteStateMachine(State initialState) {
        currentState = initialState;
        nodeTransitionMap = new HashMap<State, Transition<State, RobotData>[]>();
    }

    /**
     * Runs the first valid transition of the current state. Call this method
     * periodically.
     */
    public boolean execute(RobotData robotData) {
        Transition<State, RobotData>[] transitions = nodeTransitionMap.get(currentState);

        if (transitions == null)
            return false;

        for (Transition<State, RobotData> t : transitions) {
            if (t.shouldExecute(robotData)) {
                currentState = t.execute(robotData);
                return true;
            }
        }

        return false;
    }

    /**
     * Adds all the transitions leading away from a state.
     * 
     * @param state       the starting state
     * @param transitions a list of transitions in order of priority
     */
    @SafeVarargs
    public final void addTransitionsFromState(State state,
            Transition<State, RobotData>... transitions) {

        nodeTransitionMap.put(state, transitions);
    }
}
