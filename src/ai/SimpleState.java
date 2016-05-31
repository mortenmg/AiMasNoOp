package ai;

/**
 * Created by hvingelby on 5/27/16.
 */
class SimpleState extends TestState {

    public SimpleState(TestState parent) {
        super(parent);
    }

    @Override
    public boolean isCellFree(int row, int col) {
        // TODO: Do a single lookup instead of looping
        for (Box b : Supervisor.getInstance().getLevel().getBoxes().values()) {
            if (b.location.x == col && b.location.y == row)
                return false;
        }

        for (MAgent a : Supervisor.getInstance().getAgents()) {
            if (a.getPosition().x == col && a.getPosition().y == row)
                return false;
        }

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
        SimpleState other = (SimpleState) obj;
        if ( !agentPosition.equals(other.getAgentPosition()) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return agentPosition.hashCode();
    }
}
