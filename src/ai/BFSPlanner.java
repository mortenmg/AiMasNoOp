package ai;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by hvingelby on 5/31/16.
 */
public abstract class BFSPlanner {
    protected HashSet<TestState> explored;
    protected Task task;

    public LinkedList<TestState> generatePlan(Task task) {
        Queue<TestState> frontier = new LinkedList<>();
        this.task = task;

        TestState start = generateStartState(task);
        frontier.add(start);

        explored = new HashSet<>();
        explored.add(start);

        while(!frontier.isEmpty()) {
            TestState n = frontier.remove();
            TestState child = null;
            while((child=getUnvisitedChildNode(n))!=null) {
                if (isGoal(child)){
                    return child.extractPlan();
                }
                explored.add(child);
                frontier.add(child);
            }
            System.err.println("Explored states: "+explored.size());
        }
        return null;
    }

    protected abstract TestState generateStartState(Task task);
    protected abstract TestState getUnvisitedChildNode(TestState s);
    protected abstract boolean isGoal(TestState s);

}
