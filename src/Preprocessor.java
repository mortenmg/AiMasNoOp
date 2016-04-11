import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hvingelby on 4/5/16.
 */
public class Preprocessor {
    private BufferedReader serverMessages;

    public Preprocessor(BufferedReader serverMessages) {
        this.serverMessages = serverMessages;
    }

    public List<Agent> readMap() throws IOException {
        Map< Character, String > colors = new HashMap< Character, String >();
        String line, color;

        // Read lines specifying colors
        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            color = line.split( ":" )[0];

            for ( String id : line.split( ":" )[1].split( "," ) )
                colors.put( id.charAt( 0 ), color );
        }

        List< Agent > agents = new ArrayList< Agent >();

        // Read lines specifying level layout
        while ( !line.equals( "" ) ) {
            for ( int i = 0; i < line.length(); i++ ) {
                char id = line.charAt( i );
                if ( '0' <= id && id <= '9' )
                    agents.add( new Agent( id, colors.get( id ) ) );
            }

            line = serverMessages.readLine();

        }

        return agents;
    }
}
