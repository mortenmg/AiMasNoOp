package ai;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by  on 4/5/16.
 */
public class Preprocessor {
    private BufferedReader serverMessages;
    int mapHeight = 0;
    int mapWidth = 0;

    private char[][] walls;

    private ArrayList<String> mapLines;

    private HashMap<Integer, Goal> goals = new HashMap<>();
    private HashMap<Integer, Box> boxes = new HashMap<>();
    private ArrayList<Agent> agents = new ArrayList<>();


    private HashMap<Integer,Boolean> corridorTypes = new HashMap<Integer,Boolean>();
    //For graph creation
    private ArrayList<ArrayList<Node>> graph = new ArrayList<>();

    private static Cell[][] map;
    private Level level;

    public Preprocessor(BufferedReader serverMessages) {
        this.serverMessages = serverMessages;
        initializeCorridorTypes();
    }

    private void initializeCorridorTypes() {

    }

    public List<Agent> readMap() throws IOException {

        mapLines = new ArrayList<String>();
        Map<Character, String> colors = new HashMap<Character, String>();
        String line, color;
        int levelLine = 0;

        // Read lines specifying colors
        while ((line = serverMessages.readLine()).matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
            line = line.replaceAll("\\s", "");
            color = line.split(":")[0];

            for (String id : line.split(":")[1].split(","))
                colors.put(id.charAt(0), color);
        }

        //Read maplines into a buffer to optimize the seize of the variables holding the map
        while (!line.equals("")) {
            mapLines.add(line);
            line = serverMessages.readLine();
        }

        //Setting the map size
        mapHeight = mapLines.get(0).length();
        mapWidth = mapLines.size();
        this.walls = new char[mapWidth][mapHeight];

        map = new Cell[mapWidth][mapHeight];

        int goalId = 0;
        int boxId = 0;

        System.err.println("MapSize: " + this.walls[0].length + "," + this.walls.length);

        // Read lines specifying level layout
        for (String ln : mapLines) {
            for (int i = 0; i < ln.length(); i++) {
                char id = ln.charAt(i);
                if ('0' <= id && id <= '9') { //If agent
                    agents.add(new Agent(id, colors.get(id)));
                    //map[levelLine][i].setAgent(id);
                } else if (id == '+') { //If wall
                    this.walls[levelLine][i] = id;
                    map[levelLine][i] = new Cell(CellType.WALL);
                } else if ('a' <= id && id <= 'z') { //If goal
                    goals.put(goalId, new Goal(goalId, id, new Point(levelLine, i)));
                    map[levelLine][i] = new Cell(CellType.GOAL);
                    goalId++;
                } else if ('A' <= id && id <= 'Z') { //If boxes
                    boxes.put(boxId, new Box(boxId, id, colors.get(id), new Point(levelLine, i)));
                    map[levelLine][i] = new Cell(CellType.EMPTY, boxId);
                    boxId++;
                }
            }
            levelLine++;
        }

        System.err.println("Number of goals: " + goals.size());
        System.err.println("Number of boxes: " + boxes.size());

        level.setBoxes(boxes);
        level.setGoals(goals);

        findCorridors();
        printCorridorMap();
        createGraphFromMap();
        return agents;
    }

    public Cell[][] getMap() {
        return map;
    }
    public Level getLevel() { return level; }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    //Find and prioritize the initial goal tasks of the map
    private PriorityQueue<GoalTask> findGoalTasks() {
        PriorityQueue<GoalTask> goalTasks = new PriorityQueue<GoalTask>();
        //Loop through goals and find a box for each goal
        for (int goalKey : goals.keySet()) {
            for (int boxKey : boxes.keySet()) {
                char goalLetter = goals.get(goalKey).letter;
                char boxLetter = boxes.get(boxKey).letter;
                if (goalLetter == Character.toLowerCase(boxLetter)) {
                    goalTasks.add(new GoalTask(0,0,0)); //TODO add arguments to fit goalTask class
                }
            }
        }
        //TODO Implement priority
        return goalTasks;
    }

    //Need a datastructure for corridors
    private void findCorridors() {
        char c;
        int id = 48; //Ascii character '0'

        for (int i = 1; i < walls.length - 1; i++) {
            for (int j = 1; j < walls[0].length - 1; j++) {

                //If cell is a wall, skip
                c = walls[i][j];
                if (c == '+') continue;

                    //If cell is empty, analyse
                else if (isCorridor(i, j)) {
                    if (assignId(i, j, id)) {
                        id++;
                    }
                }
//                isCorridorSum(i,j);
            }
        }
    }

    private boolean isCorridorSum(int x, int y) {
        // Iterate 3x3 kernal
        int value = 1;
        int score = 0;
        String s = "";
        System.err.println("Kernal " + x + "," + y);
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (walls[i][j] == '+')
                    score += value;
                value++;
                s = s + walls[i][j];
            }
            System.err.println(s);
            s = "";
        }
        walls[x][y] = (char) score;
        return false;
    }

    private boolean isCorridor(int x, int y) {

        //Vertical corridor
        if ((walls[x - 1][y] != '+' || walls[x + 1][y] != '+') && walls[x][y - 1] == '+' && walls[x][y + 1] == '+') {
            return true;
        }
        //Horizontal corridor
        else if (walls[x - 1][y] == '+' && walls[x + 1][y] == '+' && (walls[x][y - 1] != '+' || walls[x][y + 1] != '+')) {
            return true;
        }
        //Corners - assume that you can escape from a free cell that is not a dead end
        //Corner LD
        else if (walls[x][y - 1] == '+' && walls[x + 1][y] == '+' && walls[x - 1][y + 1] == '+')
            return true;
            //Corner RD
        else if (walls[x][y - 1] == '+' && walls[x - 1][y] == '+' && walls[x + 1][y + 1] == '+')
            return true;
            //Corner LU
        else if (walls[x][y + 1] == '+' && walls[x + 1][y] == '+' && walls[x - 1][y - 1] == '+')
            return true;
            //Corner RU
        else if (walls[x][y + 1] == '+' && walls[x - 1][y] == '+' && walls[x + 1][y - 1] == '+')
            return true;

        return false;
    }

    private boolean assignId(int x, int y, int id) {
        char c = (char) 0;
        if (walls[x - 1][y] > '+') {
            c = walls[x - 1][y];
        } else if (walls[x + 1][y] > '+') {
            c = walls[x + 1][y];
        } else if (walls[x][y - 1] > '+') {
            c = walls[x][y - 1];
        } else if (walls[x][y + 1] > '+') {
            c = walls[x][y + 1];
        }
        if (c > (char) 0) {
            walls[x][y] = c;
            return false;
        } else {
            walls[x][y] = (char) id;
            return true;
        }
    }

    public void printCorridorMap() {
        System.err.println("ai.Corridor map print out");
        String s = "";

        for (int i = 0; i < walls.length; i++) {
            s = "";
            if (walls[i][0] < ' ') //no more lines in map
                break;

            for (int j = 0; j < walls[0].length; j++) {
                if (walls[i][j] > ' ')
                    s = s + walls[i][j];
                else
                    s = s + " ";
            }
            System.err.println(s);
        }
    }

    /**
     * @return
     * @author Rasmus
     */
    public HashMap<Integer, Goal> getGoals() {
        return goals;
    }

    public void createGraphFromMap() {

        //Make Nodes
        for (int i = 0; i < this.map.length; i++) {
            this.graph.add(new ArrayList<>());
            for (int j = 0; j < map[i].length; j++) {
                Node n = new Node(new Point(i, j), this.map[i][j].getType());

                graph.get(i).add(n); //Add to graph
            }
        }

        //Make edges
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[i].length; j++) {

                if (j + 1 < this.graph.get(i).size()) { //Right neighbor case
                    //if(j+1 < this.map[i].length){
                    this.graph.get(i).get(j).addNeighbor(new Edge(graph.get(i).get(j + 1), graph.get(i).get(j)));
                }
                if (j - 1 >= 0) { //Left neighbor case
                    this.graph.get(i).get(j).addNeighbor(new Edge(graph.get(i).get(j - 1), graph.get(i).get(j)));
                }
                if (i + 1 < this.graph.size()) { //Check for list below - Below neighbor case
                    if (j < this.graph.get(i + 1).size()) { //Check if list below is bigger then current index(x-axis)
                        this.graph.get(i).get(j).addNeighbor(new Edge(graph.get(i + 1).get(j), graph.get(i).get(j)));
                    }
                }
                if (i - 1 >= 0) { //Above neighbor case
                    if (j < this.graph.get(i - 1).size()) {
                        this.graph.get(i).get(j).addNeighbor(new Edge(graph.get(i - 1).get(j), graph.get(i).get(j)));
                    }
                }
            }
        }
    }

    public ArrayList<ArrayList<Node>> getGraph() {
        return graph;
    }
}



