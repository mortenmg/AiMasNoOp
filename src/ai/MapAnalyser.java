package ai;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MapAnalyser {

    private String filePath;

    MapAnalyser(String mapPath){
        this.filePath = mapPath;
    }

    public ArrayList<ArrayList<Node>> nodes = new ArrayList<>();

    private char[][] map;
    private ArrayList<String> stringMap = new ArrayList<>();

    public void readMap() {

        String line = null;

        try {

            FileReader fileReader =  new FileReader(this.filePath);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int lineCount = 0;

            while ((line = bufferedReader.readLine()) != null) {
                this.stringMap.add(line);
            }

            int mapHeight  = this.stringMap.get(0).length();
            int mapWidth   = this.stringMap.size();
            map = new char[mapWidth][mapHeight];

            for(String ln: this.stringMap) {
                map[lineCount] = ln.toCharArray();
                lineCount++;
            }

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            //Handle this
        } catch (IOException ex) {
            //Handle this
        }
    }

    public Graph createGraphFromMap(){

        readMap();

        HashMap<String, Node> goals = new HashMap<>();
        HashMap<String, Node> boxes = new HashMap<>();

        //Make Nodes
        for (int i = 0; i < this.map.length; i++){
            this.nodes.add(new ArrayList<>());
            for(int j = 0; j < map[i].length; j++){
                Node n = new Node(new Point(j,i),charToType(this.map[i][j]));
                if(n.getType() == NodeType.GOAL) { //Add to goal collection
                    n.setId(UUID.randomUUID().toString());
                    goals.put(n.getId(), n);
                    //goalPoints.add(new Point(i,j));
                }
                if(n.getType() == NodeType.BOX){//Add to box collection
                    n.setId(UUID.randomUUID().toString());
                    boxes.put(n.getId(), n);
                }

                nodes.get(i).add(n); //Add to graph
            }
        }

        //Make edges
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[i].length; j++) {

                if(j+1 < this.nodes.get(i).size()){ //Right neighbor case
                    //if(j+1 < this.map[i].length){
                    this.nodes.get(i).get(j).addNeighbor(new Edge(nodes.get(i).get(j+1),nodes.get(i).get(j)));
                }
                if(j-1 >= 0){ //Left neighbor case
                    this.nodes.get(i).get(j).addNeighbor(new Edge(nodes.get(i).get(j-1),nodes.get(i).get(j)));
                }
                if(i+1 < this.nodes.size()) { //Check for list below - Below neighbor case
                    if (j < this.nodes.get(i + 1).size()) { //Check if list below is bigger then current index(x-axis)
                        this.nodes.get(i).get(j).addNeighbor(new Edge(nodes.get(i + 1).get(j), nodes.get(i).get(j)));
                    }
                }
                if(i - 1 >= 0) { //Above neighbor case
                    if (j < this.nodes.get(i - 1).size()) {
                        this.nodes.get(i).get(j).addNeighbor(new Edge(nodes.get(i - 1).get(j), nodes.get(i).get(j)));

                    }
                }
            }
        }

        HashMap<Point,Node> g = new HashMap<>();

        for(ArrayList<Node> nodeList: this.nodes){
            for(Node n: nodeList){
                g.put(n.getCoord(),n);
            }
        }

        Graph graph = new Graph(g, boxes, goals);

        return graph;

    }


    public NodeType charToType(char c){
        if ('0' <= c && c <= '9'){
            return NodeType.AGENT;
        }
        else if (c == ' ') {
            return NodeType.EMPTY;
        }
        else if (c == '+') {
            return NodeType.WALL;
        }
        else if ('a' <= c && c <= 'z' ){
            return NodeType.GOAL;
        }
        else if ('A' <= c && c <= 'Z'){
            return NodeType.BOX;
        }
        return NodeType.EMPTY;
    }



}
