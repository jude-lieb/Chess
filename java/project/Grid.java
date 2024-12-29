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
	Piece[] pieces;
	int[] values = {0,1,3,3,5,9,0,1,3,3,5,9,0};
	int color;
	int bMat;
	int wMat;
	boolean bK;
	boolean wK;
	boolean bQ;
	boolean wQ;
	
	//Copy constructor
	public Grid(int[][] board, Piece[] pieces, int wMat, int bMat, int color) {
		this.pieces = pieces;
		this.board = board.clone();
		this.bMat = bMat;
		this.wMat = wMat;
		this.color = color;
		bK = false;
		wK = false;
		bQ = false;
		wQ = false;
	}
	
	//Initializing
	public Grid(int[] set, Piece[] pieces, int wMat, int bMat, int startColor) {
		int[][] temp = new int[8][8];
		int count = 0; 
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				temp[i][j] = set[count];	
				count++;
			}
		}
		this.board = temp;
		this.pieces = pieces;
		color = startColor;
		this.bMat = bMat;
		this.wMat = wMat;
		bK = false;
		wK = false;
		bQ = false;
		wQ = false;
	}
	
	//Calls evaluation method and makes automatic move
	public void compMove() {		
		Eval ev = new Eval();
		CrdPair result = ev.getBestMove(this, color);
		if(result != null) {
			move(new Move(this, result));
		} else {
			System.exit(0);
		}
	}

	//Gets a material difference to be used in Eval scoring
	public int positionEval() {
		return bMat - wMat;
	}
	
	//Updates board array to new values when moving
	public void move(Move move) {
		
		//Castling
		if(move.special != 0) {
			System.out.println("special" + move.special);
			if(move.special == 1) { //Kingside
				board[move.coord.endY][7] = 0;
				board[move.coord.endY][5] = move.startType - 2;
			}
			if(move.special == 2) { //Queenside
				board[move.coord.endY][0] = 0;
				board[move.coord.endY][3] = move.startType - 2;
			}
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
	
	public void undoMove(Move move) {
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
		colorSwap();
	}

	//Returns whether the given color is in checkmate (no legal moves and in check)
	public boolean checkMate(int color) {
		return getLegalMoves(null, color) > 0;
	}
	
	//Checks whether a given coordinate is within board limits
	public boolean inBounds(Crd dest) {
		return (dest.y < 8 && dest.y > -1) && (dest.x < 8 && dest.x > -1);
	}
	
	//Returns the number of legal moves and saves them in parameter array
	public int getLegalMoves(CrdPair[] list, int color) {
		int count = canCastle(list, color);
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!colorCompare(board[i][j], color)) {
					Crd[] moves = pieces[board[i][j]].getMoves();
					for(int q = 0; q < moves.length; q++) {
						if(isMoveLegal(new Crd(i, j), new Crd(i+moves[q].y, j+moves[q].x), color)){
							list[count] = new CrdPair(i, j, i+moves[q].y, j+moves[q].x);
							count++;
						}
					}
				}
			}
		}
		return count;
	}
	
	public int canCastle(CrdPair[] list, int color) {
		int number = 0;
		//System.out.println("Status " + wK + wQ + bQ + bK);
		
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
					System.out.println("Success!");
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
			undoMove(stat);
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
					Crd[] moves = pieces[type].getMoves();
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
			System.out.println("Failed out of bounds");
			return false;
		}
		int type, x, y;
		for(int i = 0; i < 8; i++) {
			for(int q = 0; q < 8; q++) {
				type = board[i][q];
				if(type != 0 && colorCompare(type, color)) {
					Crd[] moves = pieces[type].getMoves();
					for(int j = 0; j < moves.length; j++) {
						x = q + moves[j].x;
						y = i + moves[j].y;
						if(inBounds(new Crd(y, x))) {
							if(coord.x == x && coord.y == y) {
								if(systemChecks(new Crd(i,q),new Crd(y, x))) {
									System.out.println("Failed " + coord.y + " " + coord.x);
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
		for(int i = 0; i < 8; i++) {
			System.out.println();
			for(int j = 0; j < 8; j++) {
				if(board[i][j] != 0) {
					System.out.printf("%-2s ", board[i][j]);
				} else {
					System.out.printf("%-2s ", "_");
				}
			}
		}
		System.out.println("wMat: " + wMat + " bMat: "+ bMat);
	}
	
	//Based on an online resource's array copier
	public Grid clone() {
		int length = board.length;
	    int[][] boardClone = new int[length][board[0].length];
	    for (int i = 0; i < length; i++) {
	        System.arraycopy(board[i], 0, boardClone[i], 0, board[i].length);
	    }
		return new Grid(boardClone, this.pieces, wMat, bMat, color);
	}
}