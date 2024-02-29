package project;
public class Mv {
	int startX, endX, startY, endY;
	public Mv(int a, int b, int c, int d) {
		startX = b;
		startY = a;
		endX = d;
		endY = c;
	}
	
	public Crd getInit() {
		return new Crd(startX, startY);
	}
	public Crd getDest() {
		return new Crd(endX, endY);
	}
}
