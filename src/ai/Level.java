package ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mathias on 09-05-2016.
 */


public class Level {

    //This is the static map, contains only static map information.
    private final Cell[][] map;

    private HashMap<Integer,Goal> goals = new HashMap<>();

    //This data structure is updated when a box is moved. e.i. a push or pull action is send to the server.
    private HashMap<Point,Box> boxes = new HashMap<>();
    private HashMap<Integer,Box> intBoxes = new HashMap<>();

    private ArrayList<Agent> agents = new ArrayList<>();

    private ArrayList<ArrayList<Node>> graph = new ArrayList<>();

    Level(Cell[][] map) {
        this.map = map;
    }

    public boolean isMoveValidForAgent(Command c, Agent a) {
        boolean isValid = false;

        if(c != null) {
            switch (c.actType) {
                case Move:
                    int newAgentRow = a.getPosition().y + dirToRowChange(c.dir1);
                    int newAgentCol = a.getPosition().x + dirToColChange(c.dir1);

                    System.err.println("Agent Row: "+a.getPosition().y + " Agent Col: "+ a.getPosition().x);
                    System.err.println("New Row: "+newAgentRow + " New Col: "+ newAgentCol);
                    if (isCellFree(newAgentCol, newAgentRow)) {
                        isValid = true;
                        a.setPosition(new Point(newAgentCol, newAgentRow)); //Update posistion
                    }
                    break;

                case Pull:
                    int boxRow = a.getPosition().y + dirToRowChange(c.dir2); //Supposed box position
                    int boxCol = a.getPosition().x + dirToColChange(c.dir2);

                    int newAgentRowPull = a.getPosition().y + dirToRowChange(c.dir1);
                    int newAgentColPull = a.getPosition().x + dirToColChange(c.dir1);

                    Point boxPoint = new Point(boxCol, boxRow);
                    //System.err.println(boxPoint);
                    System.err.println(boxes.keySet());
                    if (boxes.containsKey(boxPoint)) { //Check if there is box to move
                        System.err.println("Box exists");
                        Box b1 = boxes.get(boxPoint); //Get box
                        if (b1.color == a.getColor()) { //Check if agent can move
                            System.err.println("Agent can move the box");
                            if (isCellFree(newAgentColPull, newAgentRowPull)) { //Is the new position of agent valid
                                System.err.println("The new position is okay");
                                isValid = true;
                                boxes.remove(boxPoint);
                                Point newBoxLocation = new Point(a.getPosition().x, a.getPosition().y);
                                b1.setLocation(newBoxLocation);
                                boxes.put(newBoxLocation, b1); // update box position
                                a.setPosition(new Point(newAgentColPull, newAgentRowPull)); //update agent position
                            }
                        }
                    }
                    break;

                case Push:
                    int boxRowPush = a.getPosition().y + dirToRowChange(c.dir1);
                    int boxColPush = a.getPosition().x + dirToColChange(c.dir1);



                    Point boxPointPush = new Point(boxColPush, boxRowPush);

                    if (boxes.containsKey(boxPointPush)) { //Check if there is box to move
                        System.err.println("box to move!");
                        Box b2 = boxes.get(boxPointPush); //Get box

                        int newBoxRow = b2.location.y + dirToRowChange(c.dir2);
                        int newBoxCol = b2.location.x + dirToColChange(c.dir2);

                        if (b2.color == a.getColor()) { //Check if agent can move
                            System.err.println("color is the same");
                            if (isCellFree(newBoxCol, newBoxRow)) {
                                isValid = true;
                                a.setPosition(boxPointPush); //Update agent position to where box were
                                boxes.remove(boxPointPush);
                                Point newBoxPos = new Point(newBoxCol, newBoxRow);
                                b2.setLocation(newBoxPos);
                                boxes.put(newBoxPos, b2);
                            }
                        }
                    }
                    break;
            }
        }
        return isValid;
    }


    public boolean boxAtPosition(Point p){
        return boxes.containsKey(p);
    }

    private int dirToRowChange( Command.dir d ) {
        return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
    }

    private int dirToColChange( Command.dir d ) {
        return ( d == Command.dir.E ? 1 : ( d == Command.dir.W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
    }

    /**
     * Checks for wall, agent and box at x,y
     * @param x
     * @param y
     * @return
     */
    public boolean isCellFree(int x, int y){
        Point p = new Point(x,y);
        for(Agent a: agents){
            if(a.getPosition().equals(p))
                return false;
        }
        if(map[y][x].isFree() && !boxes.containsKey(p)){
            return true;
        }else{
            return false;
        }
    }

    public void setGoals(HashMap<Integer,Goal> goals) { this.goals = goals; }

    public Goal getGoalWithId(int goalId) {
        return goals.get(goalId);
    }

    /**
     * copy of boxes. So that level have the actual state of the boxes
     * @return copy of boxes
     */
    public HashMap<Integer, Box> getBoxes() {
        HashMap<Integer,Box> tmpBoxes = new HashMap<>();

        //for(int boxId : intBoxes)

        for(Box b: intBoxes.values()){
            tmpBoxes.put(b.id, new Box(b));
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
        System.err.println("Graph size = " + graph.size() + ", " + graph.get(0).size());

        System.err.println("x:" + x + "y: " + y + " goalId: " + goalId );
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
}
