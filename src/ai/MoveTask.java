package ai;

import java.awt.*;

/**
 * Created by hvingelby on 5/17/16.
 */
public class MoveTask {
    private Point illegalPosition;

    public MoveTask(Point illegalPosition) {
        this.illegalPosition = illegalPosition;
    }

    public Point getIllegalPosition() {
        return illegalPosition;
    }
}
