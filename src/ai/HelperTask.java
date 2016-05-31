package ai;

import java.awt.*;
import java.util.Set;

/**
 * Created by hvingelby on 4/5/16.
 */
public class HelperTask implements Task {
    private int boxId;
    private String color;
    private int agentId;
    private Set<Point> illegalPositions;

    public HelperTask(Set<Point> illegalPositions, String color, int boxId){
        this.illegalPositions = illegalPositions;
        this.color = color;
        this.boxId = boxId;
    }

    @Override
    public int getGoalId() {
        return 0;
    }

    @Override
    public int getBoxId() {
        return boxId;
    }

    @Override
    public Point getStartPosition() {
        return null;
    }

    public String getColor() {
        return color;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public boolean isIllegal(Point p) {
        return illegalPositions.contains(p);
    }

    public int getAgentId() {
        return agentId;
    }
}
