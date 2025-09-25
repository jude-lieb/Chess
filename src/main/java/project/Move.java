package project;

public class Move {
	Mod start, end, s1, s2;
	Game game;
	int bMatChange, wMatChange;
	boolean wK, wQ, bK, bQ;


	public Move(Game game, Mod a, Mod b, Mod c, Mod d) {
		this.game = game;
		this.start = a;
		this.end = b;
		this.s1 = c;
		this.s2 = d;

		if(start.type < 7) { //White

		} else { //Black

		}
	}

	public void enter() {
		start.apply(game.board);
		end.apply(game.board);
		if(s1 != null) 
			s1.apply(game.board);
		if(s1 != null) 
			s2.apply(game.board);
	}

	public void undo() {
		start.reverse(game.board);
		end.reverse(game.board);
		if(s1 != null) 
			s1.reverse(game.board);
		if(s1 != null) 
			s2.reverse(game.board);
	}

	public boolean isEqual(Crd init, Crd dest) {
		return init.equals(start.square) && dest.equals(end.square);
	}

	public Crd getInit() {
		return start.square;
	}

	public Crd getDest() {
		return start.square;
	}
}