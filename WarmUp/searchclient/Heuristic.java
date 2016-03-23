package searchclient;

import java.util.Comparator;

public abstract class Heuristic implements Comparator< Node > {

	public Node initialState;
	public Heuristic(Node initialState) {
		this.initialState = initialState;
	}

	public int compare( Node n1, Node n2 ) {
		return f( n1 ) - f( n2 );
	}

	public int h( Node n ) {
		int h = 0;
		outerloop:
		for (int row = 1; row < n.MAX_ROW-1 ; row++ ) {
			for (int col = 1 ; col< n.MAX_COLUMN-1 ; col++ ) {
				char chr = n.boxes[row][col];
				if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
					for (point p : SearchClient.goalsAsPoints){
						if (p.a == Character.toLowerCase(chr)){
							h += euclidean(p,row,col);
						}
					}
				}
			}			
		}
		// System.err.println("Heuristics: " + he);
		return h;
	}

	public int euclidean(int x1, int y1, int x2, int y2){
		return (int)Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
	}
	public int euclidean(point p, int x, int y){
		// System.err.println("Point: " + p.x + ", " + p.y);
		return (int)Math.sqrt(Math.pow((p.x-x),2)+Math.pow((p.y-y),2));
	}

	public abstract int f( Node n );

	public static class AStar extends Heuristic {
		public AStar(Node initialState) {
			super( initialState );
		}

		public int f( Node n ) {
			return n.g() + h( n );
		}

		public String toString() {
			return "A* evaluation";
		}
	}

	public static class WeightedAStar extends Heuristic {
		private int W;

		public WeightedAStar(Node initialState) {
			super( initialState );
			W = 5; // You're welcome to test this out with different values, but for the reporting part you must at least indicate benchmarks for W = 5
		}

		public int f( Node n ) {
			return n.g() + W * h( n );
		}

		public String toString() {
			return String.format( "WA*(%d) evaluation", W );
		}
	}

	public static class Greedy extends Heuristic {

		public Greedy(Node initialState) {
			super( initialState );
		}

		public int f( Node n ) {
			return h( n );
		}

		public String toString() {
			return "Greedy evaluation";
		}
	}
}
