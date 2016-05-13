package ai;

import java.awt.*;

/**
 * Created by Mathias on 09-05-2016.
 */
public class Box {

    int id;
    char letter;
    String color;
    Point location;

    public Box(int id, char letter, String color, Point location){
        this.id = id;
        this.letter = letter;
        this.color = color;
        this.location = location;
    }

    /**
     * Copy constructor
     */
    public Box(Box b){
        this.id = b.id;
        this.letter = b.letter;
        this.color = b.color;
        this.location = new Point(b.location.x,b.location.y);
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
