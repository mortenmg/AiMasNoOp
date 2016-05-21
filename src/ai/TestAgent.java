package ai;

import java.awt.*;

/**
 * Created by Mathias on 18-05-2016.
 */
public class TestAgent extends Thread {

    private int id;
    public int getAgentId() { return id; }
    public void setAgentId(int id){ this.id = id; }

    private String color;
    public String getColor() {
        return color;
    }
    public void setColor(String color){this.color = color;}

    private Supervisor s;
    public void addSupervisor(Supervisor supervisor) {
        s = supervisor;
    }
    public Supervisor getSupervisor(){return this.s;}

    private Point position;
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) {
        this.position = position;
    }


    private Planner planner;
    public Planner getPlanner() {
        return planner;
    }

    public void setPlanner(Planner planner) {
        this.planner = planner;
    }

    public TestAgent(int id, String color, Point position){
        this.id = id;
        this.position = position;
        this.color = color;
    }

    //Extend this agent to and implement run method for this specific agent.
    @Override
    public void run() {
        super.run();
    }


    @Override
    public String toString() {
        return "[Agent "+id+"]";
    }

}
