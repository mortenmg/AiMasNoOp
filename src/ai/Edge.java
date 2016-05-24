package ai;

public class Edge {

    private final Node to;
    private final Node from;

    public Edge( Node to, Node from) {
        this.to = to;
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public int getWeight() {
        if(to.getType() == CellType.WALL){
            return 5000;
        }else if(to.getType() == CellType.BOX){
            return 100;
        }else if(to.getType() == CellType.GOAL){
            return 500;
        }else{ //To.getType() == .EMPTY
            return 1;
        }
    }


}
