import java.awt.*;

/**
 * Created by Philip on 12-04-2016.
 */
public class Cell {

    private char    cell = '0';         // Empty Cell / Wall
    private char    object = '0';       // Agent / Box
    private int     corridor = 0;     // Int to define if its part of a corridor

    /** The cell is either a wall / an empty cell(set to 0)
     *  The object is either an Agent or an Box.
     * */
    Cell(char cell, char object){
        this.cell = cell;
        this.object = object;
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

    public void setObject(char object){
        this.object = object;
    }

    public char getCell(){
        return this.cell;
    }

    public char getObject(){
        return this.object;
    }

    /** Returns '0' if there is no objects otherwise it returns a char according to the object on it*/
    public char isEmpty(){
        if(object != '0'){ return object;}
        if(cell != '0' ){ return cell;}
        return '0';
    }
}
