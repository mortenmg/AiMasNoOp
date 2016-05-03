package Planning;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by hvingelby on 4/19/16.
 */
public class AStarPlanner implements Planner{
    private PriorityQueue<State> frontier;
    public HashSet<State> explored;
    public State initialState = null;


    // The planner generates a plan
    public LinkedList<State> generatePlan (State initialState) {
        SimpleHeuristic heuristic = new SimpleHeuristic();
        frontier = new PriorityQueue<>(10,heuristic);
        explored = new HashSet<>();
        this.initialState = initialState;

            int iterations = 0;
            while ( true ) {
                if ( iterations % 200 == 0 ) {
                    System.err.println( searchStatus() );
                }

                if ( frontier.isEmpty() ) {
                    return null;
                }

                State leafState = frontier.poll();

                if ( leafState.isGoalState() ) {
                    return leafState.extractPlan();
                }

                explored.add(leafState);
                for ( State n : leafState.getExpandedNodes() ) { // The list of expanded nodes is shuffled randomly; see Planning.searchclient.State.java
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
        return String.format( "#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", explored.size(), frontier.size(), 12, 13);//timeSpent(), Memory.stringRep() );
    }
}
