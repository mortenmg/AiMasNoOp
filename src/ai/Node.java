package ai;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Mathias on 03-05-2016.
 */

public class Node {

    private int id;
    private Point coord;
    private CellType type;
    private LinkedList<Edge> neighboors;
    private int cost;
    private HashMap<Integer,Integer> goalPathCosts;

    private Node previous;

    public Node(Point coord, CellType type){
        this.coord = coord;
        this.type = type;
        this.neighboors = new LinkedList<>();
        this.previous = null;
        goalPathCosts = new HashMap<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addNeighbor(Edge e){
        this.neighboors.push(e);
    }

    public CellType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void addGoalPath(Integer cost, int goalId){
        this.goalPathCosts.put(goalId,cost);
    }

    public void setPrevious(Node n){
        this.previous = n;
    }

    public int getGoalPathsCost(int goalId){
        //System.err.println(this.getType() +  " node type");
        //System.err.println("nodes number of goal paths:" + this.goalPathCosts.size());

        return goalPathCosts.get(goalId);
    }

    public LinkedList<Edge> getNeighboors() {
        return neighboors;
    }

    public Point getCoord() {
        return coord;
    }

    public String toString(){
        return "[Coordinate - x,y] = " + this.coord.getX() + "," + this.coord.getY() + " | " + "[Cost] = " + getCost();
    }
}



