package ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


/*      Usage example!
        ai.MapAnalyser ma = new ai.MapAnalyser("SAMASters.lvl");

        ai.Graph g = ma.createGraphFromMap();

        for(Node goal: g.getGoals().values()){
            GraphToolkit.dijkstra(g.getGraphAsList(), goal);
        }

        Stack<Point> path = g.getPathFromPointToGoal(new Point(3,5),"SomeStringIdentifierForGoal");
 */


public class Graph{

    private HashMap<Point, Node> graph;
    private HashMap<String, Node> boxes;
    private HashMap<String, Node> goals;

    Graph(HashMap<Point, Node> graph, HashMap<String, Node> boxes, HashMap<String, Node> goals){
        this.graph = graph;
        this.goals = goals;
        this.boxes = boxes;
    }

    public HashMap<Point,Node> getGraph() {
        return graph;
    }

    public HashMap<String,Node> getGoals() {
        return goals;
    }

    public ArrayList<Node> getGraphAsList(){
        return new ArrayList<>(graph.values());
    }

    public HashMap<String,Node> getBoxes() {
        return boxes;
    }


}
