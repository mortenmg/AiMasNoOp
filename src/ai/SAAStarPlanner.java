package ai;

import java.awt.*;
import java.util.*;

/**
 * Created by Mathias on 24-05-2016.
 */
public class SAAStarPlanner {

    private PriorityQueue<SAState> frontier;
    public HashSet< SAState > explored;
    private ArrayList<GoalTask> goalTasks;
    public long startTime = System.currentTimeMillis();
    public float timeSpent() {
        return ( System.currentTimeMillis() - startTime ) / 1000f;
    }

    public SAAStarPlanner() {
        explored = new HashSet<>();
        goalTasks = new ArrayList<>();
    }

    public SAState getAndRemoveLeaf() {
        return frontier.remove();
    }
    public void addToFrontier(SAState n) {
        frontier.add(n);
    }
    public int countFrontier() {
        return frontier.size();
    }
    public boolean frontierIsEmpty() {
        return frontier.isEmpty();
    }
    public boolean inFrontier(SAState n) {
        return frontier.contains(n);
    }

    public void addToExplored( SAState n ) {
        explored.add( n );
    }
    public boolean isExplored( SAState n ) {
        return explored.contains( n );
    }
    public int countExplored() {
        return explored.size();
    }

    private static int totalExploredCount = 0;
    private static float totalTimeSpent = 0;

    public String searchStatus() {
        return String.format( "#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", countExplored(), countFrontier(), timeSpent(), Memory.stringRep() );
    }

    public LinkedList<SAState> generatePlan(SAState initialState) {

        this.goalTasks = SAState.goalTasks;
        frontier = new PriorityQueue<>(new SAHeuristic(goalTasks));

        addToFrontier( initialState );

        int iterations = 0;
        while ( true ) {
            if ( iterations % 200 == 0 ) {
                System.err.println( searchStatus() );
            }
            if ( Memory.shouldEnd() ) {
                System.err.format( "Memory limit almost reached, terminating search %s\n", Memory.stringRep() );
                System.err.println( searchStatus() );
                return null;
            }

            if ( timeSpent() > 300) { // Minutes timeout
                System.err.format( "Time limit reached, terminating search %s\n", Memory.stringRep() );
                System.err.println( searchStatus() );
                return null;
            }

            if ( frontierIsEmpty() ) {
                return null;
            }

            SAState leafNode = getAndRemoveLeaf();

            if ( leafNode.isGoalState() ) {
                System.err.println( searchStatus() );
                totalExploredCount += countExplored();
                totalTimeSpent += timeSpent();

                System.err.println( totalExploredCount );
                System.err.println( totalTimeSpent );
                return leafNode.extractPlan();


            }

            addToExplored( leafNode );
            for ( SAState n : leafNode.getExpandedNodes() ) { // The list of expanded nodes is shuffled randomly; see Node.java
                if ( !isExplored( n ) && !inFrontier( n ) ) {
                    addToFrontier( n );
                }
            }
            iterations++;
        }

    }
}
