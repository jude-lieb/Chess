package project;
public class CrdPair {
	int startX, endX, startY, endY;
	int extra;
	
	//Another coordinate format
	public CrdPair(int a, int b, int c, int d) {
		startY = a;
		startX = b;
		endY = c;
		endX = d;
	}
	
	public CrdPair(int a, int b, int c, int d, int extra) {
		startY = a;
		startX = b;
		endY = c;
		endX = d;
		this.extra = extra;
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

	public CrdPair clone() {
		return new CrdPair(startY, startX, endY, endX);
	}
	
	//Getting starting square coord
	public Crd getInit() {
		return new Crd(startY, startX);
	}
	
	//Getting destination square coord
	public Crd getDest() {
		return new Crd(endY, endX);
	}
	
	public int getX() {
		return endX - startX;
	}
	
	public int getY() {
		return endY - startY;
	}
}