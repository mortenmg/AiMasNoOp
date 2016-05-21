package ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hvingelby on 5/21/16.
 */
public class StartDebugger {
    public static void main(String[] args) throws IOException {
        Preprocessor p = new Preprocessor();
        p.receiveMapFromFile("/home/hvingelby/IdeaProjects/AiMasNoOp/levels/test1.lvl");


        System.err.println();
        System.err.println("+--------------------+");
        System.err.println("+    DEBUG START     +");
        System.err.println("+--------------------+");


        Supervisor.getInstance().setAgents(p.getAgents());
        Supervisor.getInstance().setLevel(p.getLevel());
        Supervisor.getInstance().setGoalTasks(p.getGoalTasks());
        Supervisor.getInstance().setDebugging(true);

        Supervisor.getInstance().start();
    }
}
