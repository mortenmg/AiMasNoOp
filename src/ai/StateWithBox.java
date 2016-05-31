package ai;

import java.awt.*;

/**
 * Created by hvingelby on 5/31/16.
 */
public class StateWithBox extends TestState {
    Point boxPosition;

    public StateWithBox(TestState parent) {
        super(parent);
    }

    @Override
    public boolean isCellFree(int row, int col) {
        // TODO: Do a single lookup instead of looping
        for (Box b : Supervisor.getInstance().getLevel().getBoxes().values()) {
            if (b.location.x == col && b.location.y == row)
                return false;
        }

        //for (MAgent a : Supervisor.getInstance().getAgents()) {
        //    if (a.getPosition().x == col && a.getPosition().y == row)
        //        return false;
        //}

        return  Supervisor.getInstance().getLevel().getMap()[row][col].getType()!= CellType.WALL;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        StateWithBox other = (StateWithBox) obj;
        if ( !agentPosition.equals(other.getAgentPosition()) )
            return false;
        if ( !boxPosition.equals(other.getBoxPosition()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return agentPosition.hashCode()+boxPosition.hashCode();
    }

    public Point getBoxPosition() {
        return boxPosition;
    }

    public void setBoxPosition(Point boxPosition) {
        this.boxPosition = boxPosition;
    }
}
