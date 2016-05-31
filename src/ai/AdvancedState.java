package ai;

/**
 * Created by hvingelby on 5/27/16.
 */
public class AdvancedState extends TestState{
    private int g;
    private Task task;

    public AdvancedState(AdvancedState parent) {
        super(parent);
        if ( parent == null ) {
            g = 0;
        } else {
            g = parent.g() + 1;
        }

    }

    private int g() {
        return g;
    }

    @Override
    public boolean isCellFree(int row, int col) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
