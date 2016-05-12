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

    /**
     * simple goal constructor
     * @param id Id of task!
     */
    GoalTask(int id){
        this.taskId = id;
    }

    GoalTask(int boxPosition, int goalPosition, String colorOfTask){
        this.boxId = boxPosition;
        this.goalId = goalPosition;
        this.color = colorOfTask;
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
