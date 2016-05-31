package ai;

import java.awt.*;

/**
 * Created by hvingelby on 5/31/16.
 */
public class HelpPlanner extends BFSPlanner{

    @Override
    protected boolean isGoal(TestState child) {
        // TODO: It is a goal if the box is moved to a safe location
        StateWithBox c = (StateWithBox) child;
        for (Box b : Supervisor.getInstance().getLevel().getBoxes().values()) {
            if (b.location.equals(c.getAgentPosition()) || b.location.equals(c.getBoxPosition()))
                return false;
        }
        for (MAgent a : Supervisor.getInstance().getAgents()) {
            if (a.getPosition().equals(c.getAgentPosition()) || a.getPosition().equals(c.getBoxPosition()))
                return false;
        }
        HelperTask t = (HelperTask) task;
        if(t.isIllegal(c.getAgentPosition()) || t.isIllegal(c.getBoxPosition())) {
            return false;
        }
        boolean agentPosFree = Supervisor.getInstance().getLevel().getMap()[c.getAgentPosition().y][c.getAgentPosition().x].getType()==CellType.EMPTY;
        boolean boxPosFree = Supervisor.getInstance().getLevel().getMap()[c.getBoxPosition().y][c.getBoxPosition().x].getType()==CellType.EMPTY;
        return  agentPosFree && boxPosFree;
    }

    @Override
    protected TestState generateStartState(Task task) {
        HelperTask t = (HelperTask) task;
        StateWithBox start = new StateWithBox(null);
        start.setAgentPosition(new Point(Supervisor.getInstance().getAgentWithId(t.getAgentId()).getPosition()));
        start.setBoxPosition(new Point(Supervisor.getInstance().getLevel().getBoxWithId(t.getBoxId()).location));
        return start;
    }

    @Override
    protected TestState getUnvisitedChildNode(TestState n) {
        StateWithBox s = (StateWithBox) n;
        for ( Command c : Command.every ) {
            int newAgentRow = n.getAgentPosition().y + Command.dirToRowChange(c.dir1);
            int newAgentCol = n.getAgentPosition().x + Command.dirToColChange(c.dir1);
            if (c.actType == Command.type.Move) {
                // Check if there's a wall or box on the ai.Cell to which the agent is moving
                if (n.isCellFree(newAgentRow, newAgentCol)) {
                    StateWithBox child = new StateWithBox(s);
                    child.setAction(c);
                    child.setAgentPosition(new Point(newAgentCol,newAgentRow));
                    child.setBoxPosition(new Point(s.getBoxPosition()));
                    if (!explored.contains(child))
                        return child;
                }
            } else if ( c.actType == Command.type.Push ) {
                // Make sure that there's actually a box to move
                if ( s.getBoxPosition().equals(new Point(newAgentCol, newAgentRow)) ) {
                    int newBoxRow = newAgentRow + Command.dirToRowChange( c.dir2 );
                    int newBoxCol = newAgentCol + Command.dirToColChange( c.dir2 );

                    if ( n.isCellFree( newBoxRow, newBoxCol ) ) {
                        StateWithBox child = new StateWithBox(s);
                        child.setAction(c);
                        child.setAgentPosition(new Point(newAgentCol,newAgentRow));
                        child.setBoxPosition(new Point(newBoxCol, newBoxRow));
                        if (!explored.contains(child))
                            return child;
                    }
                }
            } else if ( c.actType == Command.type.Pull ) {
                // ai.Cell is free where agent is going
                if ( s.isCellFree( newAgentRow, newAgentCol ) ) {
                    int boxRow = s.getAgentPosition().y + Command.dirToRowChange( c.dir2 );
                    int boxCol = s.getAgentPosition().x + Command.dirToColChange( c.dir2 );
                    // .. and there's a box in "dir2" of the agent
                    if ( s.getBoxPosition().equals(new Point(boxCol, boxCol)) ) {
                        StateWithBox child = new StateWithBox(s);
                        child.setAction(c);
                        child.setAgentPosition(new Point(newAgentCol,newAgentRow));
                        child.setBoxPosition(new Point(boxCol, boxRow));
                        if (!explored.contains(child))
                            return child;
                    }
                }
            }
        }
        return null;
    }
}
