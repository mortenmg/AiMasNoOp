package ai;

import java.awt.*;

/**
 * Created by hvingelby on 4/5/16.
 */
public class GoalTask implements Task {
    private int taskId;
    private int boxId;
    private int goalId;
    private String color;

    GoalTask(int boxId, int goalId, int taskId){
        this.boxId = boxId;
        this.goalId = goalId;
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }


    @Override
    public int getGoalId() {
        return this.goalId;
    }

    @Override
    public int getBoxId() {
        return boxId;
    }
}
