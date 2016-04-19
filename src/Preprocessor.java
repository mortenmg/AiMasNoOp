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
//    private char[][] goals = new char[70][70]; //2D array of goals
    private HashMap<Point,Character> goals = new HashMap<Point,Character>();
    private char[][] boxes = new char[70][70]; //2D array of goals
    private List< Agent > agents = new ArrayList< Agent >();

    //Testing arrays for making goal lists unprioritized at first
    private ArrayList<Point> goalPoints = new ArrayList<Point>();
    private ArrayList<Point> boxPoints = new ArrayList<Point>();
    private HashMap<Integer,Boolean> corridorTypes = new HashMap<Integer,Boolean>();

    private Cell[][] map = new Cell[70][70];

    public Preprocessor(BufferedReader serverMessages) {
        this.serverMessages = serverMessages;
        initializeCorridorTypes();
    }

    private void initializeCorridorTypes() {

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
                else if (id == '+') { //If wall
                    this.walls[levelLine][i] = id;
//                    System.err.println("Found wall at ("+ levelLine + "," + i +"): " + walls[levelLine][i]);
                }
                else if ('a' <= id && id <= 'z' ){ //If goal
                    goals.put(new Point(levelLine,i),id);
                    goalPoints.add(new Point(levelLine,i));
                }
                else if ('A' <= id && id <= 'Z'){ //If boxes
                    boxes[levelLine][i] = id;
                    boxPoints.add(new Point(levelLine,i));
                }
            }
            levelLine ++;
            line = serverMessages.readLine();
        }
        findCorridors();
        printCorridorMap();
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
                char goal = goals.get(g);
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
        char c;
        int id = 48; //Ascii character '0'

        for (int i = 1; i < walls.length-1;i++) {
            for (int j = 1; j < walls[0].length-1; j++){

                //If cell is a wall, skip
                c = walls[i][j];
                if (c == '+') continue;

                //If cell is empty, analyse
                else if(isCorridor(i,j)){
                    if(assignId(i,j,id)){
                        id++;
                    }
                }
//                isCorridorSum(i,j);
            }
        }
    }

    private boolean isCorridorSum(int x, int y){
        // Iterate 3x3 kernal
        int value = 1;
        int score = 0;
        String s = "";
        System.err.println("Kernal " + x + "," + y);
        for (int i = x-1; i <= x+1; i++){
            for (int j = y-1; j <= y+1; j++){
                if(walls[i][j] == '+')
                    score += value;
                value++;
                s = s + walls[i][j];
            }
            System.err.println(s);
            s = "";
        }
        walls[x][y] = (char)score;
        return false;
    }
    private boolean isCorridor(int x, int y){

        //Vertical corridor
        if((walls[x-1][y] !='+' || walls[x+1][y] != '+') && walls[x][y-1] == '+' && walls[x][y+1] == '+'){
            return true;
        }
        //Horizontal corridor
        else if(walls[x-1][y] == '+' && walls[x+1][y] == '+' && (walls[x][y-1] != '+' || walls[x][y+1] != '+')){
            return true;
        }
        //Corners - assume that you can escape from a free cell that is not a dead end
        //Corner LD
        else if(walls[x][y-1] == '+' && walls[x+1][y] == '+' && walls[x-1][y+1] == '+')
            return true;
        //Corner RD
        else if(walls[x][y-1] == '+' && walls[x-1][y] == '+' && walls[x+1][y+1] == '+')
            return true;
        //Corner LU
        else if(walls[x][y+1] == '+' && walls[x+1][y] == '+' && walls[x-1][y-1] == '+')
            return true;
        //Corner RU
        else if(walls[x][y+1] == '+' && walls[x-1][y] == '+' && walls[x+1][y-1] == '+')
            return true;

        return false;
    }

    private boolean assignId(int x, int y, int id){
        char c = (char)0;
        if (walls[x-1][y] > '+'){
            c = walls[x-1][y];
        }
        else if (walls[x+1][y] > '+'){
            c = walls[x+1][y];
        }
        else if (walls[x][y-1] > '+'){
            c = walls[x][y-1];
        }
        else if (walls[x][y+1] > '+'){
            c = walls[x][y+1];
        }
        if (c > (char)0){
            walls[x][y] = c;
            return false;
        }
        else{
            walls[x][y] = (char)id;
            return true;
        }
    }

    public void printCorridorMap(){
        System.err.println("Corridor map print out");
        String s = "";

        for (int i = 0; i < walls.length;i++) {
            s = "";
            if(walls[i][0] < ' ') //no more lines in map
                break;

            for (int j = 0; j < walls[0].length; j++){
                if(walls[i][j] > ' ')
                    s = s + walls[i][j];
                else
                    s = s + " ";
            }
            System.err.println(s);
        }
    }

}
