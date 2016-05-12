package ai;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Mathias on 05-05-2016.
 */


public class GraphToolkit {


    public static void dijkstra(ArrayList<Node> graph, Node startNode){
        Node n;

        ArrayDeque<Node> queue = new ArrayDeque<>();

        for(Node node: graph){
            node.setCost(Integer.MAX_VALUE);
            node.setPrevious(null);
        }

        startNode.setCost(0);
        queue.push(startNode);

        while(!queue.isEmpty()){
            n = queue.poll();

            for(Edge neighbor: n.getNeighboors()){
                if(neighbor.getTo().getCost() > n.getCost() + neighbor.getWeight()){
                    neighbor.getTo().setCost(n.getCost() + neighbor.getWeight());
                    queue.push(neighbor.getTo());
                    neighbor.getTo().setPrevious(n);
                }
            }
        }
        updateNodePaths(graph,startNode.getId());
    }

    private static void updateNodePaths(ArrayList<Node> graph, int goalId){

        for(Node n: graph) {
            Node pathNode = n;
            if (pathNode.getType() != CellType.WALL) { //Only save paths from !Wall nodes
                n.addGoalPath(pathNode.getCost(), goalId);
            }
        }
    }

}
