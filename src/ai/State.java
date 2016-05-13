package ai;

import java.awt.*;
import java.util.*;

public class State {

	private static Random rnd = new Random( 1 ); 
	public static int MAX_ROW = 70;
	public static int MAX_COLUMN = 25;

	public int agentRow;
	public int agentCol;

	private Task task;

	public HashMap<Integer, Box> getBoxes() {
		return boxes;
	}

	public void setTask(Task task) { this.task = task; }

	public Task getTask() {
		return this.task;
	}

	// public char[][] boxes = new char[MAX_ROW][MAX_COLUMN];
	private HashMap<Integer,Box> boxes = new HashMap<>();


	public State parent;
	public Command action;

	public Command getAction() {
		return action;
	}

	private int g;

	public State(State parent ) {
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

	public ArrayList<State> getExpandedNodes() {
		//System.err.println("Looking from "+agentCol+" , "+agentRow);
		ArrayList<State> expandedStates = new ArrayList<State>( Command.every.length );
		for ( Command c : Command.every ) {
			// Determine applicability of action
			int newAgentRow = this.agentRow + dirToRowChange( c.dir1 );
			int newAgentCol = this.agentCol + dirToColChange( c.dir1 );

			if ( c.actType == Command.type.Move ) {
				// Check if there's a wall or box on the ai.Cell to which the agent is moving
				if ( cellIsFree( newAgentRow, newAgentCol ) ) {
					State n = this.ChildNode();
					n.action = c;
					n.agentRow = newAgentRow;
					n.agentCol = newAgentCol;
					expandedStates.add( n );
				}
			} else if ( c.actType == Command.type.Push ) {
				// Make sure that there's actually a box to move
				if ( boxAt( newAgentRow, newAgentCol ) ) {
					int newBoxRow = newAgentRow + dirToRowChange( c.dir2 );
					int newBoxCol = newAgentCol + dirToColChange( c.dir2 );
					// .. and that new ai.Cell of box is free
					if ( cellIsFree( newBoxRow, newBoxCol ) ) {
						State n = this.ChildNode();
						n.action = c;
						n.agentRow = newAgentRow;
						n.agentCol = newAgentCol;

						// update boxes
						n.boxes.get(task.getBoxId()).location = new Point(newBoxRow,newBoxCol);

						expandedStates.add( n );
					}
				}
			} else if ( c.actType == Command.type.Pull ) {
				// ai.Cell is free where agent is going
				if ( cellIsFree( newAgentRow, newAgentCol ) ) {
					int boxRow = this.agentRow + dirToRowChange( c.dir2 );
					int boxCol = this.agentCol + dirToColChange( c.dir2 );
					// .. and there's a box in "dir2" of the agent
					if ( boxAt( boxRow, boxCol ) ) {
						State n = this.ChildNode();
						n.action = c;
						n.agentRow = newAgentRow;
						n.agentCol = newAgentCol;

						// update boxes
						n.boxes.get(task.getBoxId()).location = new Point(this.agentRow, this.agentCol);

						expandedStates.add( n );
					}
				}
			}
		}
		Collections.shuffle(expandedStates, rnd );
		return expandedStates;
	}

	private boolean cellIsFree( int row, int col ) { //I dont think you can do this! map does not(should not) contain box information. TODO
		return ! Supervisor.getInstance().getLevel().getMap()[row][col].hasBox() && Supervisor.getInstance().getLevel().getMap()[row][col].getType()!=CellType.WALL;
	}

	/**
	 * Checks if there is a box in the location given,
	 * and if the box is the relevant box
	 * from the task.
	 * @param row
	 * @param col
     * @return
     */
	private boolean boxAt( int row, int col ) {
		Box myBox = boxes.get(task.getBoxId());
		return myBox.location.equals(new Point(row,col));
	}

	public void setBoxes(HashMap<Integer, Box> boxes) {
		this.boxes = boxes;
	}

	private int dirToRowChange( Command.dir d ) {
		return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
	}

	private int dirToColChange( Command.dir d ) {
		return ( d == Command.dir.E ? 1 : ( d == Command.dir.W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
	}

	private State ChildNode() {
		State copy = new State( this );
		copy.setBoxes((HashMap<Integer, Box>) boxes.clone());
		copy.setTask(this.task);
		// TODO: do a proper deepclone of the boxes array. - See level, it is kinda implemented there
		return copy;
	}

	public LinkedList<State> extractPlan() {
		LinkedList<State> plan = new LinkedList<State>();
		State n = this;
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
		//result = prime * result + Arrays.deepHashCode( boxes );
		//result = prime * result + Arrays.deepHashCode( SearchClient.goals );
		//result = prime * result + Arrays.deepHashCode( SearchClient.walls );
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
		State other = (State) obj;
		if ( agentCol != other.agentCol )
			return false;
		if ( agentRow != other.agentRow )
			return false;

		if(! boxes.keySet().equals(other.boxes.keySet())) {
			return false;
		}
		for (int key : boxes.keySet()) {
			if(!boxes.get(key).location.equals(other.boxes.get(key).location) ||
					!(boxes.get(key).letter == other.boxes.get(key).letter)) {
				return false;
			}
		}
		//if ( !Arrays.deepEquals( boxes, other.boxes ) ) {
		//	return false;
		//}
		// if ( !Arrays.deepEquals( goals, other.goals ) )
		// 	return false;
		// if ( !Arrays.deepEquals( walls, other.walls ) )
		// 	return false;
		return true;
	}

	// TODO: Refactor this to string method
	/*
	public String toString() {
		StringBuilder s = new StringBuilder();
		for ( int row = 0; row < MAX_ROW; row++ ) {
			if ( ai.Supervisor.getInstance().getMap()[row][0].getCell()!='+' ) {
				break;
			}
			for ( int col = 0; col < MAX_COLUMN; col++ ) {
				if ( this.boxes[row][col] > 0 ) {
					s.append( this.boxes[row][col] );
				} else if ( ai.Supervisor.getInstance().getGoals().get(new Point(row,col)) > 0 ) {
					s.append( SearchClient.goals[row][col] );
				} else if ( SearchClient.walls[row][col] ) {
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
	*/

}