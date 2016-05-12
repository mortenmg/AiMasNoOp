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
    private HashMap<Point,Goal> goals = new HashMap<>();
    private HashMap<Integer,Box> boxes = new HashMap<>();

    private HashMap<Character, ArrayList<Node>> graphsForGoals = new HashMap<>();

    Level(Cell[][] map){
        this.map = map;
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
    }

    public void setGoals(HashMap<Point,Goal> goals) { this.goals = goals; }
    public void setBoxes(HashMap<Integer,Box> boxes) { this.boxes = boxes; }

    public HashMap<Integer, Box> getBoxes() {
        return boxes;
    }

    public Box getBoxWithId(Integer i){
        return this.boxes.get(i);
    }

    public void setGraphsForGoals(HashMap<Character, ArrayList<Node>> graphsForGoals) {
        this.graphsForGoals = graphsForGoals;
    }

}
