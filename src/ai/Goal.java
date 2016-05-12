package ai;

import java.awt.*;

/**
 * Created by hvingelby on 5/9/16.
 */
public class Goal {

    int id;
    char letter;
    Point point;

    public Goal(int id, char letter, Point location){
        this.id = id;
        this.letter = letter;
        this.point = location;
    }
}
