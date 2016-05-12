package ai;

import java.awt.*;

/**
 * Created by hvingelby on 4/5/16.
 */
public class HelperTask implements Task {

    @Override
    public int getGoalId() {
        return 0;
    }

    @Override
    public int getBoxId() {
        return 0;
    }
    //Box position as ai.point
    //Color of agent required to complete the task
    //List of points where the box should NOT be to clear the path for the agent offering this task


}
