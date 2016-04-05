/**
 * Created by hvingelby on 4/5/16.
 */
public class Supervisor {

    public static void main( String[] args ) {

    }

    private static Supervisor ourInstance = new Supervisor();

    public static Supervisor getInstance() {
        return ourInstance;
    }

    private Supervisor() {
        Preprocessor preprocessor;
    }
}
