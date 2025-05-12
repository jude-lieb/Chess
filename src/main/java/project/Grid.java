package project;
/**
 * Grid class
 * Uses integer matrix to represent chess board
 * Move legality and coordinates analysis
 * Scanning for checks
 * Piece obstacle detection
 * Entering and undoing moves
 */
public class Grid {
	int[][] board;
	MoveStack prevMoves;
	Crd[][] pieceMoves;
	CrdPair[] moves;
	int[] values = {0,1,3,3,5,9,0,1,3,3,5,9,0};
	int legalMoveCount;
	int color;
	int promote;
	int bMat;
	int wMat;
	boolean bK;
	boolean wK;
	boolean bQ;
	boolean wQ;
	
	//Initializing
	public Grid(int[] set, Crd[][] pieceMoves, int wMat, int bMat, int startColor, int promote) {
		int[][] temp = new int[8][8];
		int count = 0; 
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				temp[i][j] = set[count];	
				count++;
			}
		}
		this.promote = promote;
		this.board = temp;
		this.pieceMoves = pieceMoves;
		prevMoves = new MoveStack();
		color = startColor;
		this.bMat = bMat;
		this.wMat = wMat;
		bK = false;
		wK = false;
		bQ = false;
		wQ = false;
		findLegalMoves();
		//System.out.println("legal move count " + legalMoveCount);
	}

	public CrdPair isLegal(CrdPair chosenMove) {
		for(int i = 0; i < 100 && moves[i] != null; i++) {
			if(moves[i].equals(chosenMove)) {
				return moves[i];
			}
		}
		return null;
	}

	public int status() {
		if(legalMoveCount == 0) {
			if(inCheck(color)) { //Checkmate
				return 2;
			} else { //Stalemate
				return 1; 
			}
		} else { //Legal moves available
			return 0;
		}
	}
	
	//Calls evaluation method and makes automatic move
	public void compMove() {	
		if(legalMoveCount == 0) {
			System.out.println("Computer cannot move.");
			return;
		}

		Eval ev = new Eval();
		ev.getBestMove(this);
		//System.out.println("Best Move: ");
		//System.out.println(this.moves[0]);
		move(new Move(this, this.moves[0]));
	}

	//Gets a material difference to be used in Eval scoring
	public int positionEval() {
		return bMat - wMat;
	}
	
	//Updates board array to new values when moving
	public void move(Move move) {
		prevMoves.push(move);
		
		//Castling
		if(move.special != 0) {
			if(move.special == 1) { //Kingside
				board[move.coord.endY][7] = 0;
				board[move.coord.endY][5] = move.startType - 2;
			}
			if(move.special == 2) { //Queenside
				board[move.coord.endY][0] = 0;
				board[move.coord.endY][3] = move.startType - 2;
			}
		}
		
		if(move.coord.passant != 0) { //En Passant capturing adjacently
			board[move.coord.startY][move.coord.startX - move.coord.passant] = 0;
		}
		
		//Castle status updates
		if(move.bK) {
			bK = true;
		}
		if(move.bQ) {
			bQ = true;
		}
		if(move.wK) {
			wK = true;
		}
		if(move.wQ) {
			wQ = true;
		}
		
		//Handling material change
		if(move.startType > 6) {
			wMat = wMat - move.matChange;
		} else {
			bMat = bMat - move.matChange;
		}
		
		//Moving integer type values in board
		board[move.coord.startY][move.coord.startX] = 0;
		if(move.promote != 0) {
			board[move.coord.endY][move.coord.endX] = move.promote;
		} else {
			board[move.coord.endY][move.coord.endX] = move.startType;
		}
		colorSwap();
	}
	
	public void undoMove() {
		Move move = prevMoves.pop();
		if(move == null) {
			System.out.println("No moves to undo.");
			return;
		}
		
		//Uncastling
		if(move.special != 0) {
			if(move.special == 1) { //Kingside
				board[move.coord.endY][7] = move.startType - 2;
				board[move.coord.endY][5] = 0;
			}
			if(move.special == 2) { //Queenside
				board[move.coord.endY][0] = move.startType - 2;
				board[move.coord.endY][3] = 0;
			}
		}
		
		if(move.coord.passant != 0) { //En Passant capturing adjacently
			board[move.coord.startY][move.coord.startX - move.coord.passant] = 1;
		}
		
		//Castle status updates
		if(move.bK) {
			bK = false;
		}
		if(move.bQ) {
			bQ = false;
		}
		if(move.wK) {
			wK = false;
		}
		if(move.wQ) {
			wQ = false;
		}

		//Handling material change
		if(move.startType > 6) {
			wMat = wMat + move.matChange;
		} else {
			bMat = bMat + move.matChange;
		}
		
		//Moving integer type values in board
		board[move.coord.endY][move.coord.endX] = move.endType;
		board[move.coord.startY][move.coord.startX] = move.startType;

		legalMoveCount = move.legalMoveCount;
		moves = move.moves;
		//System.out.println("Undo Color " + move.color);
		color = move.color;
	}
	
	public int canEnPassant(CrdPair[] list, int count) {
		int index = count;
		Move prev = prevMoves.peek();
		if(prev == null) {
			return index;
		}
		
		int shift = Math.abs(prev.coord.getY());
		
		if(prev.startType == prev.color - 5 && shift == 2) {
			int x1 = prev.coord.endX - 1;
			int x2 = prev.coord.endX + 1;
			int way = 0;
			
			if(color == 6) {
				way = -1;
			} else {
				way = 1;
			}
			
			if(x1 > 0 && x1 < 8) {
				if(board[prev.coord.endY][x1] == color - 5) {
					list[index] = new CrdPair(prev.coord.endY, x1, prev.coord.endY + way, x1 + 1, -1);
					index++;
				}
			}
			if(x2 > 0 && x2 < 8) {
				if(board[prev.coord.endY][x2] == color - 5) {
					list[index] = new CrdPair(prev.coord.endY, x2, prev.coord.endY + way, x2 - 1, 1);
					index++;
				}
			}
		}
		
		return index;
	}

	//Checks whether a given coordinate is within board limits
	public boolean inBounds(Crd dest) {
		return (dest.y < 8 && dest.y > -1) && (dest.x < 8 && dest.x > -1);
	}
	
	//Updating the list of legal moves in the position
	public void findLegalMoves() {
		CrdPair[] list = new CrdPair[100];
		int count = canCastle(list, color);
		count = canEnPassant(list, count);
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!colorCompare(board[i][j], color)) {
					Crd[] moves = pieceMoves[board[i][j]];
					for(int q = 0; q < moves.length; q++) {
						if(isMoveLegal(new Crd(i, j), new Crd(i+moves[q].y, j+moves[q].x), color)){
							list[count] = new CrdPair(i, j, i+moves[q].y, j+moves[q].x);
							count++;
						}
					}
				}
			}
		}
		legalMoveCount = count;
		moves = list;
	}
	
	public int canCastle(CrdPair[] list, int color) {
		int number = 0;
		if(inCheck(color)) {
			return 0;
		}

		if(color > 6) {
			if(bQ == false) {
				if(board[0][2] == 0 && board[0][3] == 0 && board[0][1] == 0 &&
						!squareAttacked(new Crd(0, 2), color) && !squareAttacked(new Crd(0, 3), color)){
					list[number] = new CrdPair(0,4,0,2);
					number++;
				}
			}
			if(bK == false) {
				if(board[0][5] == 0 && board[0][6] == 0 && 
						!squareAttacked(new Crd(0, 5), color) && !squareAttacked(new Crd(0, 6), color)){
					list[number] = new CrdPair(0,4,0,6);
					number++;
				}
			}
		} else {
			if(wQ == false) {
				if(board[7][2] == 0 && board[7][3] == 0 && board[7][1] == 0 &&
						!squareAttacked(new Crd(7, 2), color) && !squareAttacked(new Crd(7, 3), color)){
					//System.out.println("Success!");
					list[number] = new CrdPair(7,4,7,2);
					number++;
				}
			}
			if(wK == false) {
				if(board[7][5] == 0 && board[7][6] == 0 && 
						!squareAttacked(new Crd(7, 5), color) && !squareAttacked(new Crd(7, 6), color)){
					list[number] = new CrdPair(7,4,7,6);
					number++;
				}
			}
		}
		return number;
	}
	
	//Determines if a move is allowed
	//Uses board boundaries, check status, piece potential moves and obstacles
	public boolean isMoveLegal(Crd init, Crd mv, int color) {
		if(inBounds(mv) && colorCompare(color, board[mv.y][mv.x]) && systemChecks(init, mv)) {
			Move stat = new Move(this, new CrdPair(init, mv));
			move(stat);
			boolean result = !inCheck(color);
			undoMove();
			return result;
		}
		return false;
	}

	//Returns whether the given color's king is in check
	public boolean inCheck(int color) {
		int type, x, y;
		for(int i = 0; i < 8; i++) {
			for(int q = 0; q < 8; q++) {
				type = board[i][q];
				if(type != 0 && colorCompare(type, color)) {
					Crd[] moves = pieceMoves[type];
					for(int j = 0; j < moves.length; j++) {
						x = q + moves[j].x;
						y = i + moves[j].y;
						if(inBounds(new Crd(y, x))) {
							if(board[y][x] == color) {
								if(systemChecks(new Crd(i,q),new Crd(y, x))) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean squareAttacked(Crd coord, int color) {
		if(!inBounds(coord)) {
			return false;
		}
		int type, x, y;
		for(int i = 0; i < 8; i++) {
			for(int q = 0; q < 8; q++) {
				type = board[i][q];
				if(type != 0 && colorCompare(type, color)) {
					Crd[] moves = pieceMoves[type];
					for(int j = 0; j < moves.length; j++) {
						x = q + moves[j].x;
						y = i + moves[j].y;
						if(inBounds(new Crd(y, x))) {
							if(coord.x == x && coord.y == y) {
								if(systemChecks(new Crd(i,q),new Crd(y, x))) {
									return true;
								}
							}
						}
					}
				}
			}
		}	
		return false;
	}

	//Makes sure a piece has no obstacles in between start and destination
	public boolean systemChecks(Crd init, Crd mv) {
		switch (board[init.y][init.x]) {
		case 3, 9: //Bishop
			if(bishopCheck(init.x, init.y, mv.x, mv.y) == false) {
				return false;
			}
			break;		
		case 4, 10: //Book
			if(rookCheck(init.x, init.y, mv.x, mv.y) == false) {
				return false;
			}
			break;
		case 5, 11: //Queen
			if(mv.x != init.x && mv.y != init.y) {
				if(!bishopCheck(init.x, init.y, mv.x, mv.y)) {
					return false;
				} else {
					break;
				}
			} else if(mv.x == init.x || mv.y == init.y){
				if(!rookCheck(init.x, init.y, mv.x, mv.y)) {
					return false;
				} else {
					break;
				}
			} else {
				return false;
			}
		
		case 1, 7: //Pawn
			if(pawnCheck(init.x, init.y, mv.x, mv.y) == false) {
				return false;
			}
		}
		return true;
	}
	
	//Analyzing pawn move legality
	public boolean pawnCheck(int startX, int startY, int endX, int endY) {
		if(Math.abs(startY - endY) == 2) { //checking if 2 square move with pawn
			if(board[startY][startX] == 1) {
				if(startY == 6) {//Has the pawn moved yet? (black)
					return (board[endY][endX] == 0 && board[endY+1][endX] == 0);
				} else {
					return false;
				}
			} else {
				if(startY == 1) {//Has the pawn moved yet? (white)
					return (board[endY][endX] == 0 && board[endY-1][endX] == 0);
				} else {
					return false;
				}
			}
		} else {
			if(startX == endX) {
				return board[endY][endX] == 0;
			} else {
				return board[endY][endX] != 0;
			}
		}
	}
	
	//Bishop obstacle scanning (also used for queen)
	public boolean bishopCheck(int startX, int startY, int endX, int endY) {
		int ycount;
		int xstart;
		int ystart;
		//Setting correct diagonal for scanning
		if(startY < endY && startX < endX) { 
			xstart = startX + 1;
			ystart = startY + 1;
			ycount = 1;
		} else if(endY < startY && endX < startX) { 
			xstart = endX + 1;
			ystart = endY + 1;
			ycount = 1;
		} else if(startY < endY && endX < startX) {
			xstart = endX + 1;
			ystart = endY - 1;
			ycount = - 1;
		} else {
			xstart = startX + 1;
			ystart = startY - 1;
			ycount = - 1;
		}
		
		int v = ystart;
		int u = xstart;
		
		//Scanning
		for(int i = 0; i < Math.abs(startX - endX) - 1; i++) { 
			if(board[v][u] != 0) {
				return false;
			}
			v = v + ycount;
			u++;
		}
		return true;
	}
	
	//Rook obstacle scanning (also used for queen)
	public boolean rookCheck(int startX, int startY, int endX, int endY) {
		int beginning;
		int end;
		if(startY == endY) { //Determining which direction to scan
			if(startX > endX) {
				beginning = endX + 1;
				end = startX;
			} else {
				beginning = startX + 1;
				end = endX;
			}
			//Scanning horizontally
			for(int i = beginning; i < end; i++) {
				if(board[startY][i] != 0) {
					return false;
				}
			}
		} else { //startX == endX
			if(startY > endY) {
				beginning = endY + 1;
				end = startY;
			} else {
				beginning = startY + 1;
				end = endY;
			}
			//Scanning vertically
			for(int i = beginning; i < end; i++) {
				if(board[i][startX] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	//Checks if pieces are the same color
	public static boolean colorCompare(int type1, int type2) { 
		if(type2 != 0) {
			if((type2 > 6 && type1 > 6) || (type2 <= 6 && type1 <= 6)) {
				return false;
			}
		}
		return true;
	}
	
	//Changes turn
	public void colorSwap() { 
		if(color == 12) {
			color = 6;
		} else {
			color = 12;
		}
	}	
	
	//Displaying integer representation of board and material totals
	public void print() {
		// for(int i = 0; i < 8; i++) {
		// 	System.out.println();
		// 	for(int j = 0; j < 8; j++) {
		// 		if(board[i][j] != 0) {
		// 			System.out.printf("%-2s ", board[i][j]);
		// 		} else {
		// 			System.out.printf("%-2s ", "_");
		// 		}
		// 	}
		// }
		// System.out.println("wMat: " + wMat + " bMat: "+ bMat);
	}
}