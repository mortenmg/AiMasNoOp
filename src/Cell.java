import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Philip on 12-04-2016.
 */
public class Cell {

    private ArrayList<Point> cells; // Do we need to know this
    private boolean locked;

    Cell(Point cell){
        cells = new ArrayList<>();
        cells.add(cell);
    }

    public void lock(){
        locked = true;
    }

    private void unlock(){
        locked = false;
    }


}
