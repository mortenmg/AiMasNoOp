import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Supervisor {
    private List< Agent > agents = new ArrayList<>();
    private BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );

    public static void main( String[] args ) {
        System.err.println( "Supervisor is running!" );

        // The solution is a list of "nodes" or states
        //LinkedList< Node > solution = null;
        //Command.every;
        // Retrieving the supervisor singleton
        Supervisor.getInstance().start();

        // Ask the supervisor to start sending the steps to server
        // while ( s.update() ) ;


    }

    /**
     * Starting the agents.
     */
    private void start() {
        for (Agent agent: agents) {
            agent.run();
        }

        while (sendActions());
    }

    private boolean sendActions() {
        String jointAction = "[";

        // In this loop we are sending the actions that is waiting to be sent.
        for ( int i = 0; i < agents.size() - 1; i++ )
            jointAction += agents.get( i ).act() + ",";

        jointAction += agents.get( agents.size() - 1 ).act() + "]";

        // Place message in buffer
        System.out.println( jointAction );

        // Flush buffer
        System.out.flush();

        // Disregard these for now, but read or the server stalls when its output buffer gets filled!
        String percepts = null;
        try {
            percepts = serverMessages.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ( percepts == null )
            return false;

        return true;
    }

    private static Supervisor ourInstance = new Supervisor();

    public static Supervisor getInstance() {
        return ourInstance;
    }

    /**
     * The constructor of the Supervisor class.
     * This is private due to the singleton
     * pattern.
     */
    private Supervisor() {
        Preprocessor p = new Preprocessor(serverMessages);

        try {
            agents = p.readMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}