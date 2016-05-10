package ai;

public class Edge implements Cloneable{

    public Object clone() throws CloneNotSupportedException{
        Edge clone = (Edge) super.clone();
        clone.to = (Node)clone.to.clone();
        clone.from = (Node)clone.from.clone();

        return clone;
    }

    private Node to;
    private Node from;
    private final int weight;


    public Edge( Node to, Node from) {
        this.to = to;
        this.from = from;
        this.weight = setWeight();
    }


    private int setWeight(){
        if(to.getType() == NodeType.WALL){
            return 1000;
        }else if(to.getType() == NodeType.BOX){
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
