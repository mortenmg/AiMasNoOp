package ai;

import java.awt.*;
import java.util.Set;

/**
 * Created by hvingelby on 5/17/16.
 */
public class MoveTask implements Task{
    private Set<Point> illegalPositions;
    private Point startPosition;

    public MoveTask(Point startPosition, Set<Point> illegalPositions) {
        this.illegalPositions = illegalPositions;
        this.startPosition = startPosition;
    }

    public boolean isIllegal(Point p) {
        return illegalPositions.contains(p);
    }

    @Override
    public Point getStartPosition() {
        return startPosition;
    }

    @Override
    public int getGoalId() {
        return 0;
    }

    @Override
    public int getBoxId() {
        return 0;
    }
}
