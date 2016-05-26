package ai;

import java.awt.*;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Mathias on 24-05-2016.
 */
public class SAHeuristic implements Comparator<SAState> {

    public SAHeuristic(){}

    @Override
    public int compare(SAState n1, SAState n2) {
        return f( n1 ) - f( n2 );
    }

    private int f(SAState n){
        return h( n );
    }

    private int h(SAState n ) {

        int h = 0;
        PriorityQueue<GoalTask> goalTasks = Supervisor.getInstance().getGoalTasks();

        for(GoalTask gt: goalTasks) {

            //Point goalPos = Supervisor.getInstance().getLevel().getGoals().get(gt.getGoalId()).point;
            Point boxPos = n.getBoxWithIdPos(gt.getBoxId());
            h += Supervisor.getInstance().getLevel().getCostForCoordinateWithGoal(boxPos.x,boxPos.y, gt.getGoalId());
            //h += SimpleHeuristic.euclidean(goalPos.x, goalPos.y, boxPos.x, boxPos.y);
        }
        return h;
    }
            /*
            for (Box b1: n.getBoxes().values()){
                if(b1.letter == 'B')
                    System.err.println("BoxId:" + b1.id);
            }

            h += euclidean(g.point.x,g.point.y, b.location.x, b.location.y);
            //h += euclidean(n.agentCol,n.agentRow, b.location.x, b.location.y);
        }

        return h;
    }
        /*


        for(GoalTask gt: goalTasks){
            Goal g = Supervisor.getInstance().getLevel().getGoalWithId(gt.getGoalId());
            Box b = n.getBoxWithId(gt.getBoxId());
            /*
            for (Box b1: n.getBoxes().values()){
                if(b1.letter == 'B')
                    System.err.println("BoxId:" + b1.id);
            }

            h += euclidean(g.point.x,g.point.y, b.location.x, b.location.y);
            //h += euclidean(n.agentCol,n.agentRow, b.location.x, b.location.y);
        }

        //System.err.println(h);
        return h;
    */
}
