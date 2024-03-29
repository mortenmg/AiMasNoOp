package ai;

/**
 * Created by Philip on 12-04-2016.
 */
public class Cell {

    private int corridor = 0;     // Int to define if its part of a corridor
    private CellType type = CellType.EMPTY; // The cell can have a type default is empty;
    private Integer goalId;
    private boolean isFree;

    /**
     * We have a corridor provide a number
     */
    Cell(int corridor, CellType type) {
        this.corridor = corridor;
        this.type = type;
        if (type == CellType.EMPTY || type == CellType.GOAL){
            this.isFree = true;
        }else{
            this.isFree = false;
        }
    }

    /**
     * The real constructor
     */
    Cell(CellType type) {
        this.type = type;
        this.goalId = null;
        if ((type == CellType.EMPTY || type == CellType.GOAL)){
            this.isFree = true;
        }else{
            this.isFree = false;
        }
    }

    /**
     * Constructor for setting
     * @param type
     * @param goalId
     */
    public Cell(CellType type, int goalId) {
        this.type = type;
        this.goalId = goalId;
        if ((type == CellType.EMPTY || type == CellType.GOAL)){
            this.isFree = true;
        }else{
            this.isFree = false;
        }
    }

    @Override
    public String toString() {
        return "This cell is a special cell";
    }

    public CellType getType() {
        return type;
    }


    public Integer getGoalId() {
        return goalId;
    }
}

enum CellType {
    EMPTY,
    WALL,
    GOAL,
    BOX,
    AGENT
}