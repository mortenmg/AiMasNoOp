package ai;

import java.util.LinkedList;

import static ai.Command.dir.*;

public class Command {
	static {
		LinkedList< Command > cmds = new LinkedList< Command >();
		for ( dir d : dir.values() ) {
			cmds.add( new Command( d ) );
		}

		for ( dir d1 : dir.values() ) {
			for ( dir d2 : dir.values() ) {
				if ( !Command.isOpposite( d1, d2 ) ) {
					cmds.add( new Command( type.Push, d1, d2 ) );
				}
			}
		}
		for ( dir d1 : dir.values() ) {
			for ( dir d2 : dir.values() ) {
				if ( d1 != d2 ) {
					cmds.add( new Command( type.Pull, d1, d2 ) );
				}
			}
		}

		every = cmds.toArray( new Command[0] );
	}

	public final static Command[] every;

	private static boolean isOpposite( dir d1, dir d2 ) {
		return d1.ordinal() + d2.ordinal() == 3;
	}

	// Order of enum important for determining opposites
	public static enum dir {
		N, W, E, S
	};
	
	public static enum type {
		Move, Push, Pull, NoOp
	};

	public final type actType;
	public final dir dir1;
	public final dir dir2;

	public Command( dir d ) {
		actType = type.Move;
		dir1 = d;
		dir2 = null;
	}

	public Command( type t, dir d1, dir d2 ) {
		actType = t;
		dir1 = d1;
		dir2 = d2;
	}

	public String toString() {
		if ( actType == type.Move )
			return actType.toString() + "(" + dir1 + ")";

		return actType.toString() + "(" + dir1 + "," + dir2 + ")";
	}

	public Command reverseCommand(Command command){

		Command com;

		// Push or Pull
		if(command.dir1 != null && command.dir2 != null){
			if(command.actType == type.Pull){
				com = new Command(type.Push,reverseDirection(dir1), reverseDirection(dir2));
				return com;
			}else {
				com = new Command(type.Pull,reverseDirection(dir1), reverseDirection(dir2));
				return com;
			}
		} else if(dir1 != null && dir2 == null){
			com = new Command(reverseDirection(dir1));
			return com;
		}

		return null;
	}

	private dir reverseDirection(dir direction){

		switch (direction){
			case E:
				direction = W;
				break;
			case W:
				direction = E;
				break;
			case N:
				direction = S;
				break;
			case S:
				direction = N;
				break;

		}
		return direction;
	}

	public String toActionString() {
		return "[" + this.toString() + "]";
	}


	public static int dirToRowChange( Command.dir d ) {
		return ( d == Command.dir.S ? 1 : ( d == Command.dir.N ? -1 : 0 ) ); // South is down one row (1), north is up one row (-1)
	}

	public static int dirToColChange( Command.dir d ) {
		return ( d == E ? 1 : ( d == W ? -1 : 0 ) ); // East is left one column (1), west is right one column (-1)
	}


}
