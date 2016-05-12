package ai;

/**
 * Created by Philip on 12-04-2016.
 */
public class Cell {

    private char    cell = '0';         // Empty ai.Cell / Wall
    private char    agent = '0';       // ai.Agent / Box
    private char    box = '0';
    //private Box box;
    private int corridor = 0;     // Int to define if its part of a corridor

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    private CellType type = CellType.EMPTY; // The cell can have a type default is empty;

    /** The cell is either a wall / an empty cell(set to 0)
     *  The agent is either an ai.Agent or an Box.
     * */
    Cell(char cell, char agent){
        this.cell = cell;
        this.agent = agent;
    }

    /** We have an agent inside a corridor */
    Cell(char cell, int corridor){
        this.cell = cell;
    }

    /** We have a corridor provide a number */
    Cell(int corridor){
        this.corridor = corridor;
    }

    /**
     * The real constructor
     */
    Cell(CellType type) {
        this.type = type;
    }

    public void setCell(char cell){
        this.cell = cell;
    }

    public void setAgent(char agent){
        this.agent = agent;
    }

    public void setBox(char box){
        this.box = box;
    }

    public char getCell(){
        return this.cell;
    }

    public char getAgent(){
        return this.agent;
    }

    public char getBox(){ return this.box; }

    /**
     * @author Rasmus
     * @return
     */
    public boolean hasBox() {
        return cell != '+' && box != '0';
    }


    /** Returns '0' if there is no objects otherwise it returns a char according to the agent on it*/
    public char isEmpty(){
        if(agent != '0'){ return agent;}
        if(cell != '0' ){ return cell;}
        return '0';
    }

    @Override
    public String toString() {
        return "This cell is a special cell";
    }
}

enum CellType{
    EMPTY,
    WALL,
    GOAL
}