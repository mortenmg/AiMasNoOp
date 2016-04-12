import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Preprocessor {
    private BufferedReader serverMessages;
    private char[][] walls = new char[70][70]; //2D Array of walls
    private char[][] goals = new char[70][70]; //2D array of goals
    private char[][] boxes = new char[70][70]; //2D array of goals
    private List< Agent > agents = new ArrayList< Agent >();

    //Testing arrays for making goal lists unprioritized at first
    private ArrayList<Point> goalPoints = new ArrayList<Point>();
    private ArrayList<Point> boxPoints = new ArrayList<Point>();


    public Preprocessor(BufferedReader serverMessages) {
        this.serverMessages = serverMessages;
    }

    public List<Agent> readMap() throws IOException {
        //int[][] mapStatic = new int[70][70];  //Static part of the map saved in an integer array

        Map< Character, String > colors = new HashMap< Character, String >();
        String line, color;
        int levelLine = 0;

        // Read lines specifying colors
        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            color = line.split( ":" )[0];

            for ( String id : line.split( ":" )[1].split( "," ) )
                colors.put( id.charAt( 0 ), color );
        }



        // Read lines specifying level layout
        while ( !line.equals( "" ) ) {
            for (int i = 0; i < line.length(); i++) {
                char id = line.charAt(i);
                if ('0' <= id && id <= '9') //If agent
                    agents.add(new Agent(id, colors.get(id)));
                else if (id == '+') //If wall
                    walls[levelLine][i] = id;
                else if ('a' <= id && id <= 'z' ){ //If goal
                    goals[levelLine][i] = id;
                    goalPoints.add(new Point(levelLine,i));
                }
                else if ('A' <= id && id <= 'Z'){ //If boxes
                    boxes[levelLine][i] = id;
                    boxPoints.add(new Point(levelLine,i));
                }
            }
            line = serverMessages.readLine();
        }
        return agents;
    }

    public List<Agent> getAgents(){
        return agents;
    }

    //Find and prioritize the initial goal tasks of the map
    private PriorityQueue<GoalTask> findGoalTasks(){
        PriorityQueue <GoalTask> goalTasks = new PriorityQueue<GoalTask>() ;
        //Loop through goals and find a box for each goal
        for (Point g : goalPoints) {
            for (Point b : boxPoints) {
                char goal = goals[g.x][g.y];
                char box = Character.toLowerCase(boxes[b.x][b.y]);
                if (goal == box){
                    goalTasks.add(new GoalTask()); //TODO add arguments to fit goalTask class
                }
            }
        }
        //TODO Implement priority
        return goalTasks;
    }

    //Need a datastructure for corridors
    private void findCorridors(){
        //return corridors
    }

}
