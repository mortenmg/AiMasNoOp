package ai;

import java.awt.*;
import java.util.*;

import ai.State;

/**
 * Created by hvingelby on 4/5/16.
 */
public class MAgent extends Agent {
    private final Queue<Message> agentMsgQueue;
    private final Deque<Command> plan;

    private Supervisor s;

    private GoalTask currentTask;
    private boolean isWorkingOnPlan;

    public boolean commandQueueEmpty() {
        return this.agentMsgQueue.isEmpty();
    }

    private boolean terminateFlag = false;
    private AStarPlanner planner;

    public MAgent(int id, String color, Point position) {
        super(id,color,position);
        this.planner = new AStarPlanner(getAgentId());
        this.agentMsgQueue = new LinkedList<>();
        this.plan = new LinkedList<>();
    }

    public MAgent(Agent a) {
        super(a.getAgentId(), a.getColor(), new Point(a.getPosition().x, a.getPosition().y));
        this.planner = new AStarPlanner(getAgentId());
        this.agentMsgQueue = new LinkedList<>();
        this.plan = new LinkedList<>();
    }

    private void moveToSafePlace(Set<Point> illegalPoints){
        MovePlanner movePlanner = new MovePlanner(getAgentId());

        // Create a move task away from the agents own position.
        MoveTask task = new MoveTask(getPosition(), illegalPoints);

        synchronized (this.plan) {
            for (Command c : movePlanner.generatePlan(task)) {
                this.plan.add(c);
            }
        }
    }

    private void addPlan(LinkedList<ai.State> plan) {
        synchronized (this.plan) {
            for (ai.State n : plan) {
                this.plan.add(n.getAction());
            }
        }
    }

    public Command pollCommand() {
        synchronized (this.plan) {
            return this.plan.poll();
        }
    }

    public Command peekTopCommand() {
        synchronized (this.plan) {
            return this.plan.peek();
        }
    }

    @Override
    public void run() {
        System.err.println(this+" Hello!");

        while (!terminateFlag) {
            if (currentTask != null && agentMsgQueue.isEmpty()) {

            }
            handleMessage(getMessage());
        }
        System.err.println(this + " Goodbye!");

    }

    private void printPlan(LinkedList<ai.State> states) {
        // Just printing the plans actions
        System.err.print(this + " My plan: ");
        for (ai.State state : states) {
            System.err.print(state.action+" ");
        }
        System.err.println();
    }

    public void setCurrentTask(GoalTask currentTask) {
        if(currentTask == null){
            isWorkingOnPlan = false;
        }else{
            isWorkingOnPlan = true;
        }
        this.currentTask = currentTask;
    }


    /**
     * Handle message from queue
     * @param msg
     */
    private void handleMessage(Message msg){
        switch (msg.getType()){
            case Task:
                this.currentTask = (GoalTask) msg.getPayload();
                generateTaskPlan((GoalTask) msg.getPayload());
                break;
            case Help:
                //Calc "bid" for help
                //Return bid
                break;
            case Terminate:
                this.terminateFlag = true;
                break;
            case Replan:
                System.err.println(this + " I was asked to empty my action queue.");
                if (!replanTaskPlan(currentTask)) {
                    System.err.println(this + " I could not replan :-( So I will ask my friends to help me!");
                }
                break;
            case MoveToASafePlace:
                System.err.println(this + " I was asked to move to a safe place.");
                isWorkingOnPlan = true;
                moveToSafePlace((Set<Point>) msg.getPayload());
                break;
            default:
                break;

        }
    }

    private void generateTaskPlan(GoalTask task) {
        isWorkingOnPlan = true;
        System.err.println(this + " I am planning task #"+task.getTaskId());
        ai.State s = new ai.State(null);
        LinkedList<ai.State> states = planner.generatePlan(s,currentTask);
        if (states == null) {
            System.err.println(this + " I could not find a plan, so i will try finding a relaxed plan.");
            states = planner.generatePlan(s,currentTask, true);
        }
        printPlan(states);

        addPlan(states);
        isWorkingOnPlan = false;
    }

    private boolean replanTaskPlan(GoalTask task) {
        isWorkingOnPlan = true;

        System.err.println(this + " I am replanning task #"+task.getTaskId());
        ai.State s = new ai.State(null);
        LinkedList<ai.State> states = planner.generatePlan(s,currentTask);
        if (states != null) {
            this.plan.clear();
            printPlan(states);
            addPlan(states);
            return true;
        }
        return false;
    }

    public void addSupervisor(Supervisor supervisor) {
        s = supervisor;
    }

    public synchronized void postMsg(Message msg){
        agentMsgQueue.add(msg);
        notify();
    }

    private synchronized Message getMessage(){
        while(agentMsgQueue.size() == 0){
            try {
                wait();
            }catch (Exception e){

            }
        }
        notify();
        return agentMsgQueue.poll();
    }


    public GoalTask getCurrentTask() {
        return currentTask;
    }

    public boolean isWorkingOnPlan() {
        return isWorkingOnPlan;
    }

    public Set<Point> getRestOfPlan() {
        Set<Point> pointsInPlan = new HashSet<>();
        Point agentPos = new Point(this.getPosition());
        for (Command c : plan){
            pointsInPlan.add(getNewAgentPosition(agentPos,c));
            if ((getNewBoxPosition(agentPos,c))!= null)
                pointsInPlan.add(getNewBoxPosition(agentPos,c));
            agentPos = getNewAgentPosition(agentPos,c);
        }
        return pointsInPlan;
    }

    private Point getNewAgentPosition(Point agentPos, Command c){
        int newAgentRow = agentPos.y + Command.dirToRowChange(c.dir1);
        int newAgentCol = agentPos.x + Command.dirToColChange(c.dir1);
        return new Point(newAgentCol, newAgentRow);
    }

    private Point getNewBoxPosition(Point agentPos, Command c) {
        if (!(c.actType == Command.type.Move)){
            int boxRow = agentPos.y + Command.dirToRowChange(c.dir2);
            int boxCol = agentPos.x + Command.dirToColChange(c.dir2);
            return new Point(boxCol, boxRow);
        }
        return null;
    }


    /**
     * Method for getting a string representation
     * of an agent.
     * @return
     */
    @Override
    public String toString() {
        return "[Agent "+getAgentId()+"]";
    }

}
