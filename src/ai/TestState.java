package ai;

import junit.framework.Test;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by hvingelby on 5/27/16.
 */
public abstract class TestState {

    protected TestState parent;
    protected Point agentPosition;
    protected Command action;

    public TestState(TestState parent) {
        this.parent = parent;
    }


    public Point getAgentPosition() {
        return agentPosition;
    }

    public void setAgentPosition(Point agentPosition) {
        this.agentPosition = agentPosition;
    }

    public Command getAction() {
        return action;
    }

    public abstract boolean isCellFree(int row, int col);

    public LinkedList<TestState> extractPlan() {
        LinkedList<TestState> plan = new LinkedList<>();
        TestState n = this;
        while( !n.isInitialState() ) {
            plan.addFirst( n );
            n = n.parent;
        }
        return plan;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    private boolean isInitialState() {
        return this.parent == null;
    }

    public void setAction(Command action) {
        this.action = action;
    }
}
