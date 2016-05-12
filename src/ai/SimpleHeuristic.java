package ai;

import java.util.Comparator;

/**
 * Created by hvingelby on 5/3/16.
 */
public class SimpleHeuristic implements Comparator<State>{
    private Task task;
    private int agentId;

    public SimpleHeuristic(Task task, int agentId) {
        this.task = task;
        this.agentId = agentId;
    }

    @Override
    public int compare(State n1, State n2 ) {
        return f( n1 ) - f( n2 );
    }

    /**
     * public abstract int f( ai.State n );
     * @param n
     * @return
     */
    public int f( State n ) {
        return n.g() + h( n );
    }

    /**
     * Calculate the heuristics
     *
     * In this simple case we use the euclidian distance from agent to
     * box, and from box to goal.
     *
     * @param n
     * @return
     */
    public int h( State n ) {


        int pathHeuristic = Supervisor.getInstance().getLevel().getCostForCoordinateWithGoal(n.agentCol, n.agentRow, n.getTask().getGoalId());

        /*
        int h = 0;
        // outerloop:
        for (int row = 1; row < n.MAX_ROW-1 ; row++ ) {
            for (int col = 1 ; col< n.MAX_COLUMN-1 ; col++ ) {
                char chr = n.boxes[row][col];
                if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
                    for (ai.point p : SearchClient.goalsAsPoints){
                        if (p.a == Character.toLowerCase(chr)){
                            h += euclidean(p,row,col);
                        }
                    }
                }
            }
        }
        */
        int h = 0;
        // System.err.println("Heuristics: " + he);
        return h;
    }

    public int euclidean(int x1, int y1, int x2, int y2){
        return (int)Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
    }
}
