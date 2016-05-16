package ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Supervisor extends Thread {
    private List< Agent > agents = new ArrayList<>();
    private PriorityQueue<GoalTask> goalTasks = new PriorityQueue<>();
    private BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );
    public final Queue<Message> supervisorMsgQueue;
;
    private Level level;

    public static void main( String[] args ) {
        System.err.println( "Supervisor is running!" );

        // Retrieving the supervisor singleton
        Supervisor.getInstance().start();
    }


    /**
     * Supervisor main loop
     */
    @Override
    public void run() {
        for (Agent agent: agents) {
            agent.addSupervisor(this);
            agent.start();
        }

        while(goalTasks.size() > 0){
            System.err.println("In the while loop");

            assignGoalTask();

            ArrayList<Command> validCommands = getValidActions(); //Internal map is also updated!

            sendActions(validCommands);

        }


    /*
        //For each initial task, broadcast it.
        int totalTaskCount = 5; //Mock of tasklist

        while (totalTaskCount > 0) {
            System.err.println("Main loop supervisor");
            //While not all agents have task - Part of initial routine
            Message task = new Message(new GoalTask(0,0,0, ""), MessageType.TaskForBid);

            broadcastMessage(task);

            handleBidsOnTask();

            totalTaskCount--;
            //end while
            //Final loop - Handle incoming help message and requests for new tasks
        }
            //while (sendActions());
     */
    }

    private void assignGoalTask() {

        System.err.println("GoalTasks: " + goalTasks.size());
        for (GoalTask gt : goalTasks) {

                Agent bestAgent = null;
                int currentBestBid = Integer.MAX_VALUE;
                for (Agent a : agents) {

                    if (a.commandQueueEmpty() && a.getCurrentTask() == null) {
                        Box gtBox = level.getBoxWithId(gt.getBoxId());
                        if (gt.getColor() == a.getColor()) {
                            //int agentBid = SimpleHeuristic.euclidean(a.getPosition().x, a.getPosition().y, gtBox.location.x, gtBox.location.y);
                            int agentBid = SimpleHeuristic.distanceToGoal(level,a.getPosition().x, a.getPosition().y, gtBox.location.x, gtBox.location.y, gt.getGoalId());
                            if (agentBid < currentBestBid) {
                                currentBestBid = agentBid;
                                bestAgent = a;
                            }
                        }
                    }
                }
                if (bestAgent != null) {
                    System.err.println("Supervisor assigned a task to agent!");
                    bestAgent.setCurrentTask(gt);
                    bestAgent.postMsg(new Message(MessageType.Task));
                }
            }
        }


        /**
         *Internal method for getting an agent from its name.
         **/
    private Agent getAgentFromName(int agentName){
        List<Agent> allAgents = agents.stream().filter(c -> c.getAgentId() == agentName).collect(Collectors.toList());
        return allAgents.get(0);
    }

    private int getCountOfBidsInQueue(){
        int count = (int) supervisorMsgQueue.stream().filter(c -> c.getType() == MessageType.Bid).count();
        return count;
    }

    private void handleBidsOnTask(){
        synchronized (this) {
            while (supervisorMsgQueue.size() < agents.size()) {
                try {
                    System.err.println("Waiting for all bids");

                    wait(); //TODO: Wait only for some time!
                } catch (Exception e) {
                    System.err.println("junk");
                    System.err.println(e.getClass());
                }
            }
            announceWinnerOfTask();
        }
    }

    /**
     * returns all bid messages from message queue, these are removed
     * @return
     */
    private List<Message> getBidsFromQueue(){
        Iterator<Message> i = supervisorMsgQueue.iterator();

        List<Message> bids = new ArrayList<>();

        while(i.hasNext()){
            Message next = i.next();
            if(next.getType() == MessageType.Bid){
                bids.add(next);
                i.remove();
            }
        }

        return bids;
    }

    private void announceWinnerOfTask(){

        List<Message> bids = getBidsFromQueue();

        Message minBid = Collections.min(bids);

        for (Message bid : bids) {
            int receiver = bid.getSender();
            if (bid == minBid) {
                sendMessageToAgent(getAgentFromName(receiver), new Message(bid.getTask(), MessageType.Winner));
            } else {
                sendMessageToAgent(getAgentFromName(receiver), new Message(bid.getTask(), MessageType.Loser));
            }
        }
        supervisorMsgQueue.clear();
    }

    /**
     * send message to single agent
     * @param a Agent to send message to
     * @param msg Message to send
     */
    private synchronized void sendMessageToAgent(Agent a, Message msg) {
        a.postMsg(msg);
    }

    /**
     * Post a message to supervisor message queue
     **/
    public synchronized void postMessageToSupervisor(Message msg){
        supervisorMsgQueue.add(msg);
        notify();
    }

    /**
     * Broadcast message to all agents
     * @param msg
     */
    public void broadcastMessage(Message msg){
        System.err.println("Supervisor: Broadcasting - " + msg.getTask().getTaskId());

        for (Agent a: agents) {
            a.postMsg(msg);
        }
    }

    //TODO: Handle case where agent does not have plan yet!
    private ArrayList<Command> getValidActions() {

        ArrayList<Command> cmds = new ArrayList<>();

        for (Agent a: agents){
            Command c = a.peekTopCommand();
            System.err.println(c);
            if (c == null && a.getCurrentTask()!=null && !a.isWorkingOnPlan()) {
                GoalTask g = a.getCurrentTask();
                this.goalTasks.remove(g);
                a.setCurrentTask(null);
                System.err.println("Agent is done with plan!!!!!!!");
            }
            if (level.isMoveValidForAgent(c, a)){
                System.err.println("Move "+c+" is valid");
                cmds.add(a.getAgentId(),a.pollCommand());
            }else{
                cmds.add(a.getAgentId(),null);
                // TODO: Handle invalid command! - Send message to agent
                a.postMsg(new Message(MessageType.Replan));
            }
        }
        return cmds;
    }

    private boolean sendActions(ArrayList<Command> commands) {
        String jointAction = "[";

        for(Command cmd: commands){
            if(cmd != null)
                jointAction += cmd.toString() + ",";
            else
                jointAction += "NoOp" + ",";
        }

        jointAction = jointAction.substring(0,jointAction.length()-1);
        jointAction +=  "]";

        // Place message in buffer
        System.err.println( jointAction );
        System.out.println( jointAction );

        // Flush buffer
        System.out.flush();

        // Disregard these for now, but read or the server stalls when its output buffer gets filled!
        String percepts = null;
        try {
            percepts = serverMessages.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ( percepts == null )
            return false;

        return true;
    }

    private static Supervisor ourInstance = new Supervisor();

    public static Supervisor getInstance() {
        return ourInstance;
    }

    /**
     * The constructor of the Supervisor class.
     * This is private due to the singleton
     * pattern.
     */
    private Supervisor() {
        Preprocessor p = new Preprocessor(serverMessages);

        try {
            p.readMap();
            level = p.getLevel();

            agents = p.getAgents();

            this.goalTasks = p.getGoalTasks();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.supervisorMsgQueue = new LinkedList<>();
    }

    public Level getLevel() { return level; }

    public Agent getAgentWithId(int id) {
        return agents.get(id);
    }
}