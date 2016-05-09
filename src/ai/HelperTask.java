package ai;

/**
 * Created by hvingelby on 4/5/16.
 */
public class HelperTask implements Task {
    @Override
    public String getGoal() {
        return null;
    }

    @Override
    public String getBox() {
        return null;
    }
    //Box position as ai.point
    //Color of agent required to complete the task
    //List of points where the box should NOT be to clear the path for the agent offering this task


}
