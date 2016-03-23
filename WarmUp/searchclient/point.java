package searchclient;

public class point{
	int x;
	int y;
	char a;
	
	point(int x, int y, char a){
		this.x = x;
		this.y = y;
		this.a = a;
	}
	
	public boolean equals(int x, int y){
		if(this.x == x && this.y == y){
			return true;
		}
		return false;
	}
}