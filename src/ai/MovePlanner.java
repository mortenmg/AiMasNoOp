package ai;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by hvingelby on 5/17/16.
 */
public class MovePlanner implements Planner{
    private int agentId;
    private HashSet<SimpleNode> explored;

    public MovePlanner(int agentId) {
        this.agentId = agentId;
    };

    @Override
    public LinkedList<State> generatePlan(State initialState, Task task) {
        return null;
    }

    public LinkedList<Command> generatePlan(State initialState, MoveTask task) {

        // BFS uses Queue data structure
        Queue queue = new LinkedList();


        SimpleNode start = new SimpleNode(null);
        start.p = task.getIllegalPosition();
        queue.add(start);

        explored = new HashSet<>();
        explored.add(start);

        while(!queue.isEmpty()) {
            SimpleNode n = (SimpleNode) queue.remove();
            SimpleNode child = null;
            while((child=getUnvisitedChildNode(n))!=null) {
                if (isGoal(child)){
                    return child.extractPlan();
                }
                explored.add(child);
                queue.add(child);
            }
        }
        return null;
    }

    private boolean isGoal(SimpleNode child) {
        for (Box b : Supervisor.getInstance().getLevel().getBoxes().values()) {
            if (b.location.x == child.p.x && b.location.y == child.p.y)
                return false;
        }
        for (Agent b : Supervisor.getInstance().getAgents()) {
            if (b.getPosition().x == child.p.x && b.getPosition().y == child.p.y)
                return false;
        }
        return  Supervisor.getInstance().getLevel().getMap()[child.p.y][child.p.x].getType()==CellType.EMPTY;


    }

    private SimpleNode getUnvisitedChildNode(SimpleNode n) {
        for ( Command c : Command.every ) {
            // Determine applicability of action
            int newAgentRow = n.p.y + dirToRowChange(c.dir1);
            int newAgentCol = n.p.x + dirToColChange(c.dir1);
            if (c.actType == Command.type.Move) {
                // Check if there's a wall or box on the ai.Cell to which the agent is moving
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    SimpleNode child = new SimpleNode(n);
                    child.action = c;
                    child.p = new Point(newAgentCol,newAgentRow);
                    return child;
                }
            }
        }
        return null;
    }

    private boolean cellIsFree(int row, int col) {
        for (Box b : Supervisor.getInstance().getLevel().getBoxes().values()) {
            if (b.location.x == col && b.location.y == row)
                return false;
        }
        return  Supervisor.getInstance().getLevel().getMap()[row][col].getType()!=CellType.WALL;

    }



    private int dirToRowChange( Command.dir d ) {
        return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange( Command.dir d ) {
        return ( d == Command.dir.E ? 1 : ( d == Command.dir.W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
    }
}

class SimpleNode {
    SimpleNode parent;
    Point p;
    Command action;

    public SimpleNode(SimpleNode n) {
        parent = n;
    }

    public LinkedList<Command> extractPlan() {
        LinkedList<Command> plan = new LinkedList<>();
        SimpleNode n = this;
        while( n.parent != null ) {
            plan.addFirst( n.action );
            n = n.parent;
        }
        return plan;
    }
}