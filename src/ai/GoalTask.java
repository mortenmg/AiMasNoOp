package ai;

import java.awt.*;

/**
 * Created by hvingelby on 4/5/16.
 */
public class GoalTask implements Task, Comparable<GoalTask> {
    private int taskId;
    private int boxId;
    private int goalId;
    private int weight;
    private String color;

    GoalTask(int boxId, int goalId, int taskId){
        this.boxId = boxId;
        this.goalId = goalId;
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getGoalId() {
        return this.goalId;
    }

    public int getBoxId() {
        return boxId;
    }

    public int getWeight(){return weight; }

    @Override
    public int compareTo(GoalTask o) {
        if (o.weight > this.weight){
            return -1;
        }
        else if (o.weight < this.weight){
            return 1;
        }
        return 0;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
