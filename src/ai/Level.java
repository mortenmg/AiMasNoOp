package ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mathias on 09-05-2016.
 */

public class Level {

    private final Cell[][] map;
    private ArrayList<Agent> agents = new ArrayList<>();
    private HashMap<Point, Goal> goals = new HashMap<>();
    private HashMap<Integer, Box> boxes = new HashMap<>();

    private ArrayList<ArrayList<Node>> graph = new ArrayList<>();

    Level(Cell[][] map) {
        this.map = map;
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
    }


    public HashMap<Integer, Box> getBoxes() {
        return boxes;
    }

    public Box getBoxWithId(Integer i) {
        return this.boxes.get(i);
    }

    public void setGraph(ArrayList<ArrayList<Node>> graph) {
        this.graph = graph;
    }

    public int getCostForCoordinateWithGoal(int x, int y, int goalId){

        if(graph.size() > x){
            if(graph.get(x).size() > y){
                return graph.get(x).get(y).getGoalPathsCost(goalId);
            }
        }
        else return Integer.MAX_VALUE;

    }

}
