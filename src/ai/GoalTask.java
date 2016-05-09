package ai;

import java.awt.*;

/**
 * Created by hvingelby on 4/5/16.
 */
public class GoalTask implements Task {
    private int taskId;
    private Point boxPosition;
    private Point goalPosition;
    private String color;

    /**
     * simple goal constructor
     * @param id Id of task!
     */
    GoalTask(int id){
        this.taskId = id;
    }

    GoalTask(Point boxPosition, Point goalPosition, String colorOfTask){
        this.boxPosition = boxPosition;
        this.goalPosition = goalPosition;
        this.color = colorOfTask;
    }

    public int getTaskId() {
        return taskId;
    }

    @Override
    public Point getGoalPoint() {
        return goalPosition;
    }

    @Override
    public String getBoxId() {
        return null;
    }
}
