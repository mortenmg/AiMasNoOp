package ai;

import sun.rmi.server.InactiveGroupException;

import java.awt.*;
import java.util.*;



// Arrays are indexed from the top-left of the level, with first index being row and second being column.
// Row 0: (0,0) (0,1) (0,2) (0,3) ...
// Row 1: (1,0) (1,1) (1,2) (1,3) ...
// Row 2: (2,0) (2,1) (2,2) (2,3) ...
// ...


public class SAState {

    private static Random rnd = new Random( 1 );
    public static int MAX_ROW = 70;
    public static int MAX_COLUMN = 70;

    public int agentRow;
    public int agentCol;

    private HashMap<Integer,Point> heuristicBoxes_Int = new HashMap<>();
    private HashMap<Point,Integer> heuristicBoxes_Point = new HashMap<>();

    public static boolean[][] walls;// = new boolean[MAX_ROW][MAX_COLUMN];
    public char[][] boxes = new char[MAX_ROW][MAX_COLUMN];
    public static char[][] goals;// = new char[MAX_ROW][MAX_COLUMN];

    public SAState parent;
    public Command action;

    private int g;

    public static ArrayList<GoalTask> goalTasks;

    public void setHeuristicBoxes(HashMap<Integer, Point> hBoxes) {
        //this.heuristicBoxes_Int = hBoxes;
        this.heuristicBoxes_Point = new HashMap<>();
        this.heuristicBoxes_Int = new HashMap<>();
        for(int key: hBoxes.keySet()){
            this.heuristicBoxes_Point.put(hBoxes.get(key),key);
            this.heuristicBoxes_Int.put(key,hBoxes.get(key));
        }
    }



    private void updateBoxesAt(int row, int col, int newRow, int newCol){
        int id = this.heuristicBoxes_Point.get(new Point(col,row));
        this.heuristicBoxes_Point.remove(new Point(col,row));
        this.heuristicBoxes_Point.put(new Point(newCol,newRow),id);

        heuristicBoxes_Int.put(id, new Point(newCol,newRow));
    }

    public Point getBoxWithIdPos(int id){
        return heuristicBoxes_Int.get(id);
    }
    public int getBoxIdFromPos(Point position){
        if(heuristicBoxes_Point.containsKey(position))
            return heuristicBoxes_Point.get(position);

        return -1;

    }

    public HashMap<Integer, Point> getHeuristicBoxes() {
        return heuristicBoxes_Int;
    }

    public SAState(SAState parent ) {
        this.parent = parent;
        if ( parent == null ) {
            g = 0;
        } else {
            g = parent.g() + 1;
        }
    }

    public int g() {
        return g;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean isGoalState() {

        for(GoalTask goalTask: this.goalTasks){
            Goal g = Supervisor.getInstance().getLevel().getGoalWithId(goalTask.getGoalId());
            Point boxPos = heuristicBoxes_Int.get(goalTask.getBoxId());
            if(!g.point.equals(boxPos)){
                return false;
            }
        }
        return true;
        /*
        for ( int row = 0; row < MAX_ROW; row++ ) {
            for ( int col = 0; col < MAX_COLUMN; col++ ) {
                char g = goals[row][col];
                char b = Character.toLowerCase( boxes[row][col] );
                if ( g > 0 && b != g) {
                    return false;
                }
            }
        }
        return true;
        */
    }

    public ArrayList< SAState > getExpandedNodes() {
        ArrayList< SAState > expandedNodes = new ArrayList< SAState >( Command.every.length );
        for ( Command c : Command.every ) {
            // Determine applicability of action
            int newAgentRow = this.agentRow + dirToRowChange( c.dir1 );
            int newAgentCol = this.agentCol + dirToColChange( c.dir1 );

            if ( c.actType == Command.type.Move ) {
                // Check if there's a wall or box on the cell to which the agent is moving
                if ( cellIsFree( newAgentRow, newAgentCol ) ) {
                    SAState n = this.ChildNode();
                    n.action = c;
                    n.agentRow = newAgentRow;
                    n.agentCol = newAgentCol;
                    //System.err.println("Expanded with move");
                    expandedNodes.add( n );
                }
            } else if ( c.actType == Command.type.Push ) {
                // Make sure that there's actually a box to move
                if ( boxAt( newAgentRow, newAgentCol ) ) {
                    int newBoxRow = newAgentRow + dirToRowChange( c.dir2 );
                    int newBoxCol = newAgentCol + dirToColChange( c.dir2 );
                    // .. and that new cell of box is free
                    if ( cellIsFree( newBoxRow, newBoxCol ) ) {
                        SAState n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[newBoxRow][newBoxCol] = this.boxes[newAgentRow][newAgentCol];
                        n.boxes[newAgentRow][newAgentCol] = 0;
                        if(heuristicBoxes_Point.containsKey(new Point(newAgentCol,newAgentRow))){
                            n.updateBoxesAt(newAgentRow,newAgentCol,newBoxRow,newBoxCol);
                        }
                        //System.err.println("Expanded with push");
                        expandedNodes.add( n );
                    }
                }
            } else if ( c.actType == Command.type.Pull ) {
                // Cell is free where agent is going
                if ( cellIsFree( newAgentRow, newAgentCol ) ) {
                    int boxRow = this.agentRow + dirToRowChange( c.dir2 );
                    int boxCol = this.agentCol + dirToColChange( c.dir2 );
                    // .. and there's a box in "dir2" of the agent
                    if ( boxAt( boxRow, boxCol ) ) {
                        SAState n = this.ChildNode();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxes[this.agentRow][this.agentCol] = this.boxes[boxRow][boxCol];
                        n.boxes[boxRow][boxCol] = 0;
                        if(heuristicBoxes_Point.containsKey(new Point(boxCol,boxRow))){
                            n.updateBoxesAt(boxRow,boxCol,agentRow,agentCol);
                        }

                        //System.err.println("Expanded with pull");
                        expandedNodes.add( n );
                    }
                }
            }
        }
        Collections.shuffle( expandedNodes, rnd );
        return expandedNodes;
    }

    private boolean cellIsFree( int row, int col ) {
        if(row < 0 || col < 0 )
            return false;

        return ( !this.walls[row][col] && this.boxes[row][col] == 0 );
    }

    private boolean boxAt( int row, int col ) {
        if(row < 0 || col < 0 )
            return false;
        return this.boxes[row][col] > 0;
    }

    public int dirToRowChange(Command.dir d ) {
        return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
    }

    public int dirToColChange( Command.dir d ) {
        return ( d == Command.dir.E ? 1 : ( d == Command.dir.W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
    }

    private SAState ChildNode() {
        SAState copy = new SAState( this );
        for ( int row = 0; row < MAX_ROW; row++ ) {
            System.arraycopy( this.walls[row], 0, copy.walls[row], 0, MAX_COLUMN );
            System.arraycopy( this.boxes[row], 0, copy.boxes[row], 0, MAX_COLUMN );
            System.arraycopy( this.goals[row], 0, copy.goals[row], 0, MAX_COLUMN );
        }
        copy.setHeuristicBoxes(heuristicBoxes_Int);
        return copy;
    }

    public LinkedList< SAState > extractPlan() {
        LinkedList< SAState > plan = new LinkedList< SAState >();
        SAState n = this;
        while( !n.isInitialState() ) {
            plan.addFirst( n );
            n = n.parent;
        }
        return plan;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + agentCol;
        result = prime * result + agentRow;
        result = prime * result + Arrays.deepHashCode( boxes );
        result = prime * result + Arrays.deepHashCode( goals );
        result = prime * result + Arrays.deepHashCode( walls );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        SAState other = (SAState) obj;
        if ( agentCol != other.agentCol )
            return false;
        if ( agentRow != other.agentRow )
            return false;
        if ( !Arrays.deepEquals( boxes, other.boxes ) ) {
            return false;
        }
        if ( !Arrays.deepEquals( goals, other.goals ) )
            return false;
        if ( !Arrays.deepEquals( walls, other.walls ) )
            return false;
        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for ( int row = 0; row < MAX_ROW; row++ ) {
            if ( !this.walls[row][0] ) {
                break;
            }
            for ( int col = 0; col < MAX_COLUMN; col++ ) {
                if ( this.boxes[row][col] > 0 ) {
                    s.append( this.boxes[row][col] );
                } else if ( this.goals[row][col] > 0 ) {
                    s.append( this.goals[row][col] );
                } else if ( this.walls[row][col] ) {
                    s.append( "+" );
                } else if ( row == this.agentRow && col == this.agentCol ) {
                    s.append( "0" );
                } else {
                    s.append( " " );
                }
            }

            s.append( "\n" );
        }
        return s.toString();
    }

}