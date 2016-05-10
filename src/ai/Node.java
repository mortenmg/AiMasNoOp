package ai;

import java.awt.*;
import java.util.LinkedList;

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

public class Node implements Cloneable {

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }


    private String id;
    private Point coord;
    private NodeType type;
    private LinkedList<Edge> neighboors;
    private boolean marked;
    private int cost;

    private Node previous;

    public Node(Point coord, NodeType type){
        this.coord = coord;
        this.type = type;
        this.marked = false;
        this.neighboors = new LinkedList<>();
        this.previous = null;
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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Node getPrevious() {
        return previous;
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



