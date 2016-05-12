package ai;

/**
 * Created by Philip on 12-04-2016.
 */
public class Cell {

    private int corridor = 0;     // Int to define if its part of a corridor
    private CellType type = CellType.EMPTY; // The cell can have a type default is empty;
    private Integer boxId;


    /**
     * We have a corridor provide a number
     */
    Cell(int corridor, CellType type) {
        this.corridor = corridor;
        this.type = type;
    }

    /**
     * The real constructor
     */
    Cell(CellType type) {
        this.type = type;
        this.boxId = null;
    }

    /**
     * Constructor for setting
     * @param type
     * @param boxId
     */
    public Cell(CellType type, int boxId) {
        this.type = type;
        this.boxId = boxId;
    }

    /**
     * @return boolean
     * @author Rasmus
     */
    public boolean hasBox() {
        if (type == CellType.EMPTY && boxId!=null)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "This cell is a special cell";
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }
}

enum CellType {
    EMPTY,
    WALL,
    GOAL,
    BOX
}