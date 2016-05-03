import Planning.AStarPlanner;
import Planning.Command;
import Planning.Planner;

import java.util.Random;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Agent implements Runnable{
    private char id;
    private String color;
    private Planner planner;

    public Agent( char id, String color ) {
        this.id = id;
        this.color = color;
        this.planner = new AStarPlanner();
    }

    public String act() {
        Random rand = new Random();
        return Command.every[rand.nextInt( Command.every.length )].toString();
    }

    @Override
    public void run() {
        System.err.println("Hi from agent "+id);
    }


}
