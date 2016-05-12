package ai;

import ai.State;

import java.util.LinkedList;

/**
 * Created by hvingelby on 4/11/16.
 */
public interface Planner {
    // LinkedList<Action> generatePlan(ai.Task task);
    LinkedList<State> generatePlan (State initialState, Task task) ;
}