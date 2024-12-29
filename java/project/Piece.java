package project;
public class Piece {
	Crd[] moves;
	
	//Sends back all potential shifts for this piece
	public Crd[] getMoves() {
		return moves;
	}
	
	public Piece(int type, Crd[] moves) {
		this.moves = moves;
	}
}