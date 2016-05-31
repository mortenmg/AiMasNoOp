package ai;

import java.awt.*;
import java.util.*;

public class State {

	private static Random rnd = new Random( 1 );

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
	private HashMap<Integer,Box> boxes = new HashMap<>();

	private boolean relaxedPlanning = false;


	public State parent;
	public Command action;

	public Command getAction() {
		return action;
	}

	private int g;

	public State(State parent) {
		this.parent = parent;
		if ( parent == null ) {
			g = 0;
		} else {
			g = parent.g() + 1;
			this.relaxedPlanning = parent.getRelaxedPlanning();
		}
	}

	public int g() {
		return g;
	}

	private boolean isInitialState() {
		return this.parent == null;
	}

	public ArrayList<State> getExpandedNodes() {
		//System.err.println("Looking from "+agentCol+" , "+agentRow);
		ArrayList<State> expandedStates = new ArrayList<State>( Command.every.length );
		for ( Command c : Command.every ) {
			// Determine applicability of action
			int newAgentRow = this.agentRow + Command.dirToRowChange( c.dir1 );
			int newAgentCol = this.agentCol + Command.dirToColChange( c.dir1 );

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
					int newBoxRow = newAgentRow + Command.dirToRowChange( c.dir2 );
					int newBoxCol = newAgentCol + Command.dirToColChange( c.dir2 );
					// .. and that new ai.Cell of box is free
					if ( cellIsFree( newBoxRow, newBoxCol ) ) {
						State n = this.ChildNode();
						n.action = c;
						n.agentRow = newAgentRow;
						n.agentCol = newAgentCol;

						// update boxes
						n.boxes.get(task.getBoxId()).location = new Point(newBoxCol,newBoxRow);

						expandedStates.add( n );
					}
				}
			} else if ( c.actType == Command.type.Pull ) {
				// ai.Cell is free where agent is going
				if ( cellIsFree( newAgentRow, newAgentCol ) ) {
					int boxRow = this.agentRow + Command.dirToRowChange( c.dir2 );
					int boxCol = this.agentCol + Command.dirToColChange( c.dir2 );
					// .. and there's a box in "dir2" of the agent
					if ( boxAt( boxRow, boxCol ) ) {
						State n = this.ChildNode();
						n.action = c;
						n.agentRow = newAgentRow;
						n.agentCol = newAgentCol;

						// update boxes
						n.boxes.get(task.getBoxId()).location = new Point(this.agentCol, this.agentRow);

						expandedStates.add( n );
					}
				}
			}
		}
		Collections.shuffle(expandedStates, rnd );
		return expandedStates;
	}

	private boolean cellIsFree( int row, int col ) { //I dont think you can do this! map does not(should not) contain box information. TODO
		for (Box b : boxes.values()) {
			if (b.location.x == col && b.location.y == row && !relaxedPlanning)
				return false;
		}
		return  Supervisor.getInstance().getLevel().getMap()[row][col].getType()!=CellType.WALL;
	}

	private boolean boxAt( int row, int col ) {
		Box myBox = boxes.get(task.getBoxId());
		return myBox.location.equals(new Point(col,row));
	}

	public void setBoxes(HashMap<Integer, Box> boxes) {
		this.boxes = boxes;
	}

	private State ChildNode() {
		State copy = new State( this );
		HashMap<Integer,Box> newBoxes = new HashMap<>();

		for (Box b: boxes.values()) {
			newBoxes.put(b.id, new Box(b));
		}
		copy.setBoxes(newBoxes);
		copy.setTask(this.task);
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
		return true;
	}

	public boolean getRelaxedPlanning() {
		return relaxedPlanning;
	}

	public void setRelaxedPlanning(boolean relaxed) {
		relaxedPlanning = relaxed;
	}
}