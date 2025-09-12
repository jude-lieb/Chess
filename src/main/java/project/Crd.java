package project;
public class Crd {
	int x, y;

	public Crd(int y, int x) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Crd crd) {
		return crd.y == y && crd.x == x;
	}
}