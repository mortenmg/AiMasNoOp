package ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Mathias on 24-05-2016.
 */
public class SAHeuristic implements Comparator<SAState> {

    private ArrayList<GoalTask> goalTasks;

    public SAHeuristic(ArrayList<GoalTask> goalTasks){
        this.goalTasks = goalTasks;
    }

    @Override
    public int compare(SAState n1, SAState n2) {
        return f( n1 ) - f( n2 );
    }

    private int f(SAState n){
        return h( n );
    }


    private int h(SAState n) {

        int h = 0;
        PriorityQueue<GoalTask> goalTasks = Supervisor.getInstance().getGoalTasks();


        for (GoalTask gt : this.goalTasks) {
            Point goalPos = Supervisor.getInstance().getLevel().getGoals().get(gt.getGoalId()).point;
            Point boxPos = n.getBoxWithIdPos(gt.getBoxId());
            h += Supervisor.getInstance().getLevel().getCostForCoordinateWithGoal(boxPos.x, boxPos.y, gt.getGoalId());

            h += SimpleHeuristic.euclidean(n.agentCol, n.agentRow, boxPos.x, boxPos.y);
        }

        int boxRow = -1;
        int boxCol = -1;
        if (n.action.actType == Command.type.Pull) {
            boxRow = n.parent.agentRow + n.dirToRowChange(n.action.dir2);
            boxCol = n.parent.agentCol + n.dirToColChange(n.action.dir2);
        }
        if (n.action.actType == Command.type.Pull) {
            //boxRow = n.agentRow + n.dirToRowChange()
        }
        Point movingBoxPos = new Point(boxCol, boxRow);
        int boxId = n.parent.getBoxIdFromPos(movingBoxPos);

        if (Supervisor.getInstance().isGoalFulfilledForBox(movingBoxPos, boxId)) {
            h += 2;
        }

        return h;
    }

}
