package ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Mathias on 24-05-2016.
 */
public class SAHeuristic implements Comparator<SAState> {

    private ArrayList<GoalTask> sortedGoalTask;
    private float goalPriorityWeight = 0.5f;
    private int agentApprochBonus = 0;


    public SAHeuristic(){    }

    @Override
    public int compare(SAState n1, SAState n2) {
        if(f( n1 ) > f( n2 ))
            return 1;
        else if(f( n1 ) < f( n2 ))
            return -1;
        else
        return 0;
    }

    private float f(SAState n){
        return h( n );
    }

    private float h(SAState n ) {

        if(sortedGoalTask == null){
            sortedGoalTask = new ArrayList<>();
            PriorityQueue<GoalTask> goalTasks = Supervisor.getInstance().getGoalTasks();
            while(!goalTasks.isEmpty()){
                sortedGoalTask.add(goalTasks.poll());
            }
            goalTasks.addAll(sortedGoalTask);
        }

        float h = 0;
        PriorityQueue<GoalTask> goalTasks = Supervisor.getInstance().getGoalTasks();

        float goalTaskIterWeight = 1;
        boolean agentApproachingBoxFlag = false;


        for(GoalTask gt: sortedGoalTask) {
            //Point goalPos = Supervisor.getInstance().getLevel().getGoals().get(gt.getGoalId()).point;
            Point boxPos = n.getBoxWithIdPos(gt.getBoxId());
            h += (Supervisor.getInstance().getLevel().getCostForCoordinateWithGoal(boxPos.x,boxPos.y, gt.getGoalId()) * goalTaskIterWeight);
            //h += SimpleHeuristic.euclidean(goalPos.x, goalPos.y, boxPos.x, boxPos.y);
            if(n.parent != null) {
                if (SimpleHeuristic.euclidean(n.agentCol, n.agentRow, boxPos.x, boxPos.y) < SimpleHeuristic.euclidean(n.parent.agentCol, n.parent.agentRow, boxPos.x, boxPos.y)){
                    agentApproachingBoxFlag = true;
                }
            }
            goalTaskIterWeight += goalPriorityWeight;
        }

        if(agentApproachingBoxFlag)
            h -= agentApprochBonus;
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
