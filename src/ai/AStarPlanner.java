package ai;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import static ai.State.MAX_COLUMN;
import static ai.State.MAX_ROW;

/**
 * Created by hvingelby on 4/19/16.
 */
public class AStarPlanner implements Planner {
    private PriorityQueue<State> frontier;
    public HashSet<State> explored;
    public State initialState = null;


    // The planner generates a plan
    public LinkedList<State> generatePlan (State initialState, Task task) {
        SimpleHeuristic heuristic = new SimpleHeuristic(new GoalTask(0), 0);
        frontier = new PriorityQueue<>(10,heuristic);
        explored = new HashSet<>();
        this.initialState = initialState;
        // TODO: copy the boxes from the global level as they are in the moment
        // initialState.setBoxes(SOMETHING FANCY);

            int iterations = 0;
            while ( true ) {
                if ( iterations % 200 == 0 ) {
                    System.err.println( searchStatus() );
                }

                if ( frontier.isEmpty() ) {
                    return null;
                }

                State leafState = frontier.poll();

                if ( isGoalState(leafState, task) ) {
                    return leafState.extractPlan();
                }

                explored.add(leafState);
                for ( State n : leafState.getExpandedNodes() ) { // The list of expanded nodes is shuffled randomly; see Planning.searchclient.ai.State.java
                    if ( ! explored.contains( n ) && !frontier.contains(n) ) {
                        frontier.add( n );
                    }
                }
                iterations++;
            }
        }

    public String toString() {
        return "AStar search";
    }

    public String searchStatus() {
        return String.format( "#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", explored.size(), frontier.size(), 12, 13);//timeSpent(), ai.Memory.stringRep() );
    }

    /**
     * Determines whether the given state fullfills the goal
     * of the given task.s
     *
     * @author Rasmus
     * @param state
     * @param task
     * @return Boolean
     */
    public boolean isGoalState(State state, Task task) {
        Box box = state.getBoxes().get(task.getBoxId());
        // TODO: if box.coordinates == task.getGoal().coordinates then it is a goal!
        return true;
    }
}