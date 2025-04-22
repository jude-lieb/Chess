package project;
public class Crd {
	int x;
	int y;
	
	public Crd(int y, int x) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Crd compare) {
		return compare.y == y && compare.x == x;
	}
}