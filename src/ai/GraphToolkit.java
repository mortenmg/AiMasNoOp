package ai;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by Mathias on 05-05-2016.
 */
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
    }

}
