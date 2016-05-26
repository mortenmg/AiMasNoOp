package ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mathias on 09-05-2016.
 */

public class Level {

    private final Cell[][] map;                                 //This is the static map, contains only static map information.
    private HashMap<Integer,Goal> goals = new HashMap<>();      //Gets updated when a valid move is sent to server
    private HashMap<Point,Box> boxes = new HashMap<>();         //Gets updated when a valid move is sent to server
    private HashMap<Integer,Box> intBoxes = new HashMap<>();    //Copy of the boxes hashmap, but has the boxId's as index
    private ArrayList<ArrayList<Node>> graph = new ArrayList<>();

    private HashMap<Point,Box> futureBoxes = new HashMap<>();
    private HashMap<Point, Agent> futureAgents = new HashMap<>(); // This has to be the shallow agents

    Level(Cell[][] map) {
        this.map = map;
    }

    /**
     * This method determines if a move is valid.
     * If there is something in the way for a
     * move, then we will return the cell
     * that is blocking.
     *
     * Old name: isMoveValidForAgent
     * New name: conflictingCellFromMove
     *
     * @param c The command that is being applied.
     * @param a The agent that is performing an action
     * @return
     */
    public Point conflictingCellFromMove(Command c, MAgent a) {
        Point conflictingCell = new Point(-1, -1);

        if(c != null) {
            switch (c.actType) {
                case Move:
                    int newAgentRow = a.getPosition().y + dirToRowChange(c.dir1);
                    int newAgentCol = a.getPosition().x + dirToColChange(c.dir1);

                    if (isCellFree(newAgentCol, newAgentRow)) {
                        conflictingCell = null;

                        updateFutureAgent(a, new Point(newAgentCol, newAgentRow));

                    } else {
                        conflictingCell = new Point(newAgentCol, newAgentRow);
                    }
                    break;

                case Pull:
                    int boxRow = a.getPosition().y + dirToRowChange(c.dir2); //Supposed box position
                    int boxCol = a.getPosition().x + dirToColChange(c.dir2);

                    int newAgentRowPull = a.getPosition().y + dirToRowChange(c.dir1);
                    int newAgentColPull = a.getPosition().x + dirToColChange(c.dir1);

                    Point boxPoint = new Point(boxCol, boxRow);
                    if (boxes.containsKey(boxPoint) && futureBoxes.containsKey(boxPoint)) { // Check if there is box to move
                        Box b1 = boxes.get(boxPoint); //Get box
                        if (b1.color == a.getColor()) { //Check if agent can move
                            if (isCellFree(newAgentColPull, newAgentRowPull)) { //Is the new position of agent valid
                                conflictingCell = null;

                                Point newBoxLocation = new Point(a.getPosition().x, a.getPosition().y);
                                updateFutureBox(b1, newBoxLocation);

                                Point newPos = new Point(newAgentColPull, newAgentRowPull);
                                updateFutureAgent(a, newPos);
                            } else { // The new position is occupied
                                conflictingCell = new Point(newAgentColPull, newAgentRowPull);
                            }
                        }
                    }
                    break;

                case Push:
                    int boxRowPush = a.getPosition().y + dirToRowChange(c.dir1);
                    int boxColPush = a.getPosition().x + dirToColChange(c.dir1);

                    Point boxPointPush = new Point(boxColPush, boxRowPush);
                    if (boxes.containsKey(boxPointPush) && futureBoxes.containsKey(boxPointPush)) { //Check if there is box to move
                        Box b2 = boxes.get(boxPointPush); //Get box

                        int newBoxRow = b2.location.y + dirToRowChange(c.dir2);
                        int newBoxCol = b2.location.x + dirToColChange(c.dir2);

                        if (b2.color == a.getColor()) { //Check if agent can move the box
                            if (isCellFree(newBoxCol, newBoxRow)) {
                                conflictingCell = null;

                                updateFutureAgent(a, boxPointPush); //Update agent position to where box were

                                Point newBoxPos = new Point(newBoxCol, newBoxRow);
                                updateFutureBox(b2, newBoxPos);
                            } else { // The new position is occupied
                                conflictingCell = new Point(newBoxCol, newBoxRow);
                            }
                        }
                    }
                    break;
            }
        }
        return conflictingCell;
    }

    private int dirToRowChange( Command.dir d ) {
        return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange( Command.dir d ) {
        return ( d == Command.dir.E ? 1 : ( d == Command.dir.W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
    }

    private void updateFutureAgent(MAgent agent, Point newPos) {
        Agent futureAgent = futureAgents.remove(agent.getPosition());
        futureAgent.setPosition(newPos);
        futureAgents.put(newPos, futureAgent);
    }

    private void updateFutureBox(Box box, Point newPos) {
        Box futureBox = futureBoxes.remove(box.location);
        futureBox.setLocation(newPos);
        futureBoxes.put(newPos, futureBox);
    }

    /**
     * Updates the level so that the boxes and agents of the level
     * are set to the future boxes and future agents.
     */
    public void updateToFuture() {
        setBoxes(this.futureBoxes);
        this.futureBoxes.clear();

        for (Agent a: futureAgents.values()) {
            MAgent agent = Supervisor.getInstance().getAgents().get(a.getAgentId());
            agent.setPosition(a.getPosition());
        }
    }

    /**
     * This method will prepare the level for agents testing their actions.
     * This means it will create a list of future boxes, based on the
     * current boxes, and then when an agents move is valid, it will
     * update the list of future boxes.
     */
    public void prepareNextLevel(){
        this.futureBoxes.clear();
        this.futureBoxes = getBoxesWithPointKeys();
    }

    /**
     * Checks for walls, current agents, future agents, current boxes
     * and future boxes at the position p.
     *
     * @param p
     * @return
     */
    private boolean isCellFree(Point p){
        // Check the agents current position
        for(MAgent a: Supervisor.getInstance().getAgents()){
            if(a.getPosition().equals(p))
                return false;
        }

        // Checks the agents future position
        for (Agent a : this.futureAgents.values()){
            if (a.getPosition().equals(p))
                return false;
        }

        if(map[p.y][p.x].isFree() && !boxes.containsKey(p) && !futureBoxes.containsKey(p)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Just a overloading method.
     * @param x
     * @param y
     * @return
     */
    private boolean isCellFree(int x, int y) {
        return isCellFree(new Point(x,y));
    }

    public void setGoals(HashMap<Integer,Goal> goals) { this.goals = goals; }

    public Goal getGoalWithId(int goalId) {
        return goals.get(goalId);
    }

    public HashMap<Integer,Goal> getGoals(){return goals;}

    /**
     * copy of boxes. So that level have the actual state of the boxes
     * @return copy of boxes
     */
    public HashMap<Integer, Box> getBoxes() {
        HashMap<Integer,Box> tmpBoxes = new HashMap<>();
        for(Box b: intBoxes.values()){
            tmpBoxes.put(b.id, new Box(b));
        }
        return tmpBoxes;
    }

    public HashMap<Point, Box> getFutureBoxes() {
        HashMap<Point,Box> tmpBoxes = new HashMap<>();
        for(Box b: futureBoxes.values()){
            tmpBoxes.put(new Point(b.location), new Box(b));
        }
        return tmpBoxes;
    }

    public HashMap<Point, Box> getBoxesWithPointKeys() {
        HashMap<Point,Box> tmpBoxes = new HashMap<>();
        for(Box b: intBoxes.values()){
            tmpBoxes.put(new Point(b.location), new Box(b));
        }
        return tmpBoxes;
    }

    public Box getBoxWithId(Integer i) {
        return this.intBoxes.get(i);
    }

    public void setGraph(ArrayList<ArrayList<Node>> graph) {
        this.graph = graph;
    }

    public int getCostForCoordinateWithGoal(int x, int y, int goalId){
        if(graph.size() > y){
            if(graph.get(y).size() > y){
                Node n = graph.get(y).get(x);
                return n.getGoalPathsCost(goalId);
            }
        }
        return Integer.MAX_VALUE;
    }

    public Cell[][] getMap() {
        return map;
    }

    public void setIntBoxes(HashMap<Integer, Box> intBoxes) {
        this.intBoxes = intBoxes;
        for(Box b: this.intBoxes.values()){
            this.boxes.put(new Point(b.location),b);
        }
    }

    private void setBoxes(HashMap<Point, Box> boxes) {
        this.boxes = getFutureBoxes();
        for(Box b: this.boxes.values()) {
            this.intBoxes.replace(b.id, b);
        }
    }

    public void setFutureAgents(HashMap<Point, Agent> futureAgents) {
        this.futureAgents = futureAgents;
    }

    public Box getBoxAtPosition(Point p){
        return boxes.get(p);
    }
}
