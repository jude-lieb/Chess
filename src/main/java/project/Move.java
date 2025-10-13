package project;

public class Move {
	static int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	Mod start, end, s1, s2;
	Game game;
	int bMatChange, wMatChange;
	boolean wK, wQ, bK, bQ;
	int passant;
	static Crd wKing = new Crd(7, 4);
	static Crd bKing = new Crd(0, 4);
	static Crd wQRook = new Crd(7,0);
	static Crd wKRook = new Crd(7,7);
	static Crd bQRook = new Crd(0,0);
	static Crd bKRook = new Crd(0,7);

	public Move(Game game, Mod[] params) {
		this.game = game;
		this.start = params[0];
		this.end = params[1];
		this.s1 = params[2];
		this.s2 = params[3];

		if(s2 == null) {
			if(s1 != null) {
				if(start.type < 7) {
					bMatChange = 1;
				} else {
					wMatChange = 1;
				}
			} else {
				if(start.type == end.replace) {
					if(start.type < 7) {
						bMatChange = values[end.type];
					} else {
						wMatChange = values[end.type];
					}
				} else { //Promotion
					if(start.type < 7) {
						bMatChange = values[end.type];
						wMatChange = -values[end.replace] + 1;
					} else {
						wMatChange = values[end.type];
						bMatChange = -values[end.replace] + 1;
					}
				}
			}
		}

		//Setting en passant potential marker
		if((start.type == 1 || start.type == 7) && (Math.abs(start.square.y - end.square.y) == 2)) {
			passant = end.square.x;
		} else {
			passant = -1;
		}

		if(start.type == 6) {
			if(start.square.equals(wKing)) {
				if(game.white.kingside) {
					wK = true;
				}
				if(game.white.queenside) {
					wQ = true;
				}
			}
        } else if(start.type == 12) {
			if(start.square.equals(bKing)) {
				if(game.black.kingside) {
					bK = true;
				}
				if(game.black.queenside) {
					bQ = true;
				}
			}
		}

		if(start.type == 4) {
			if(start.square.equals(wKRook) && game.white.kingside) {
				wK = true;
			}
			if(start.square.equals(wQRook) && game.white.queenside) {
				wQ = true;
			}
		} else if(start.type == 10) {
			if(start.square.equals(bKRook) && game.black.kingside) {
				bK = true;
			}
			if(start.square.equals(bQRook) && game.black.queenside) {
				bQ = true;
			}
		}
	}

	public void enter() {
		start.apply(game.board);
		end.apply(game.board);
		if(s1 != null) 
			s1.apply(game.board);
		if(s2 != null) 
			s2.apply(game.board);

		if(start.type == 6) {
			game.white.king = end.square;
        } else if(start.type == 12) {
			game.black.king = end.square;
		}

		if(wK) game.white.kingside = false;
		if(wQ) game.white.queenside = false;
		if(bK) game.black.kingside = false;
		if(bQ) game.black.queenside = false;
		game.white.material -= wMatChange;
		game.black.material -= bMatChange;
	}

	public void undo() {
		start.reverse(game.board);
		end.reverse(game.board);
		if(s1 != null) 
			s1.reverse(game.board);
		if(s2 != null) 
			s2.reverse(game.board);

		if(start.type == 6) {
			game.white.king = start.square;
        } else if(start.type == 12) {
			game.black.king = start.square;
		}

		if(wK) game.white.kingside = true;
		if(wQ) game.white.queenside = true;
		if(bK) game.black.kingside = true;
		if(bQ) game.black.queenside = true;
		game.white.material += wMatChange;
		game.black.material += bMatChange;
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