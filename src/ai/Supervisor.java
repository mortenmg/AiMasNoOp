package ai;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static ai.AgentStatus.WaitingForHelp;
import static ai.AgentStatus.WorkingOnHelperTask;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Supervisor extends Thread {
    private List<MAgent> agents = new ArrayList<>();

    private SAgent singleAgent;
    private PriorityQueue<GoalTask> goalTasks = new PriorityQueue<>();
    private BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

    private boolean debugging = false;

    public final Queue<Message> supervisorMsgQueue;

    private Level level;

    public static void main(String[] args) {

        Preprocessor p = new Preprocessor(Supervisor.getInstance().getServerMessages());

        try {
            p.receiveMapFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Supervisor.getInstance().setAgents(p.getAgents());
        Supervisor.getInstance().setLevel(p.getLevel());
        Supervisor.getInstance().setGoalTasks(p.getGoalTasks());

        System.err.println();
        System.err.println("+--------------------+");
        System.err.println("+       START        +");
        System.err.println("+--------------------+");
        Supervisor.getInstance().start();
    }

    public boolean isGoalFulfilledForBox(Point boxPos, int boxId){
        for(GoalTask gt: getGoalTasks()){
            if(gt.getBoxId() == boxId){
                Goal g = level.getGoalWithId(gt.getGoalId());
                if(g.point.equals(boxPos))
                    return true;
            }
        }
        return false;
    }


    /**
     * Supervisor main loop
     */
    @Override
    public void run() {

        if (agents.size() == 0) {
            this.singleAgent.start();
            singleAgentLoop();
        } else {
            for (MAgent agent : agents) {
                agent.addSupervisor(this);
                agent.start();
            }
            multiAgentLoop();
        }

    }

    private void multiAgentLoop() {
        while (goalTasks.size() > 0) {
            assignGoalTask();

            level.prepareNextLevel();

            if(!supervisorMsgQueue.isEmpty())
                handleMessage(supervisorMsgQueue.poll());

            ArrayList<Command> validCommands = getValidActions(); //Internal map is also updated!

            if (debugging) {
                //System.out.println(validCommands);
                level.updateToFuture();
            } else {
                if (sendActions(validCommands)) {
                    level.updateToFuture();
                }
            }

        }
    }

    private void handleMessage(Message msg) {
        switch (msg.getType()){
            case NeedHelp:
                assignHelperTask((HelperTask) msg.getPayload());
                break;
        }
    }

    private void assignHelperTask(HelperTask task) {
        MAgent bestAgent = null;
        int currentBestBid = Integer.MAX_VALUE;
        for (MAgent a: agents) {
            if (task.getColor().equals(a.getColor())) {
                Box boxToMove = level.getBoxWithId(task.getBoxId());
                int agentBid = SimpleHeuristic.euclidean(boxToMove.location, a.getPosition());

                if (agentBid < currentBestBid) {
                    currentBestBid = agentBid;
                    bestAgent = a;
                }
            }
        }
        task.setAgentId(bestAgent.getAgentId());
        bestAgent.postMsg(new Message(MessageType.Help, task));
    }

    private void singleAgentLoop() { //Make supervisor available until agent is done, but agent handles everything
        while (true) {}
    }


    private void assignGoalTask() {
        PriorityQueue<GoalTask> gts = new PriorityQueue<>(goalTasks);
        for (GoalTask git : goalTasks) {
            GoalTask gt = gts.poll();
            if (gt.getAgentId() != -1)
                continue;

            MAgent bestAgent = null;
            int currentBestBid = Integer.MAX_VALUE;
            for (MAgent a : agents) {

                if (a.commandQueueEmpty() && a.getCurrentTask() == null) {
                    Box gtBox = level.getBoxWithId(gt.getBoxId());
                    if (gt.getColor() == a.getColor()) {
                        int agentBid = SimpleHeuristic.distanceToGoal(level, a.getPosition().x, a.getPosition().y, gtBox.location.x, gtBox.location.y, gt.getGoalId());
                        if (agentBid < currentBestBid) {
                            currentBestBid = agentBid;
                            bestAgent = a;
                        }
                    }
                }
            }
            if (bestAgent != null) {
                System.err.println("[Supervisor] Assigned task #" + gt.getGoalId() + " to agent " + bestAgent.getAgentId());
                gt.setAgentId(bestAgent.getAgentId());
                bestAgent.postMsg(new Message(MessageType.Task, gt));
            }
        }
    }


    public SAgent getSingleAgent() {
        return singleAgent;
    }

    private MAgent getAgentFromPoint(Point p) {
        for (MAgent a : agents) {
            if (a.getPosition().equals(p))
                return a;
        }
        return null;
    }


    /**
     * Post a message to supervisor message queue
     **/
    public synchronized void postMessageToSupervisor(Message msg){
        supervisorMsgQueue.add(msg);
        notify();
    }


    /**
     * This method is receiving the actions from the agents
     * action queues. The action is added to the joint
     * actions sent to the server if it is valid.
     *
     * @return a list of valid actions
     */
    private ArrayList<Command> getValidActions() {

        ArrayList<Command> cmds = new ArrayList<>();
        Point p;

        for (MAgent a: agents){
            Command c = a.peekTopCommand();

            /*
            switch (a.getStatus()){
                case WaitingForHelp:

                    break;
            }
            */
            if (a.isWaitingForCorridor()) {
                // Agent is waiting for corridor..
                System.err.println("[Supervisor] Agent: " + a.getAgentId() + " i am waiting for the corridor!!");

                if (a.moveFromCorridor()) {
                    cmds.add(a.getAgentId(), a.pollCommand());
                    a.movedFromCorridor();
                } else {
                    // Test if corridor is open!
                    if (a.hasCorridorOpened()) {
                        System.err.println("[Supervisor] The Corridor has opened! Agent " + a.getAgentId() + " Will try to access" );
                        a.getBackToPlanPosition();
                        cmds.add(a.getAgentId(), a.pollCommand());
                    } else {
                        cmds.add(a.getAgentId(), null);
                    }
                }

            } else {
                if (a.getStatus() == WaitingForHelp && a.getSteps() == 0) {
                    cmds.add(a.getAgentId(), null);
                } else if (c == null && a.getCurrentTask() != null && !a.isWorkingOnPlan()) { // The agent is done calculating plan and supervisor has received all actions
                    GoalTask g = a.getCurrentTask();
                    this.goalTasks.remove(g);
                    a.setCurrentTask(null);
                    cmds.add(a.getAgentId(), null);
                    System.err.println("[Supervisor] I have received all actions from agent " + a.getAgentId());
                } else if ((p = level.conflictingCellFromMove(c, a)) == null) { // The move is valid
                    cmds.add(a.getAgentId(), a.pollCommand());
                    a.decrementSteps();
                } else {
                    handleInvalidMove(p, a);
                    cmds.add(a.getAgentId(), null);
                }
            }
        }
        return cmds;
    }

    /**
     * Method for handling an invalid action from an agent.
     * This method will ask the agent to either replan,
     * move or something else.
     *
     * @param p is the invalid point.
     * @param a is the agent that is trying to perfom an action
     */
    private void handleInvalidMove(Point p, MAgent a) {
        if (!p.equals(new Point(-1,-1))) {
            Box b;
            MAgent otherAgent;
            if ((b = level.getBoxAtPosition(p)) != null){ // There is a box in the way.
                if (a.getStatus() != WaitingForHelp)
                    a.postMsg(new Message(MessageType.Replan));
            } else if ((otherAgent = this.getAgentFromPoint(p)) != null){ // There is an agent in the way
                if (otherAgent.commandQueueEmpty() && !otherAgent.isWorkingOnPlan()) { // The other agent is not doing anything
                    otherAgent.postMsg(new Message(MessageType.MoveToASafePlace, a.getRestOfPlan()));
                    System.err.println("[Supervisor] The agent " + otherAgent.getAgentId() + " was kindly asked to move to another place");
                }
            }
        }
    }

    /**
     * Method for sending commands to the server.jar
     *
     * @param commands
     * @return returns if the command was accepted by the server.
     */
    private boolean sendActions(ArrayList<Command> commands) {
        String jointAction = "[";

        for (Command cmd : commands) {
            if (cmd != null)
                jointAction += cmd.toString() + ",";
            else
                jointAction += "NoOp" + ",";
        }

        jointAction = jointAction.substring(0, jointAction.length() - 1);
        jointAction += "]";

        // Place message in buffer
        System.err.println("[Supervisor] Sending to server: " + jointAction);
        System.out.println(jointAction);

        // Flush buffer
        System.out.flush();

        // Disregard these for now, but read or the server stalls when its output buffer gets filled!
        String percepts = null;
        try {
            percepts = serverMessages.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (percepts == null)
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
        this.supervisorMsgQueue = new LinkedList<>();
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setAgents(List<Agent> agents) {
        if(agents.size() == 1){
            singleAgent = new SAgent(agents.get(0));
        }else{
            for(Agent a: agents){
                this.agents.add(new MAgent(a));
            }
        }
    }

    public void setGoalTasks(PriorityQueue<GoalTask> goalTasks) {
        this.goalTasks = goalTasks;
    }

    public PriorityQueue<GoalTask> getGoalTasks() {
        return goalTasks;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    public Level getLevel() {
        return level;
    }

    public BufferedReader getServerMessages() {
        return serverMessages;
    }

    public MAgent getAgentWithId(int id) {
        return agents.get(id);
    }

    public List<MAgent> getAgents() {
        return agents;
    }
}