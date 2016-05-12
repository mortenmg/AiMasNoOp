package ai;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Mathias on 03-05-2016.
 */

enum NodeType{
    EMPTY,
    WALL,
    BOX,
    GOAL,
    AGENT,
}

public class Node {

    private String id;
    private Point coord;
    private NodeType type;
    private LinkedList<Edge> neighboors;
    private int cost;
    private HashMap<String,Stack<Point>> goalPaths;

    private Node previous;

    public Node(Point coord, NodeType type){
        this.coord = coord;
        this.type = type;
        this.neighboors = new LinkedList<>();
        this.previous = null;
        goalPaths = new HashMap<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addNeighbor(Edge e){
        this.neighboors.push(e);
    }

    public NodeType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Node getPrevious() {
        return previous;
    }

    public void addGoalPath(Stack<Point> path, String goalId){
        this.goalPaths.put(goalId,path);
    }

    public Stack<Point> getPathForGoal(String goalId){
        return this.goalPaths.get(goalId);
    }

    public void setPrevious(Node n){
        this.previous = n;
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



