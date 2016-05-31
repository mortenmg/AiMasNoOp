package ai;

import java.awt.*;

/**
 * Created by hvingelby on 5/17/16.
 */
public class MovePlanner extends BFSPlanner{

    @Override
    protected TestState generateStartState(Task task) {
        SimpleState start = new SimpleState(null);
        start.setAgentPosition(task.getStartPosition());
        return start;
    }

    @Override
    protected boolean isGoal(TestState child) {
        for (Box b : Supervisor.getInstance().getLevel().getBoxes().values()) {
            if (b.location.x == child.getAgentPosition().x && b.location.y == child.getAgentPosition().y)
                return false;
        }
        for (MAgent b : Supervisor.getInstance().getAgents()) {
            if (b.getPosition().x == child.getAgentPosition().x && b.getPosition().y == child.getAgentPosition().y)
                return false;
        }
        MoveTask t = (MoveTask) task;
        if(t.isIllegal(new Point(child.getAgentPosition()))) {
            return false;
        }

        return  Supervisor.getInstance().getLevel().getMap()[child.getAgentPosition().y][child.getAgentPosition().x].getType()==CellType.EMPTY;
    }

    @Override
    protected TestState getUnvisitedChildNode(TestState n) {
        for ( Command c : Command.every ) {
            // Determine applicability of action
            int newAgentRow = n.getAgentPosition().y + Command.dirToRowChange(c.dir1);
            int newAgentCol = n.getAgentPosition().x + Command.dirToColChange(c.dir1);
            if (c.actType == Command.type.Move) {
                // Check if there's a wall or box on the ai.Cell to which the agent is moving
                if (n.isCellFree(newAgentRow, newAgentCol)) {
                    SimpleState child = new SimpleState(n);
                    child.setAction(c);
                    child.setAgentPosition(new Point(newAgentCol,newAgentRow));
                    if (!explored.contains(child))
                        return child;
                }
            }
        }
        return null;
    }
}