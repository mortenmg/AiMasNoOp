package ai;

import java.awt.*;
import java.util.Set;

/**
 * Created by hvingelby on 5/17/16.
 */
public class MoveTask {
    private Set<Point> illegalPositions;
    private Point startPosition;

    public MoveTask(Point startPosition, Set<Point> illegalPositions) {
        this.illegalPositions = illegalPositions;
        this.startPosition = startPosition;
    }

    public boolean isIllegal(Point p) {
        return illegalPositions.contains(p);
    }

    public Point getStartPosition() {
        return startPosition;
    }
}
