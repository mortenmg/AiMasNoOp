package ai;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Philip on 12-04-2016.
 */
public class Corridor {

    private boolean locked = false;
    private int agent = -1;

    Corridor(){
        locked = false;
        agent = -1;
    }

    public void lock(int agentID){
        locked = true;
        agent = agentID;
    }

    public void unlock(int agentID){
        if(agentID == this.agent){
            locked = false;
            this.agent = -1;
        }
    }

    public int getOwner(){ return agent; }

    public boolean locked(){
        return locked;
    }

}
