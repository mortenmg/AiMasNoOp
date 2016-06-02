package ai;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    private int [][] cor;
    private Corridor[] corridorLocks;

    private char[][] walls;

    private HashMap<Integer, Goal> goals = new HashMap<>();
    private HashMap<Integer, Box> boxes = new HashMap<>();
    private HashMap<Point, Integer> corridors = new HashMap<>();

    private ArrayList<Agent> agents = new ArrayList<>();
    private ArrayList<ArrayList<Node>> graph = new ArrayList<>();

    private static Cell[][] map;
    private Level level;

    public Preprocessor(BufferedReader serverMessages) {
        this.serverMessages = serverMessages;
        initializeCorridorTypes();
    }

    public Preprocessor() {
        System.err.println("+--------------------+");
        System.err.println("+    PREPROCESSING   +");
        System.err.println("+--------------------+");
    }

    private void initializeCorridorTypes() {

    }

    /**
     * Wrapper method for the receiveMap() method.
     * This sets the buffered reader to the
     * System.in buffered reader for the
     * server jar file.
     *
     * @throws IOException
     */
    public void receiveMapFromServer() throws IOException{
        receiveMap(serverMessages);
    }

    /**
     * Wrapper method for the receiveMap() method,
     * that uses a level file for buffered reader
     *
     * @param fileName
     * @throws IOException
     */
    public void receiveMapFromFile(String fileName) throws IOException {
        receiveMap(new BufferedReader(new FileReader(fileName)));
    }

    /**
     * This method reads a map from a buffered reader and stores it
     * in temporary datastructures. These structures are then
     * used in the readMap() method to construct the real
     * data structures
     *
     * @param br
     * @throws IOException
     */
    private void receiveMap(BufferedReader br) throws IOException {
        ArrayList<String> mapLines = new ArrayList<>();

        Map<Character, String> colors = new HashMap<>();
        String line, color;
        int mapWidth = 0;

        // Read lines specifying colors
        while ((line = br.readLine()).matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
            line = line.replaceAll("\\s", "");
            color = line.split(":")[0];

            for (String id : line.split(":")[1].split(","))
                colors.put(id.charAt(0), color);
        }

        //Read map lines into a buffer to optimize the seize of the variables holding the map
        while (line != null && !line.equals("")) {
            mapLines.add(line);
            if (mapWidth < line.length()){
                mapWidth = line.length();
            }
            line = br.readLine();
        }

        readMap(mapLines, colors, mapWidth);
    }

    private List<Agent> readMap(ArrayList<String> mapLines, Map<Character, String> colors, int mapWidth) {

        HashMap<Point, Agent> futureAgents = new HashMap<>();

        //Setting the map size
        mapHeight = mapLines.size();
        this.mapWidth = mapWidth;


        System.err.println("Map size: " + mapWidth + "," + mapHeight);
        this.walls = new char[mapHeight][mapWidth];
        this.cor = new int[mapHeight][mapWidth];

        SAState.MAX_COLUMN = mapWidth;
        SAState.MAX_ROW = mapHeight;
        this.map = new Cell[mapHeight][mapWidth];
        for(int row = 0; row < mapHeight; row++){
            for (int col = 0; col < mapWidth; col++){
                this.map[row][col] = new Cell(CellType.EMPTY);
                this.walls[row][col] = ' ';
            }
        }

        SAState.goals = new char[SAState.MAX_ROW][SAState.MAX_COLUMN];
        SAState.walls = new boolean[SAState.MAX_ROW][SAState.MAX_COLUMN];

        int goalId = 0;
        int boxId = 0;

        int levelLine = 0;

        // Read lines specifying level layout
        for (String ln : mapLines) {
            System.err.println(ln);

            for (int x = 0; x < ln.length(); x++) {
                char id = ln.charAt(x);
                if ('0' <= id && id <= '9') { //If agent
                    agents.add(new Agent(Character.getNumericValue(id), colors.get(id), new Point(x,levelLine)));
                    futureAgents.put(new Point(x,levelLine), new Agent(Character.getNumericValue(id), colors.get(id), new Point(x,levelLine)));
                    map[levelLine][x] = new Cell(CellType.EMPTY);
                } else if (id == '+') { //If wall
                    this.walls[levelLine][x] = id;
                    SAState.walls[levelLine][x] = true;
//                    System.err.println("X:" + x + ", Y: " + levelLine + "Is wall");
                    map[levelLine][x] = new Cell(CellType.WALL);
                } else if ('a' <= id && id <= 'z') { //If goal
                    goals.put(goalId, new Goal(goalId, id, new Point(x, levelLine)));
                    map[levelLine][x] = new Cell(CellType.GOAL, goalId);
                    SAState.goals[levelLine][x] = id;
                    goalId++;
                } else if ('A' <= id && id <= 'Z') { //If boxes
                    boxes.put(boxId, new Box(boxId, id, colors.get(id), new Point(x, levelLine)));
                    map[levelLine][x] = new Cell(CellType.BOX,id);
                    boxId++;
                } else {
                    map[levelLine][x] = new Cell(CellType.EMPTY);
                }
            }
            levelLine++; //Y-coordinate
        }

        System.err.println("Number of goals: " + goals.size());
        System.err.println("Number of boxes: " + boxes.size());
        System.err.println("Rows: "+map.length+" Cols: "+map[0].length);

        Collections.sort(agents, (a1, a2) -> a1.getAgentId()-a2.getAgentId());

        level = new Level(map);
        level.setIntBoxes(boxes);
        level.setGoals(goals);
        level.setFutureAgents(futureAgents);

        findCorridors(); // First find the corridors in the map
        level.setCorridors(corridors);
        level.setCorridorLocks(corridorLocks);

        //printCorridorMap();

        createGraphFromMap();
        findCosts(agents.size() == 1);
        findCorridors();
        //getGoalTasks();  //Called from supervisor

        //printCorridorMap(cor);

//        sortAgents();

        // Tetst the locks:
        /*Point pointy = new Point(1,1);
        System.err.println("[Corridor] is it a corridor? " + level.isCorridor(pointy));
        if(level.isCorridor(pointy)){

            level.lockCorridor(pointy,1);

            System.err.println("[Corridor] is the corridor locked?" + level.isCorridorLocked(pointy));
            System.err.println("[Corridor] is the corridor locked?" + level.isCorridorLocked(new Point(2,1)));
            System.err.println("[Corridor] is the corridor locked?" + level.isCorridorLocked(new Point(1,2))); // Not a corridor..

            level.unlockCorridor(pointy,0);


            System.err.println("[Corridor] is the corridor locked?" + level.isCorridorLocked(pointy));
        }*/

        return agents;
    }

    private void findCosts(boolean singleAgent) {
        ArrayList<Node> allNodes = new ArrayList<>();

        for (ArrayList<Node> ls:graph) {
            allNodes.addAll(ls);
        }
        Iterator goalIterator = goals.entrySet().iterator();
        while(goalIterator.hasNext()){
            Map.Entry goalPair = (Map.Entry)goalIterator.next();
            Goal g = (Goal) goalPair.getValue();
            Node n = graph.get(g.point.y).get(g.point.x);
            GraphToolkit.dijkstra(allNodes,n);
            System.err.println("dikjstra: "+ n.getId());
        }

        if(singleAgent){
            Point agentPos = agents.get(0).getPosition();
            Node n = graph.get(agentPos.y).get(agentPos.x);
            GraphToolkit.dijkstra(allNodes,n);
        }
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
//        prioritizeGoals();
        PriorityQueue<GoalTask> goalTasks = new PriorityQueue<GoalTask>();
        HashMap<Integer, Box> boxesCopy = new HashMap<Integer,Box>(boxes);

        //Iterate through hashmap of goals
        Iterator goalIterator = goals.entrySet().iterator();
        int goalId = 0;

        while (goalIterator.hasNext()) {
            Map.Entry goalPair = (Map.Entry)goalIterator.next();
            Goal g = (Goal) goalPair.getValue();
            Iterator boxIterator = boxesCopy.entrySet().iterator();
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
//                boxIterator.remove(); // avoids a ConcurrentModificationException
            }
            if (boxBest != null){
                GoalTask goalTask = new GoalTask(boxBest.id,g.id,goalId, boxBest.color);
                int weight = findGoalWeight(goals.get(goalId));
                goalTask.setWeight(weight + cost);
//                goalTask.setWeight(goalTask.getWeight() + cost);
                goalTask.setCost(cost%500);
                goalTasks.offer(goalTask);
                boxesCopy.remove(boxBest.id);
                goalId++;
            }
        }

        System.err.println("goalTasks Printout:");
        PriorityQueue<GoalTask> printQueue = new PriorityQueue<>(goalTasks);
        while(printQueue.peek() != null){
            GoalTask gt = printQueue.poll();
            Goal g = goals.get(gt.getGoalId());
            Box b = boxes.get(gt.getBoxId());

            System.err.println("GoalTasks - Task " + gt.getTaskId() + " " +
                    "Goal: (" + g.id +","+ g.letter + ") BoxID: (" + b.id + "," + b.letter +")" +
                    "TaskWeight: " + gt.getWeight() + " TaskCost: " + gt.getCost());
        }

        if(agents.size() == 1){

        }

//        for (GoalTask gt: goalTasks) {
//            Goal g = goals.get(gt.getGoalId());
//            Box b = boxes.get(gt.getBoxId());
//            System.err.println("GoalTasks - Task " + gt.getTaskId() + " " +
//                    "Goal: (" + g.id +","+ g.letter + ") BoxID: (" + b.id + "," + b.letter +")" +
//                    "TaskWeight: " + gt.getWeight());
//        }
        return goalTasks;
    }

    private int findGoalWeight(Goal goal) {
//        if(isCorridor(goal.point)){
//           return 1000;
//        }
        return 0;
    }

    private void prioritizeGoals() {
        Iterator goalIterator = goals.entrySet().iterator();
        while(goalIterator.hasNext()){
            Map.Entry goalPair = (Map.Entry)goalIterator.next();
            Goal g = (Goal) goalPair.getValue();
            Point p = g.point;

        }
    }


    //Need a datastructure for corridors
    private void findCorridors() {
        char c;
        int id = 48; //Ascii character '0'


        for (int row = 1; row < walls.length-1; row++) {
            for (int col = 1; col < walls[0].length-1; col++) {

                //If cell is a wall, skip
                c = walls[row][col];
                if (c == '+') continue;

                    //If cell is empty, analyse
                else if (isCorridor(row, col)) {
                    if (assignId(row, col, id)) {
                        id++;
                    }
                }
//                isCorridorSum(i,j);
            }
        }

        corridorLocks = new Corridor[id-48];
//        corridorLocks = new Boolean[id-48];
        for (int i = 0; i < id-48; i++) {
            corridorLocks[i] = new Corridor();
        }
        printCorridorMap();
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

    private boolean isCorridor(Point p){ return isCorridor(p.y,p.x);}
    private boolean isCorridor(int row, int col) {

        //Vertical corridor
        if ((walls[row - 1][col] != '+' || walls[row + 1][col] != '+') && walls[row][col - 1] == '+' && walls[row][col + 1] == '+') {
            return true;
        }
        //Horizontal corridor
        else if (walls[row - 1][col] == '+' && walls[row + 1][col] == '+' && (walls[row][col - 1] != '+' || walls[row][col + 1] != '+')) {
            return true;
        }
        //Corners - assume that you can escape from a free cell that is not a dead end
        //Corner LD
        else if (walls[row][col - 1] == '+' && walls[row + 1][col] == '+' && walls[row - 1][col + 1] == '+')
            return true;
            //Corner RD
        else if (walls[row][col - 1] == '+' && walls[row - 1][col] == '+' && walls[row + 1][col + 1] == '+')
            return true;
            //Corner LU
        else if (walls[row][col + 1] == '+' && walls[row + 1][col] == '+' && walls[row - 1][col - 1] == '+')
            return true;
            //Corner RU
        else if (walls[row][col + 1] == '+' && walls[row - 1][col] == '+' && walls[row + 1][col - 1] == '+')
            return true;

        return false;
    }

    private boolean assignId(int row, int col, int id) {
        char c = (char) 0;
        if (walls[row - 1][col] > '+') {
            c = walls[row - 1][col];
        } else if (walls[row + 1][col] > '+') {
            c = walls[row + 1][col];
        } else if (walls[row][col - 1] > '+') {
            c = walls[row][col - 1];
        } else if (walls[row][col + 1] > '+') {
            c = walls[row][col + 1];
        }
        if (c > (char) 0) {
            walls[row][col] = c;
            cor[row][col] = Character.valueOf(c)-47; //-47 is a correction from ascii to integer
            corridors.put(new Point(row,col),Character.valueOf(c)-48);
            return false;
        } else {
            walls[row][col] = (char) id;
            cor[row][col] = id-47; // 48 i stedet? for 0-indeksering? :)
            return true;
        }
    }

    // Their value aee initially set to false
    public Corridor[] getCorridorLocks(){
        return corridorLocks;
    }

    public void printCorridorMap() {
        System.err.println("ai.Corridor map print out");
        String s = "";

        for (int row = 0; row < walls.length; row++) {
            s = "";
            if (walls[row][0] < ' ') //no more lines in map
                break;
            for (int col = 0; col < walls[0].length; col++) {
                if (walls[row][col] > ' ')
                    s = s + walls[row][col];
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
        System.err.println ("createGraphFromMap: Size of map is " + map[0].length + ", "+ map.length);

        //Make Nodes
        for (int y = 0; y < this.map.length; y++) {
            this.graph.add(new ArrayList<>());
            for (int x = 0; x < map[y].length; x++) {
                //System.err.println("Node of type: " + this.map[y][x].getType() + " at " + x + "," + y);
                Node n = new Node(new Point(x, y), this.map[y][x].getType());
                if (this.map[y][x].getType() == CellType.GOAL){
                    n.setId(this.map[y][x].getGoalId());
                }
                if(agents.size() == 1) //Used for single agent case!
                    if(agents.get(0).getPosition().x == x && agents.get(0).getPosition().y == y)
                        n.setId(99); //Unique for singleAgent

                graph.get(y).add(n); //Add to graph
            }
        }

        System.err.print("createGraphFromMap graph size: " + graph.size() + ", " + graph.get(0).size());

        //Make edges
        for (int y = 0; y < this.map.length; y++) {
            for (int x = 0; x < this.map[y].length; x++) {

                if (x + 1 < this.graph.get(y).size()) { //Right neighbor case
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