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
    private MoveTask task;

    public MovePlanner(int agentId) {
        this.agentId = agentId;
    };

    @Override
    public LinkedList<State> generatePlan(State initialState, Task task) {
        return null;
    }

    public LinkedList<Command> generatePlan(MoveTask task) {

        // BFS uses Queue data structure
        Queue queue = new LinkedList();

        this.task = task;

        SimpleNode start = new SimpleNode(null);
        start.p = task.getStartPosition();
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

        if(this.task.isIllegal(new Point(child.p))) {
            return false;
        }

        return  Supervisor.getInstance().getLevel().getMap()[child.p.y][child.p.x].getType()==CellType.EMPTY;


    }

    private SimpleNode getUnvisitedChildNode(SimpleNode n) {
        for ( Command c : Command.every ) {
            // Determine applicability of action
            int newAgentRow = n.p.y + Command.dirToRowChange(c.dir1);
            int newAgentCol = n.p.x + Command.dirToColChange(c.dir1);
            if (c.actType == Command.type.Move) {
                // Check if there's a wall or box on the ai.Cell to which the agent is moving
                if (cellIsFree(newAgentRow, newAgentCol)) {
                    SimpleNode child = new SimpleNode(n);
                    child.action = c;
                    child.p = new Point(newAgentCol,newAgentRow);
                    if (!explored.contains(child))
                        return child;
                }
            }
        }
        return null;
    }

    private boolean cellIsFree(int row, int col) {
        // TODO: Do a single lookup instead of looping
        for (Box b : Supervisor.getInstance().getLevel().getBoxes().values()) {
            if (b.location.x == col && b.location.y == row)
                return false;
        }

        return  Supervisor.getInstance().getLevel().getMap()[row][col].getType()!=CellType.WALL;

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

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        SimpleNode other = (SimpleNode) obj;
        if ( !other.p.equals(this.p) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return p.hashCode();
    }
}