package ai;

import java.awt.*;

/**
 * Created by Mathias on 09-05-2016.
 */
class Box {

    int id;
    char letter;
    String color;
    Point point;

    public Box(int id, char letter, String color, Point location){
        this.id = id;
        this.letter = letter;
        this.color = color;
        this.point = location;
    }

}
