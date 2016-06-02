package ai;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by Mathias on 24-05-2016.
 */
public class SAAStarPlanner {

    private PriorityQueue<SAState> frontier;
    public HashSet< SAState > explored;
    public long startTime = System.currentTimeMillis();
    public float timeSpent() {
        return ( System.currentTimeMillis() - startTime ) / 1000f;
    }

    public SAAStarPlanner() {
        explored = new HashSet<>();
        frontier = new PriorityQueue<>(new SAHeuristic());
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

    public String searchStatus() {
        return String.format( "#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", countExplored(), countFrontier(), timeSpent(), Memory.stringRep() );
    }

    public LinkedList<SAState> generatePlan() {
        SAState initialState = new SAState(null);
        Cell[][] map = Supervisor.getInstance().getLevel().getMap();
        initialState.boxes = new char[SAState.MAX_ROW][SAState.MAX_COLUMN];

        HashMap<Integer,Box> boxes = Supervisor.getInstance().getLevel().getBoxes();
        HashMap<Integer,Point> boxesPos = new HashMap<>();
        for(Box b: boxes.values()){
            initialState.boxes[b.location.y][b.location.x] = b.letter;
            boxesPos.put(b.id,new Point(b.location));
        }

        initialState.setHeuristicBoxes(boxesPos);
        Point agentPos = Supervisor.getInstance().getSingleAgent().getPosition();
        initialState.agentCol = agentPos.x;
        initialState.agentRow = agentPos.y;


        addToFrontier( initialState );

        int iterations = 0;
        while ( true ) {
            if ( iterations % 200 == 0 ) {
                System.err.println( searchStatus() );
            }
            if ( Memory.shouldEnd() ) {
                System.err.format( "Memory limit almost reached, terminating search %s\n", Memory.stringRep() );
                return null;
            }

            if ( timeSpent() > 300) { // Minutes timeout
                System.err.format( "Time limit reached, terminating search %s\n", Memory.stringRep() );
                return null;
            }

            if ( frontierIsEmpty() ) {
                return null;
            }

            SAState leafNode = getAndRemoveLeaf();

            if ( leafNode.isGoalState() ) {
                System.err.println( searchStatus() );
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
