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

        //int pathHeuristic = Supervisor.getInstance().getLevel().getCostForCoordinateWithGoal(n.agentCol, n.agentRow, n.getTask().getGoalId());

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

/*  // To slow have to figure better heuristic
        int h = 0;
        n.getBoxes().values();
        // Calculate for all boxes
        for (Box b: n.getBoxes().values()) {
            for (Goal g: Supervisor.getInstance().getLevel().getGoals().values()) {
                h += Supervisor.getInstance().getLevel().getCostForCoordinateWithGoal(b.location.x,b.location.y,g.id);
            }
        }
*/


        Goal g = Supervisor.getInstance().getLevel().getGoalWithId(task.getGoalId());
        Box b = n.getBoxes().get(task.getGoalId());
        int h = Supervisor.getInstance().getLevel().getCostForCoordinateWithGoal(b.location.x,b.location.y,task.getGoalId());

        //int h = euclidean(b.location.x, b.location.y, g.point.x, g.point.y);
        //int h = 0;
        // System.err.println("Heuristics: " + he);
        return h;
    }


    public static int distanceToGoal(Level level, int agentX, int agentY,int boxX, int boxY, int goalId){
        int agentToGoal = level.getCostForCoordinateWithGoal(agentX,agentY,goalId);
        int boxToGoal = level.getCostForCoordinateWithGoal(boxX,boxY,goalId);
        int euclid = euclidean(agentX,agentY,boxX,boxY);
        return agentToGoal + boxToGoal + euclid;
    }

    public static int euclidean(int x1, int y1, int x2, int y2){
        return (int)Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
    }

}
