package ai;

import java.awt.*;
import java.util.*;

import ai.State;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Agent extends Thread {
    private final Queue<Message> agentMsgQueue;
    private final Deque<Command> plan;
    private int id;
    private String color;
    private Supervisor s;
    private Point position;
    private GoalTask currentTask;
    private boolean isWorkingOnPlan;

    public boolean commandQueueEmpty() {
        return this.agentMsgQueue.isEmpty();
    }

    private boolean terminateFlag = false;
    private Planner planner;

    public Agent(int id, String color, Point position) {
        this.id = id;
        this.position = position;
        this.color = color;
        this.planner = new AStarPlanner(this.id);
        this.agentMsgQueue = new LinkedList<>();
        this.plan = new LinkedList<>();
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }


    private void moveToSafePlace(){
        MovePlanner movePlanner = new MovePlanner(this.id);
        ai.State s = new ai.State(null);

        // Create a move task away from the agents own position.
        MoveTask task = new MoveTask(position);


        synchronized (this.plan) {
            for (Command c : movePlanner.generatePlan(s, task)) {
                this.plan.add(c);
            }
        }

        //addPlan(movePlanner.generatePlan(s, task));
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

    public void forceAddCommand(Command c) {
        System.err.println(this+" waiting");
        synchronized (this.plan) {
            this.plan.addFirst(c);
            System.err.println(this+" forced a null command to the queue: "+this.plan);
        }
        System.err.println(this+" done");
    }


    @Override
    public void run() {
        System.err.println(this+" Hello!");

        while (!terminateFlag) {
            // ai.Agent will calculate a plan
            if (currentTask != null && agentMsgQueue.isEmpty()) {
                isWorkingOnPlan = true;
                System.err.println(this + " I am planning task #"+ currentTask.getTaskId());
                ai.State s = new ai.State(null);
                LinkedList<ai.State> states = planner.generatePlan(s,currentTask);

                // Just printing the plans actions
                System.err.print(this + " My plan: ");
                for (ai.State state : states) {
                    System.err.print(state.action+" ");
                }
                System.err.println();

                addPlan(states);
                //currentTask = null; //TODO: After solution is sent to server the supervisor should mark this current task..
                isWorkingOnPlan = false;
                System.err.println(this + " Done planning task #"+ currentTask.getTaskId()+". The plan size is "+states.size());
            }


            //addPlan(planner.generatePlan(s, new GoalTask(0,0,0)));

            //ai.Agent loop

            handleMessage(getMessage());
        }
        System.err.println(getAgentId() + " terminated");

    }

    public void setCurrentTask(GoalTask currentTask) {
        if(currentTask == null){
            isWorkingOnPlan = false;
        }else{
            isWorkingOnPlan = true;
        }
        //System.err.println("Agent: " + this.id + "got task" + currentTask.getTaskId() );
        this.currentTask = currentTask;
    }


    /**
     * Handle message from queue
     * @param msg
     */
    private void handleMessage(Message msg){
        switch (msg.getType()){
            case Task:
                break;
            case Loser:
                System.err.println(getAgentId() + " did not get : " + msg.getTask().getTaskId());
                break;
            case Winner:
                //Make real plan!
                currentTask = msg.getTask();
                try {
                    sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.err.println(getAgentId() + " is winner of task: " + msg.getTask().getTaskId() + " - Performing task");

                currentTask = null; //Im done with my task
                break;

            case TaskForBid:
                if(currentTask == null){ //ai.Agent is idle

                    //Calc bid -- Currently random! - Use heuristic

                    Random ran = new Random();
                    int bidSize = ran.nextInt(10);
                    System.err.println(getAgentId() + " bids " + bidSize);

                    Message bid = new Message(bidSize,this.getAgentId(), msg.getTask());

                    s.postMessageToSupervisor(bid);
                }else{
                    Message bid = new Message(Integer.MAX_VALUE, this.getAgentId(), msg.getTask()); //Send huge bid = im busy
                    s.postMessageToSupervisor(bid);
                }

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
                this.plan.clear();
                break;
            case MoveToASafePlace:
                System.err.println(this + " I was asked to move to a safe place.");
                moveToSafePlace();
                break;
            default:
                break;

        }
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

    public int getAgentId() { return id; }

    public GoalTask getCurrentTask() {
        return currentTask;
    }

    public String getColor() {
        return color;
    }

    public boolean isWorkingOnPlan() {
        return isWorkingOnPlan;
    }


    /**
     * Method for getting a string representation
     * of an agent.
     * @return
     */
    @Override
    public String toString() {
        return "[Agent "+id+"]";
    }
}
