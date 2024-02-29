package project;
public class Piece {
	int type;
	Crd[] moves;
	
	public int getType() {
		return type;
	}

	public Crd[] getMoves() {
		return moves;
	}
	
	public Piece(int type, Crd[] moves) {
		this.type = type;
		this.moves = new Crd[moves.length];
		for(int i = 0; i < moves.length; i++) {
			this.moves[i] = moves[i];
		}
	}
}