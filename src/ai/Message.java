package ai;

/**
 * Created by Mathias on 14-04-2016.
 */

enum MessageType{
    NewTaskRequest,
    Bid,
    Help,
    Winner,
    Loser,
    Task,
    TaskForBid,
    Terminate
}

public class Message implements Comparable<Message> {

    @Override//Only works for Messages of Bid type! so sort for these before comparing!
    public int compareTo(Message o) {
        return Integer.compare(this.getBid(),o.getBid());
    }

    private char receiver;
    private char sender;
    private MessageType type;
    private GoalTask task;
    private int possibleBid;

    /**
     * Simple message constructor
     * @param type
     */
    Message(MessageType type){
        this.receiver = Character.MIN_VALUE;
        this.sender = Character.MIN_VALUE;
        this.type = type;
    }

    /**
     * Constructor for bid message
     * @param bid bid size
     * @param sender Sender of message
     * @param task ai.Task for bid
     */
    Message(int bid, char sender, GoalTask task){
        this.receiver = 'S'; //S for supervisor
        this.possibleBid = bid;
        this.sender = sender;
        this.task = task;
        this.type = MessageType.Bid;
    }

    /**
     * Simple constructor only containing task and type
     * @param task
     * @param type
     */
    Message(GoalTask task, MessageType type){
        this.task = task;
        this.type = type;
    }

    Message(char receiver, char sender, GoalTask task, MessageType type){
        this.receiver = receiver;
        this.sender = sender;
        this.task = task;
        this.type = type;
    }

    public MessageType getType(){
        return this.type;
    }

    public char getSender(){
        return this.sender;
    }

    public char getReceiver(){
        return this.receiver;
    }

    public GoalTask getTask(){
        if (task == null){
            this.task = new GoalTask(0);
        }
        return this.task;
    }

    public void setSender(char sender){
        this.sender = sender;
    }

    public void setReceiver(char receiver){
        this.receiver = receiver;
    }

    public int getBid(){
        return possibleBid;
    }
}

