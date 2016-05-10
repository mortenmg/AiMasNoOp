import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Mathias on 05-05-2016.
 */

import java.awt.*;
import java.util.*;


import java.awt.*;
import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class GraphToolkit {


    public static void dijkstra(ArrayList<Node> graph, Node startNode){
        Node n = null;

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
        updateNodePaths(graph,startNode);
    }

    private static void updateNodePaths(ArrayList<Node> graph, Node goal){

        for(Node n: graph){
            Node pathNode = n;
            HashMap<String,Stack<Point>> goalPaths = new HashMap<>();
            Stack<Point> path = new Stack<>();
            while(pathNode != null){
                path.add(pathNode.getCoord());

                pathNode = pathNode.getPrevious();
            }
            n.addGoalPath(path, goal.getId());
        }
    }

}
