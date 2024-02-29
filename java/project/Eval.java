package project;

public class Eval {
	
	
	
	public boolean isMate(Grid g, Mv move, int color, int level) {		
		Grid temp = new Grid(g.board, g.pieces);
		temp.move(move.getInit(), move.getDest());
			
		if(temp.checkMate(otherColor(color))) {
			return true;
		}

		if(level < 2) {
			//Response moves
			Mv[] m = new Mv[200];
			int count = temp.getLegalMoves(m, otherColor(color));

			
			for(int j = 0; j < count; j++) {
				if(isMate(temp, m[j], otherColor(color), level + 1) == false) {
					
				}
			}
			
		}
		
		return false;
	}
	
	public int otherColor(int color) {
		if(color < 7) {
			return 12;
		} else {
			return 6;
		}
	}
}
