package ai;

public class Edge {

    private final Node to;
    private final Node from;
    private final int weight;


    public Edge( Node to, Node from) {
        this.to = to;
        this.from = from;
        this.weight = setWeight();
    }

    private int setWeight(){
        if(to.getType() == CellType.WALL){
            return 1000;
        }else if(to.getType() == CellType.BOX){
            return 500;
        }else{ //To.getType() == .EMPTY
            return 1;
        }
    }

    public Node getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }


}
