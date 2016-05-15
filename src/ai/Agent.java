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
    private boolean isWorking = false;

    public boolean isWorking() {
        return isWorking;
    }

    private boolean terminateFlag = false;
    private Planner planner;

    public Agent(int id, String color, Point position) {
        this.id = id;
        this.position = position;
        this.color = color;
        this.planner = new AStarPlanner();
        this.agentMsgQueue = new LinkedList<>();
        this.plan = new LinkedList<>();
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public String act() {
        Random rand = new Random();
        return Command.every[rand.nextInt(Command.every.length)].toString();
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
        while (!terminateFlag) {
            System.err.println("Hi from agent " + id);

            // ai.Agent will calculate a plan
            if (currentTask != null) {
                System.err.println("gent " + id + "is solving " + currentTask.getTaskId());
                ai.State s = new ai.State(null);
                LinkedList<ai.State> states = planner.generatePlan(s, currentTask);
                // Just printing the plans actions
                /*
                for (ai.State state : states) {
                    System.err.println(state.action);
                }
                */
                addPlan(states);
                isWorking = false;
            }


            //addPlan(planner.generatePlan(s, new GoalTask(0,0,0)));

            //ai.Agent loop

            handleMessage(getMessage());
        }
        System.err.println(getAgentId() + " terminated");

    }

    public void setCurrentTask(GoalTask currentTask) {
        System.err.println("Agent: " + this.id + "got task" + currentTask.getTaskId() );
        this.currentTask = currentTask;
    }


    /**
     * Handle message from queue
     * @param msg
     */
    private void handleMessage(Message msg){
        switch (msg.getType()){
            case Task:
                isWorking = true;
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
}
