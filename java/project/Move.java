package project;
/**
 * Move Class
 * Complete record of status changes for given move.
 * Includes information like castling status and whether a promotion was made.
 * Allows for the undoing of moves using the recorded changes.
 */
public class Move {
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	int startType, endType, matChange, promote;
	int special;
	CrdPair coord;
	boolean wK;
	boolean wQ;
	boolean bK;
	boolean bQ;
	
	//Saving move record to be used for move entering or undoing
	//Includes material change and promotion records
	public Move(Grid grid, CrdPair moveCrd) {		
		coord = moveCrd;
		startType = grid.board[moveCrd.startY][moveCrd.startX];
		endType = grid.board[moveCrd.endY][moveCrd.endX];
		matChange = 0;
		special = 0;
		
		if(startType == 1 && moveCrd.endY == 0 ) {
			matChange = 8;
			promote = 5;
		}

		if(startType == 7 && moveCrd.endY == 7) {
			matChange = 8;
			promote = 11;
		}
		
		//King moves
		if(startType == 6 || startType == 12) {
			//castling adjustment
			int shift = moveCrd.endX - moveCrd.startX;
			if(shift == 2) {  //Castle kingside
				System.out.println("kingside");
				special = 1;
			}
			if(shift == -2) {//Castle Queenside
				System.out.println("queenside");
				special = 2;
			}
			
			if(startType < 7) {
				if(!grid.wQ) {
					wQ = true;
				}
				if(!grid.wK) {
					wK = true;
				}
			} else {
				if(!grid.bQ) {
					bQ = true;
				}
				if(!grid.bK) {
					bK = true;
				}
			}
		}
		
		if(startType == 4) { //white rook
			if(!grid.wQ && coord.startX == 0) {
				wQ = true;
			}
			if(!grid.wK && coord.startX == 7) {
				wK = true;
			}
		}
		
		if(startType == 10) { //Black rook
			if(!grid.bQ && coord.startX == 0) {
				bQ = true;
			}
			if(!grid.bK && coord.startX == 7) {
				bK = true;
			}
		}
		
		matChange = matChange + values[grid.board[moveCrd.endY][moveCrd.endX]];
	}
}