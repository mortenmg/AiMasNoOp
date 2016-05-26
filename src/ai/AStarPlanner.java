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
    private int agentId;

    public AStarPlanner(int agentId){
        super();
        this.agentId = agentId;
    }

    // The planner generates a plan
    public LinkedList<State> generatePlan(State initialState, Task task) {
        SimpleHeuristic heuristic = new SimpleHeuristic(task, 0);
        frontier = new PriorityQueue<>(10, heuristic);
        explored = new HashSet<>();

        // Creates the initial state with the boxes as they are in the moment
        this.initialState = initialState;
        initialState.setBoxes(Supervisor.getInstance().getLevel().getBoxes());
        initialState.setTask(task);
        initialState.agentCol = Supervisor.getInstance().getAgentWithId(agentId).getPosition().x;
        initialState.agentRow = Supervisor.getInstance().getAgentWithId(agentId).getPosition().y;

        frontier.add( initialState );

        int iterations = 0;
        while (true) {
            if (iterations % 200 == 0) {
                //System.err.println(searchStatus());
            }

            if (frontier.isEmpty()) {
                System.err.println("The A* frontier is empty. Explored: "+explored.size());
                return null;
            }

            State leafState = frontier.poll();
            //System.err.println("We are now exploring #state "+iterations+ " #MAgent pos "+leafState.agentCol+","+leafState.agentRow+ " #Action: "+leafState.action + " #Box "+leafState.getBoxes().get(0).location);

            if (isGoalState(leafState, task)) {
                return leafState.extractPlan();
            }

            explored.add(leafState);
            for (State n : leafState.getExpandedNodes()) { // The list of expanded nodes is shuffled randomly; see Planning.searchclient.ai.State.java
                if (!explored.contains(n) && !frontier.contains(n)) {
                    frontier.add(n);
                }
            }
            iterations++;
        }
    }

    public String toString() {
        return "AStar search";
    }

    public String searchStatus() {
        return String.format( "#Explored: %4d, #Frontier: %3d, Time: %s \t%s", explored.size(), frontier.size(), "12", "13");//timeSpent(), ai.Memory.stringRep() );
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
    private boolean isGoalState(State state, Task task) {
        //System.err.println("Is this state a goal state? Box: "+state.getBoxes().get(0).location+" MAgent: "+state.agentRow+", "+state.agentCol);
        Box box = state.getBoxes().get(task.getBoxId());
        Goal g = Supervisor.getInstance().getLevel().getGoalWithId(task.getGoalId());
        return box.location.equals(g.point);
    }
}
