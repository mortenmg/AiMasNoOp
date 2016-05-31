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
    NeedHelp
}

public class Message {

    private int sender;
    private MessageType type;
    private Object payload;

    /**
     * Simple message constructor
     * @param type
     */
    Message(MessageType type){
        this.sender = Character.MIN_VALUE;
        this.type = type;
    }

    Message(MessageType type, Object payload) {
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

    public void setSender(int sender){
        this.sender = sender;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}

