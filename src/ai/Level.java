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
    private HashMap<Integer,Goal> goals = new HashMap<>();
    private HashMap<Integer,Box> boxes = new HashMap<>();

    private ArrayList<ArrayList<Node>> graph = new ArrayList<>();

    Level(Cell[][] map) {
        this.map = map;
    }

    public boolean isCellFreeInDirection(int x, int y, Command.dir dir){
        switch (dir){//TODO care for index out of bounds!
            case E:
                return map[x][y+1].isFree();
            case W:
                return map[x][y-1].isFree();
            case N:
                return map[x+1][y].isFree();
            case S:
                return map[x-1][y].isFree();
        }

        return false;
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
    }

    public void setGoals(HashMap<Integer,Goal> goals) { this.goals = goals; }
    public void setBoxes(HashMap<Integer,Box> boxes) { this.boxes = boxes; }

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
        System.err.println("Graph size = " + graph.size() + ", " + graph.get(0).size());
        if(graph.size() > x){
            if(graph.get(x).size() > y){
                return graph.get(x).get(y).getGoalPathsCost(goalId);
            }
        }
        return Integer.MAX_VALUE;

    }

}
