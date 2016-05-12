package ai;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static ai.MessageType.*;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Agent extends Thread {
    private final Queue<Message> agentMsgQueue;
    private char id;
    private String color;
    private Supervisor s;
    private GoalTask currentTask;

    private boolean terminateFlag = false;
    private Planner planner;

    public Agent( char id, String color ) {
        this.id = id;
        this.color = color;
        this.planner = new AStarPlanner();
        this.agentMsgQueue = new LinkedList<>();
    }

    public String act() {
        Random rand = new Random();
        return Command.every[rand.nextInt( Command.every.length )].toString();
    }

    @Override
    public void run() {
        System.err.println("Hi from agent "+id);

        // ai.Agent will calculate a plan
        ai.State s = new ai.State(null);
        planner.generatePlan(s, new GoalTask(0,0,0));

        //ai.Agent loop
        while(!terminateFlag) {

            handleMessage(getMessage());

        }
        System.err.println(getAgentId() + " terminated");

    }


    /**
     * Handle message from queue
     * @param msg
     */
    private void handleMessage(Message msg){
        switch (msg.getType()){
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

    public char getAgentId() {
        return id;
    }


}
