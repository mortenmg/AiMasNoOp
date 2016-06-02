package ai;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Mathias on 18-05-2016.
 */
public class SAgent extends Agent {

    private SAAStarPlanner planner;

    public SAgent(Agent a){
        super(a.getAgentId(), "", new Point(a.getPosition().x,a.getPosition().y));
        this.planner = new SAAStarPlanner();
    }

    @Override
    public void run() {
        //super.run();
        //Single agent loop!
        LinkedList<SAState> states = planner.generatePlan();

        // Just printing the plans actions
        System.err.println(this+ " My plan is length: "+states.size());
        System.err.print(this + " My plan: ");
        for (SAState state : states) {
            System.err.print(state.action+" ");
        }

        System.err.println("Sending solution to server!");
        for (SAState state : states) {
            System.out.println(state.action.toActionString());
            //System.out.flush();
            try {
                String response = Supervisor.getInstance().getServerMessages().readLine();
                System.err.print("Response: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
