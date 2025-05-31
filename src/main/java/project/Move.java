package project;
/**
 * Move Class
 * Complete record of status changes for given move.
 * Includes information like castling status and whether a promotion was made.
 * Allows for the undoing of moves using the recorded changes.
 */
public class Move {
	CrdPair[] moves = new CrdPair[100];
	CrdPair coord;
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	int startType, endType;
	int matChange, promote;
	int special, color, legalMoveCount;
	boolean wK, wQ, bK, bQ;
	
	//Saving move record to be used for move entering or undoing
	//Includes material change and promotion records
	public Move(Grid grid, CrdPair moveCrd) {
		coord = moveCrd;
		startType = grid.board[moveCrd.startY][moveCrd.startX];
		endType = grid.board[moveCrd.endY][moveCrd.endX];
		legalMoveCount = grid.legalMoveCount;
		matChange = 0;
		color = grid.color;
		special = 0;

		for(int i = 0; i < legalMoveCount; i++)
			moves[i] = grid.moves[i].clone();
 		
		if(startType == 1 && moveCrd.endY == 0 ) {
			matChange = values[grid.promote];
			promote = grid.promote;
		}

		if(startType == 7 && moveCrd.endY == 7) {
			matChange = values[grid.promote + 6];
			promote = grid.promote + 6;
		}
		
		//King moves
		if(startType == 6 || startType == 12) {
			//castling adjustment
			int shift = moveCrd.endX - moveCrd.startX;

			if(shift == 2) //Castle kingside
				special = 1;

			if(shift == -2) //Castle Queenside
				special = 2;
			
			if(startType < 7) {
				if(!grid.wQ) wQ = true;
				if(!grid.wK) wK = true;
			} else {
				if(!grid.bQ) bQ = true;
				if(!grid.bK) bK = true;
			}
		}
		
		if(startType == 4) { //white rook
			if(!grid.wQ && coord.startX == 0)
				wQ = true;
			if(!grid.wK && coord.startX == 7) 
				wK = true;
		}
		
		if(startType == 10) { //Black rook
			if(!grid.bQ && coord.startX == 0) 
				bQ = true;
			if(!grid.bK && coord.startX == 7) 
				bK = true;
		}

		//En passant capture
		if(coord.passant != 0) matChange = 1;

		matChange = matChange + values[grid.board[moveCrd.endY][moveCrd.endX]];
	}
}