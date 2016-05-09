import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Supervisor extends Thread {
    private  List< Agent > agents = new ArrayList<>();
    private BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );
    public final Queue<Message> supervisorMsgQueue;
    private Cell[][] map;
    private Level level;

    public static void main( String[] args ) {
        System.err.println( "Supervisor is running!" );

        // The solution is a list of "nodes" or states
        //LinkedList< Node > solution = null;
        //Command.every;
        // Retrieving the supervisor singleton
        Supervisor.getInstance().start();

        // Ask the supervisor to start sending the steps to server
        // while ( s.update() ) ;

    }



    /**
     * Starting the agents.
     */
    @Override
    public void run() {
        for (Agent agent: agents) {
            agent.addSupervisor(this);
            agent.start();
        }

        //For each initial task, broadcast it.
        int totalTaskCount = 5; //Mock of tasklist

        while (totalTaskCount > 0) {
            System.err.println("Main loop supervisor");
            //While not all agents have task - Part of initial routine
            Message task = new Message(new GoalTask(totalTaskCount), MessageType.TaskForBid);

            broadcastMessage(task);

            handleBidsOnTask();

            totalTaskCount--;
            //end while
            //Final loop - Handle incoming help message and requests for new tasks
        }
            while (sendActions());
    }

    /**
     *Internal method for getting an agent from its name.
     **/
    private Agent getAgentFromName(char agentName){
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
            char receiver = bid.getSender();
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

    private boolean sendActions() {
        String jointAction = "[";

        // In this loop we are sending the actions that is waiting to be sent.
        for ( int i = 0; i < agents.size() - 1; i++ )
            jointAction += agents.get( i ).act() + ",";

        jointAction += agents.get( agents.size() - 1 ).act() + "]";

        // Place message in buffer
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
            level = new Level(p.getMap());
            level.setAgents(p.getAgents());

            agents = p.getAgents();

            map = new Cell[p.mapWidth][p.mapHeight];
            map = p.getMap();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.supervisorMsgQueue = new LinkedList<>();
    }

    public Cell[][] getMap(){
       return map;
    }
}