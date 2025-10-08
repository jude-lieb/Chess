package project;

public class Move {
	Mod start, end, s1, s2;
	Game game;
	int bMatChange, wMatChange;
	boolean color;
	boolean wK, wQ, bK, bQ;
	int passant;


	public Move(Game game, Mod a, Mod b, Mod c, Mod d) {
		this.game = game;
		this.start = a;
		this.end = b;
		this.s1 = c;
		this.s2 = d;

		//Setting en passant potential marker
		if((start.type == 1 || start.type == 7) && (Math.abs(start.square.y - end.square.y) == 2)) {
			passant = end.square.x;
		} else {
			passant = -1;
		}

		color = start.type < 7; //White
	}

	public void enter() {
		start.apply(game.board);
		end.apply(game.board);
		if(s1 != null) 
			s1.apply(game.board);
		if(s2 != null) 
			s2.apply(game.board);

		if(start.type == 6 || start.type == 12) {
            game.currentPlayer.king = end.square;
        }
	}

	public void undo() {
		start.reverse(game.board);
		end.reverse(game.board);
		if(s1 != null) 
			s1.reverse(game.board);
		if(s2 != null) 
			s2.reverse(game.board);

		if(start.type == 6 || start.type == 12) {
			game.currentPlayer.king = start.square;
        }
	}

	public void print() {
		System.out.println("Move: Type = " + start.type);
		start.square.print();
		end.square.print();
    }

	public boolean isEqual(Crd init, Crd dest) {
		return init.equals(start.square) && dest.equals(end.square);
	}

	public Crd getInit() {
		return start.square;
	}

	public Crd getDest() {
		return end.square;
	}
}