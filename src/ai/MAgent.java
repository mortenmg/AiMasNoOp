package ai;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by hvingelby on 4/5/16.
 */
public class MAgent extends Agent {
    private final Queue<Message> agentMsgQueue;
    private final Deque<Command> plan;

    private GoalTask currentTask;
    private boolean isWorkingOnPlan;
    private AgentStatus status;

    private int numberOfStepsToTake;

    public boolean commandQueueEmpty() {
        return this.agentMsgQueue.isEmpty();
    }

    private boolean terminateFlag = false;
    private AStarPlanner planner;

    private boolean waitingForCorridor = false;
    private int waitingForCorridorNumber;
    private LinkedList<Command> BackToPlan;
    private boolean moveAway = false;
    private int waitSteps;
    private Point lastPosition;

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

    private void moveFromCorridor(Set<Point> illegalPoints){
        MovePlanner movePlanner = new MovePlanner();

        // Create a move task away from the agents own position.
        MoveTask task = new MoveTask(getPosition(), illegalPoints);

        BackToPlan = new LinkedList<>();

        List<TestState> plan = movePlanner.generatePlan(task);
        numberOfStepsToTake = plan.size();

        synchronized (this.plan) {
            for (TestState s : plan) {
                BackToPlan.add(s.getAction().reverseCommand(s.getAction()));
                this.plan.addFirst(s.getAction());
            }

        }

    }

    public boolean hasCorridorOpened(){
        if(!Supervisor.getInstance().getLevel().isCorridorLocked(waitingForCorridorNumber)){
            waitingForCorridor = false;
            return true;
        }else{
            return false;
        }

    }

    public void setLastPosition(Point position){
        lastPosition = position;
    }

    public Point getLastPosition(){
        return lastPosition;
    }

    public int getWaitNumberOfSteps(){
        return waitSteps;
    }

    public void setWaitSteps(int waitnumber){
        this.waitSteps = waitnumber;
    }

    public void madeWaitStep(){
        waitSteps--;
    }


    public void WaitForCorridor(int corNumber){
        waitingForCorridor = true;
        waitingForCorridorNumber = corNumber;
    }

    public synchronized boolean isWaitingForCorridor(){
        return waitingForCorridor;

        /*
            if(s.getLevel().isCorridorLocked(waitingForCorridorNumber)){
            System.err.println("[MAgent] Corridor is locked! ---");
            return waitingForCorridor;
        }else{
            System.err.println("[MAgent] Corridor is open! ---");
            //goToCorridor();
            waitingForCorridor = false;
            return waitingForCorridor;
        }
        * */
    }

    public void moveAwayFromCorridor(){
        moveAway = true;
    }

    public void movedFromCorridor(){
        moveAway = false;
    }

    public boolean moveFromCorridor(){
        return moveAway;
    }



    public void getBackToPlanPosition(){
        synchronized (this.plan){
            for(Command c: BackToPlan){
                this.plan.addFirst(c);
            }
        }
    }

    public void goToCorridor(){
        waitingForCorridor = false;
        // Get back to the corridor
        getBackToPlanPosition();
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
                System.err.println(this + " I received a helper task!");
                generateTaskPlan((HelperTask) msg.getPayload());
                break;
            case Terminate:
                this.terminateFlag = true;
                break;
            case Replan:
                if (!replanTaskPlan(currentTask)) {
                    System.err.println(this + " I could not replan :-( So I will ask my friends to help me!");
                    status = AgentStatus.WaitingForHelp;

                    Point p = Supervisor.getInstance().getLevel().conflictingCellFromMove(this.peekTopCommand(), this);
                    Box boxToMove = Supervisor.getInstance().getLevel().getBoxAtPosition(p);
                    HelperTask task = new HelperTask(getRestOfPlan(), boxToMove.color, boxToMove.id);

                    Set<Point> illegalpositions = new HashSet<Point>();
                    illegalpositions.add(this.getPosition());
                    moveFromCorridor(illegalpositions);

                    Message helpMsg = new Message(MessageType.NeedHelp, task);
                    helpMsg.setSender(getAgentId());
                    Supervisor.getInstance().postMessageToSupervisor(helpMsg);
                }
                break;
            case MoveToASafePlace:
                System.err.println(this + " I was asked to move to a safe place.");
                isWorkingOnPlan = true;
                generateMovePlan((Set<Point>) msg.getPayload());
                break;
            case MoveFromCorridor:
                System.err.println(this + "I was asked to move away from corridor");
                isWorkingOnPlan = true;
                moveFromCorridor((Set<Point>) msg.getPayload());
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

    private void generateTaskPlan(HelperTask task) {
        isWorkingOnPlan = true;

        this.status = AgentStatus.WorkingOnHelperTask;
        System.err.println(this + " I am planning a helper task");

        HelpPlanner planner = new HelpPlanner();

        List<TestState> plan = planner.generatePlan(task);

        // Keep trying ;)
        if(plan == null) {
            generateTaskPlan(task);
        } else {
            synchronized (this.plan) {
                for (TestState s : plan) {
                    this.plan.add(s.getAction());
                }
            }

            isWorkingOnPlan = false;
        }
    }

    private boolean replanTaskPlan(GoalTask task) {
        isWorkingOnPlan = true;
        System.err.print(this);
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

    private void generateMovePlan(Set<Point> illegalPoints){
        MovePlanner movePlanner = new MovePlanner();

        // Create a move task away from the agents own position.
        MoveTask task = new MoveTask(getPosition(), illegalPoints);

        synchronized (this.plan) {
            for (TestState s : movePlanner.generatePlan(task)) {
                this.plan.add(s.getAction());
            }
        }
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

    public AgentStatus getStatus() {
        return status;
    }
    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public int getSteps() {
        return numberOfStepsToTake;
    }

    public void decrementSteps() {
        numberOfStepsToTake--;
    }


}

enum AgentStatus{
    Planning,
    Working,
    WaitingForHelp,
    WorkingOnHelperTask

}