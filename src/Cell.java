/**
 * Created by Philip on 12-04-2016.
 */
public class Cell {

    private char    cell = '0';         // Empty Cell / Wall
    private char    agent = '0';       // Agent / Box
    private char    box = '0';
    private int     corridor = 0;     // Int to define if its part of a corridor

    /** The cell is either a wall / an empty cell(set to 0)
     *  The agent is either an Agent or an Box.
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

    /** Returns '0' if there is no objects otherwise it returns a char according to the agent on it*/
    public char isEmpty(){
        if(agent != '0'){ return agent;}
        if(cell != '0' ){ return cell;}
        return '0';
    }
}
