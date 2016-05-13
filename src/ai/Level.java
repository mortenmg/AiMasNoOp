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

        switch (c.actType){
            case Move:
                int newAgentRow = a.getPosition().y + dirToRowChange(c.dir1);
                int newAgentCol = a.getPosition().x + dirToRowChange(c.dir1);
                if(isCellFree(newAgentCol, newAgentRow)){
                    isValid = true;
                    a.setPosition(new Point(newAgentCol,newAgentRow)); //Update posistion
                }
                break;

            case Pull:
                int boxRow = a.getPosition().y + dirToRowChange(c.dir2); //Supposed box position
                int boxCol = a.getPosition().x + dirToColChange(c.dir2);

                int newAgentRowPull = a.getPosition().y + dirToRowChange(c.dir1);
                int newAgentColPull = a.getPosition().x + dirToColChange(c.dir1);

                Point boxPoint = new Point(boxCol, boxRow);
                if (boxes.containsKey(boxPoint)) { //Check if there is box to move
                    Box b1 = boxes.get(boxPoint); //Get box
                    if (b1.color == a.getColor()) { //Check if agent can move
                        if(isCellFree(newAgentColPull, newAgentRowPull)){ //Is the new position of agent valid
                            isValid = true;
                            a.setPosition(new Point(newAgentColPull, newAgentRowPull)); //update agent position
                            boxes.remove(boxPoint);
                            Point newBoxLocation = new Point(a.getPosition().x,a.getPosition().y);
                            b1.setLocation(newBoxLocation);
                            boxes.put(newBoxLocation,b1); // update box position
                        }
                    }
                }
                break;

            case Push:
                int boxRowPush = a.getPosition().y + dirToRowChange(c.dir1);
                int boxColPush = a.getPosition().x + dirToColChange(c.dir1);

                int newBoxRow = a.getPosition().y + dirToRowChange(c.dir2);
                int newBoxCol = a.getPosition().x + dirToColChange(c.dir2);

                Point boxPointPush = new Point(boxColPush, boxRowPush);
                Point newBoxPos = new Point(newBoxCol, newBoxRow);
                if (boxes.containsKey(boxPointPush)) { //Check if there is box to move
                    Box b2 = boxes.get(boxPointPush); //Get box
                    if (b2.color == a.getColor()) { //Check if agent can move
                        if(isCellFree(newBoxCol, newBoxRow)){
                            isValid = true;
                            a.setPosition(boxPointPush); //Update agent position to where box were
                            boxes.remove(boxPointPush);
                            b2.setLocation(newBoxPos);
                            boxes.put(newBoxPos, b2);
                        }
                    }
                }
                break;
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
        if(map[x][y].isFree() && !boxes.containsKey(p)){
            return true;
        }else{
            return false;
        }
    }

    public void setGoals(HashMap<Integer,Goal> goals) { this.goals = goals; }

    public void setBoxes(HashMap<Point,Box> boxes) { this.boxes = boxes; }

    /**
     * copy of boxes. So that level have the actual state of the boxes
     * @return copy of boxes
     */
    public HashMap<Integer, Box> getBoxes() {
        HashMap<Integer,Box> tmpBoxes = new HashMap<>();

        for(Box b: boxes.values()){
            tmpBoxes.put(b.id, new Box(b));
        }
        return tmpBoxes;
    }

    public Box getBoxWithId(Integer i) {
        return this.boxes.get(i);
    }

    public void setGraph(ArrayList<ArrayList<Node>> graph) {
        this.graph = graph;
    }

    public int getCostForCoordinateWithGoal(int x, int y, int goalId){
        System.err.println("Graph size = " + graph.size() + ", " + graph.get(0).size());
        if(graph.size() > x){
            if(graph.get(x).size() > y){
                return graph.get(x).get(y).getGoalPathsCost(goalId);
            }
        }
        return Integer.MAX_VALUE;
    }

    public Cell[][] getMap() {
        return map;
    }

    public void setIntBoxes(HashMap<Integer, Box> intBoxes) {
        this.intBoxes = intBoxes;
    }
}
