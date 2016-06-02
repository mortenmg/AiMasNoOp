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
    private int cost;
    private String color;
    private int agentId;
    private int previousGoal;

    GoalTask(int boxId, int goalId, int taskId, String color){
        this.boxId = boxId;
        this.goalId = goalId;
        this.taskId = taskId;
        this.color = color;
        this.agentId = -1;
        this.previousGoal = -1;
    }

    public String getColor() {
        return color;
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

    @Override
    public Point getStartPosition() {
        return null;
    }

    public int getWeight(){return weight; }

    public int getCost() {return cost;  }

    public int getPreviousGoal() {
        return previousGoal;
    }

    public void setPreviousGoal(int previousGoal) {
        this.previousGoal = previousGoal;
    }

    @Override
    //Prioritizes
    public int compareTo(GoalTask o) {
        if (o.weight > this.weight){
            return 1;
        }
        else if (o.weight < this.weight){
            return -1;
        }
        return 0;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public int getAgentId() {
        return agentId;
    }
}
