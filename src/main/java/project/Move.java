package project;
/**
 * Move Class
 * Complete record of status changes for given move.
 * Includes information like castling status and whether a promotion was made.
 * Allows for the undoing of moves using the recorded changes.
 */
public class Move {
	Mod start;
	Mod end;
	Mod s1, s2;

	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	int startType, endType;
	int matChange, promote;
	int special, color, legalMoveCount;
	boolean wK, wQ, bK, bQ;

	public boolean isEqual(Move move) {
		return false;
	}

	public Move(Game game, Mod start, Mod end, Mod s1, Mod s2) {
		this.start = start;
		this.end = end;
		this.s1 = s1;
		this.s2 = s2;

	}

}