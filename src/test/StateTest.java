package test;

import ai.State;
import ai.Box;
import org.junit.Test;

import java.awt.*;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by hvingelby on 5/11/16.
 */
public class StateTest {
    @Test
    public void testEquality() throws Exception {
        State s1 = new State(null);
        s1.agentCol = 1;
        s1.agentRow = 1;

        State s2 = new State(null);
        s2.agentCol = 1;
        s2.agentRow = 1;

        assertEquals(s1, s2);

        HashMap<Integer, Box> boxes = new HashMap<>();
        Box b1 = new Box(0, 'c', "green", new Point(1,2));
        boxes.put(0, b1);

        HashMap<Integer, Box> boxes2 = new HashMap<>();
        Box b2 = new Box(0, 'c', "green", new Point(1,2));
        boxes2.put(0, b2);

        s1.setBoxes(boxes);
        s2.setBoxes(boxes2);

        assertEquals(s1,s2);
    }

}