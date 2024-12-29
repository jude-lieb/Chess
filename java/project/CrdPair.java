package project;
public class CrdPair {
	int startX, endX, startY, endY;
	
	//Another coordinate format
	public CrdPair(int a, int b, int c, int d) {
		startX = b;
		startY = a;
		endX = d;
		endY = c;
	}
	
	//Makes new Mv object with two coordinates
	public CrdPair(Crd init, Crd dest) {
		startX = init.x;
		startY = init.y;
		endX = dest.x;
		endY = dest.y;
	}
	
	public boolean equals(CrdPair coord) {
		return (startX == coord.startX && startY == coord.startY 
				&& endX == coord.endX && endY == coord.endY);
	}
	
	//Getting starting square coord
	public Crd getInit() {
		return new Crd(startY, startX);
	}
	
	//Getting destination square coord
	public Crd getDest() {
		return new Crd(endY, endX);
	}
}