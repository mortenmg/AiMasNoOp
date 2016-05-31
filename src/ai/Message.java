package ai;

import java.util.Objects;

/**
 * Created by Mathias on 14-04-2016.
 */

enum MessageType{
    Help,
    Task,
    Terminate,
    Replan,
    MoveToASafePlace,
    MoveFromCorridor
}

public class Message {

    private char receiver;
    private int sender;
    private MessageType type;
    private GoalTask task;
    private Object payload;

    /**
     * Simple message constructor
     * @param type
     */
    Message(MessageType type){
        this.receiver = Character.MIN_VALUE;
        this.sender = Character.MIN_VALUE;
        this.type = type;
    }

    Message(MessageType type, Object payload) {
        this.receiver = Character.MIN_VALUE;
        this.sender = Character.MIN_VALUE;
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType(){
        return this.type;
    }

    public int getSender(){
        return this.sender;
    }

    public char getReceiver(){
        return this.receiver;
    }

    public GoalTask getTask(){
        if (task == null){
            this.task = new GoalTask(0,0,0, "");
        }
        return this.task;
    }

    public void setSender(int sender){
        this.sender = sender;
    }

    public void setReceiver(char receiver){
        this.receiver = receiver;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}

