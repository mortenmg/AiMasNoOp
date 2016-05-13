package ai;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

import static java.lang.Character.MAX_VALUE;

/**
 * Created by  on 4/5/16.
 */
public class Preprocessor {
    private BufferedReader serverMessages;
    int mapHeight = 0;
    int mapWidth = 0;

    private char[][] walls;
    private int [][] cor;

    private ArrayList<String> mapLines;

    private HashMap<Integer, Goal> goals = new HashMap<>();
    private HashMap<Integer, Box> boxes = new HashMap<>();
    private HashMap<Point, Integer> corridors = new HashMap<>();
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
            if (mapWidth < line.length()){
                mapWidth = line.length();
            }
            line = serverMessages.readLine();
        }

        //Setting the map size
        mapHeight = mapLines.size();
//        mapWidth = mapLines.get(0).length();
        System.err.println("Map size: " + mapWidth + "," + mapHeight);
        this.walls = new char[mapWidth][mapHeight];
        this.cor = new int[mapWidth][mapHeight];

        this.map = new Cell[mapWidth][mapHeight];

        int goalId = 0;
        int boxId = 0;

        // Read lines specifying level layout
        for (String ln : mapLines) {
            for (int x = 0; x < ln.length(); x++) {
                char id = ln.charAt(x);
                if ('0' <= id && id <= '9') { //If agent
                    agents.add(new Agent(id, colors.get(id), new Point(x,levelLine)));
                    map[x][levelLine] = new Cell(CellType.AGENT);
                } else if (id == '+') { //If wall
                    this.walls[x][levelLine] = id;
                    map[x][levelLine] = new Cell(CellType.WALL);
                } else if ('a' <= id && id <= 'z') { //If goal
                    goals.put(goalId, new Goal(goalId, id, new Point(x, levelLine)));
                    map[x][levelLine] = new Cell(CellType.GOAL, goalId);
                    goalId++;
                } else if ('A' <= id && id <= 'Z') { //If boxes
                    boxes.put(boxId, new Box(boxId, id, colors.get(id), new Point(x, levelLine)));
                    map[x][levelLine] = new Cell(CellType.EMPTY);
                    boxId++;
                } else {
                    map[x][levelLine] = new Cell(CellType.EMPTY);
                }
            }
            levelLine++; //Y-coordinate
        }

        System.err.println("Number of goals: " + goals.size());
        System.err.println("Number of boxes: " + boxes.size());

        level = new Level(map);
        level.setIntBoxes(boxes);
        level.setGoals(goals);

        printCorridorMap();

        createGraphFromMap();
        findCost();
        findCorridors();
//        getGoalTasks();  //Called from supervisor

        printCorridorMap(cor);

//        sortAgents();
        return agents;
    }

    private void findCost() {
        ArrayList<Node> allNodes = new ArrayList<>();

        for (ArrayList<Node> ls:graph) {
            allNodes.addAll(ls);
        }
        Iterator goalIterator = goals.entrySet().iterator();
        while(goalIterator.hasNext()){
            Map.Entry goalPair = (Map.Entry)goalIterator.next();
            Goal g = (Goal) goalPair.getValue();
            Node n = graph.get(g.point.x).get(g.point.y);
            GraphToolkit.dijkstra(allNodes,n);
            System.err.println("dikjstra: "+ n.getId());
        }
    }

    private void sortAgents() {
        Agent[] tempAgents = new Agent[agents.size()];
        ArrayList<Agent> tmpagents = new ArrayList<>();
        Agent a = agents.get(0);

//        for (Agent a: agents ) {
//            tmpagents.add(a.getAgentId(), a);
//        }

        agents = tmpagents;
    }

    public Cell[][] getMap() {
        return map;
    }
    public Level getLevel() { return level; }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    //Find and prioritize the initial goal tasks of the map
    public PriorityQueue<GoalTask> getGoalTasks() {
        PriorityQueue<GoalTask> goalTasks = new PriorityQueue<GoalTask>();

        //Iterate through hashmap of goals
//        System.err.println("Iterator on Goals hashmap:");
        Iterator goalIterator = goals.entrySet().iterator();
        int goalId = 0;
        while (goalIterator.hasNext()) {
            Map.Entry goalPair = (Map.Entry)goalIterator.next();
            Goal g = (Goal) goalPair.getValue();
//            System.err.println(goalPair.getKey() + " = " + g.letter);
            Iterator boxIterator = boxes.entrySet().iterator();
            int cost = MAX_VALUE;
            Box boxBest = null;

            //Prioritize goals by mesuring cost of every box to one goal
            while (boxIterator.hasNext()){
                Map.Entry boxPair = (Map.Entry)boxIterator.next();
                Box b = (Box) boxPair.getValue();
//                System.err.println("Trying to match goal(" + g.letter + ") with box(" + b.letter + ")");
                Point p = b.location;
                if (Character.toLowerCase(b.letter) == g.letter){
                    System.err.println("Goal and box is match goal(" + g.letter + ") at "+ g.point +" with box(" + b.letter + ") at " + b.location);
                    int c = level.getCostForCoordinateWithGoal(p.x,p.y,g.id);
//                    int c = (int)Math.sqrt(Math.pow((p.x-g.point.getX()),2)+Math.pow((p.y-g.point.getY()),2));
                    System.err.println("Price from goal("+ g.letter + ") to box(" +b.id +", "+ b.letter +") is " + c);
                    if (c < cost ){
                        boxBest = b;
                        cost = c;
                    }
                }
                //boxIterator.remove(); // avoids a ConcurrentModificationException
            }
            if (boxBest != null){
                GoalTask goalTask = new GoalTask(boxBest.id,g.id,goalId);
                goalTasks.offer(goalTask);
                goalId++;
            }
            goalIterator.remove(); // avoids a ConcurrentModificationException
        }

        System.err.println("goalTasks Printout:");
        for (GoalTask gt: goalTasks) {
            Goal g = goals.get(gt.getGoalId());
            System.err.println("GoalTasks - Task " + gt.getTaskId() + " Goal: " + gt.getGoalId() + " BoxID: " + gt.getBoxId());
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
            cor[x][y] = Character.valueOf(c)-47;
            return false;
        } else {
            walls[x][y] = (char) id;
            cor[x][y] = id-47;
            return true;
        }
    }

    public void printCorridorMap() {
        System.err.println("ai.Corridor map print out");
        String s = "";

        for (int i = 0; i < walls[0].length; i++) {
            s = "";
            if (walls[i][0] < ' ') //no more lines in map
                break;

            for (int j = 0; j < walls.length; j++) {
                if (walls[j][i] > ' ')
                    s = s + walls[j][i];
                else
                    s = s + " ";
            }
            System.err.println(s);
        }
    }

    public void printCorridorMap(int map[][]) {
        System.err.println("Map printout");
        String s = "";

        for (int i = 0; i < map[0].length; i++) {
            s = "";
//            if (map[i][0] < 0) //no more lines in map
//                break;

            for (int j = 0; j < map.length; j++) {
                if (map[j][i] > 0)
                    s = s + map[j][i];
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
        System.err.println ("createGraphFromMap: Size of map is " + map.length + ", "+ map[0].length);

        //Make Nodes
        for (int x = 0; x < this.map.length; x++) {
            this.graph.add(new ArrayList<>());
            for (int y = 0; y < map[y].length; y++) {
                Node n = new Node(new Point(x, y), this.map[x][y].getType());
                if (this.map[x][y].getType() == CellType.GOAL){
                    n.setId(this.map[x][y].getGoalId());
                }
                graph.get(x).add(n); //Add to graph
            }
        }
        System.err.print("createGRaphFromMap graph size: " + graph.size() + ", " + graph.get(0).size());

        //Make edges
        for (int y = 0; y < this.map.length; y++) {
            for (int x = 0; x < this.map[x].length; x++) {

                if (x + 1 < this.graph.get(y).size()) { //Right neighbor case
                    //if(j+1 < this.map[i].length){
                    this.graph.get(y).get(x).addNeighbor(new Edge(graph.get(y).get(x + 1), graph.get(y).get(x)));
                }
                if (x - 1 >= 0) { //Left neighbor case
                    this.graph.get(y).get(x).addNeighbor(new Edge(graph.get(y).get(x - 1), graph.get(y).get(x)));
                }
                if (y + 1 < this.graph.size()) { //Check for list below - Below neighbor case
                    if (x < this.graph.get(y + 1).size()) { //Check if list below is bigger then current index(x-axis)
                        this.graph.get(y).get(x).addNeighbor(new Edge(graph.get(y + 1).get(x), graph.get(y).get(x)));
                    }
                }
                if (y - 1 >= 0) { //Above neighbor case
                    if (x < this.graph.get(y - 1).size()) {
                        this.graph.get(y).get(x).addNeighbor(new Edge(graph.get(y - 1).get(x), graph.get(y).get(x)));
                    }
                }
            }
        }

        // Test goal dijksta
//        Point p = goals.get(0).point;
//        Node n = graph.get(p.x).get(p.y);
//        GraphToolkit.dijkstra(graph,n);

        //Save the graph in Level
        level.setGraph(graph);
    }

    public ArrayList<ArrayList<Node>> getGraph() {
        return graph;
    }
}