package project;
/**
 * Grid class
 * Uses int matrix to represent chess board
 * Move legality and coordinate checking
 */
public class Grid {
	int[][] board;
	Piece[] pieces;

	public Grid(int[][] board, Piece[] pieces) {
		this.pieces = pieces;
		this.board = board.clone();
	}
	
	public void move(Crd coord, Crd mv) {
		//Handling promotion
		if(board[coord.y][coord.x] == 1 && mv.y == 0) {
			board[coord.y][coord.x] = 5;
		}
		if(board[coord.y][coord.x] == 7 && mv.y == 7) {
			board[coord.y][coord.x] = 11;
		}
		//moving int values in board
		board[mv.y][mv.x] = board[coord.y][coord.x];
		board[coord.y][coord.x] = 0;
	}
	
	//Returns whether the given color is in checkmate
	public boolean checkMate(int color) {
		Mv[] temp = new Mv[200];
		if(getLegalMoves(temp, color) > 0) {
			return false;
		}
		return true;
	}
	
	//Returns the number of legal moves, stores them in list
	public int getLegalMoves(Mv[] list, int color) {
		int count = 0;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!colorCompare(board[i][j], color)) {
					Crd[] moves = pieces[board[i][j]].getMoves();
					//System.out.println("piece type" + board[i][j]);
					for(int q = 0; q < moves.length; q++) {
						if(isMoveLegal(new Crd(j, i), new Crd(j+moves[q].x, i+moves[q].y), moves[q], color)){
							if(colorCompare(color, board[i+moves[q].y][j+moves[q].x])) {
								list[count] = new Mv(i, j, i+moves[q].y, j+moves[q].x);
								count++;
							}
						}
					}
				}
			}
		}
		return count;
	}
	
	//Gets legal move count without list
	public int getLegalMoves(int color) {
		int count = 0;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!colorCompare(board[i][j], color)) {
					Crd[] moves = pieces[board[i][j]].getMoves();
					for(int q = 0; q < moves.length; q++) {
						if(isMoveLegal(new Crd(j, i), new Crd(j+moves[q].x, i+moves[q].y), moves[q], color)){
							if(colorCompare(color, board[i+moves[q].y][j+moves[q].x])) {
								count++;
							}
						}
					}
				}
			}
		}
		return count;
	}

	public boolean isMoveLegal(Crd init, Crd mv, Crd targetShift, int color) {
		if(validTarget(targetShift, board[init.y][init.x]) && systemChecks(init, mv)) {
			Grid flat = new Grid(cloneArray(), pieces);
			flat.move(init, mv);
			return !flat.inCheck(color);
		}
		return false;
	}
	
	//Checks if the move coordinates are within boundaries
	public boolean validTarget(Crd mv, int type) {
		Crd moves[] = pieces[type].getMoves();
		for(int i = 0; i < moves.length; i++) {
			if(mv.x == moves[i].x && mv.y == moves[i].y) {
				return true;
			}
		}
		//System.out.println("invalid target");
		return false;
	}
	
	//Returns whether the given color is in check
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
						if(y < 8 && y > -1 && x < 8 && x > -1) {
							if(board[y][x] == color) {
								if(systemChecks(new Crd(q,i),new Crd(x, y))) {
									//System.out.println("y = " + q + " x = " + i + " color " + board[q][i]);
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
	
	//Returns number of times the other color is currently attacking a given square
	public int squareTargets(int color, Crd start, Crd square) {
		int type, x, y;
		int totalAttacks = 0;
		for(int i = 0; i < 8; i++) {
			for(int q = 0; q < 8; q++) {
				type = board[i][q];
				if(type != 0 && colorCompare(type, color)) {
					if(q == start.x && i == start.y) {
						continue;
					}
					Crd[] moves = pieces[type].getMoves();
					for(int j = 0; j < moves.length; j++) {
						x = q + moves[j].x;
						y = i + moves[j].y;
						if(y < 8 && y > -1 && x < 8 && x > -1) {
							if(y == square.y && x == square.x) {
								//Fixing pawns attacking forward
								if(type == 1 || type == 7) {
									if(moves[j].x != 0) {
										totalAttacks++;
										//System.out.println("Attack confirmed: " + type);
										//System.out.println(type + " " + moves[j].x);
									}
								} else {
									if(systemChecks(new Crd(q,i),new Crd(x, y))) {
										totalAttacks++;
										//System.out.println("Attack confirmed: " + type);
									} else {
										//System.out.println("Check Failed. " + type);
									}
								}
							}
						}
					}
				}
			}
		}
		return totalAttacks;
	}

	public boolean systemChecks(Crd init, Crd mv) {
		boolean status = true;
		
		if(mv.y < 0 || mv.y > 7 || mv.x < 0 || mv.x > 7) {
			return false;
		}
		
		switch (board[init.y][init.x]) {
		case 3, 9: //bishop
			if(bishopCheck(init.x, init.y, mv.x, mv.y) == false) {
				return false;
			}
			break;		
		case 4, 10: //rook
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
		
		case 1, 7: //pawn
			if(pawnCheck(init.x, init.y, mv.x, mv.y) == false) {
				//System.out.println("pawn check failed");
				return false;
			}
		}
		return status;
	}
	
	public boolean pawnCheck(int startX, int startY, int endX, int endY) {
		if(Math.abs(startY - endY) == 2) { //checking if 2 square move with pawn
			if(board[startY][startX] == 1) {
				if(startY == 6) {//has the pawn moved yet? (black)
					return (board[endY][endX] == 0 && board[endY+1][endX] == 0);
				} else {
					return false;
				}
			} else {
				if(startY == 1) {//has the pawn moved yet? (white)
					return (board[endY][endX] == 0 && board[endY-1][endX] == 0);
				} else {
					return false;
				}
			}
		} else {
			if(startX == endX) {
				if(board[endY][endX] == 0) {
					return true;
				} else {
					return false;
				}
			} else {
				if(board[endY][endX] != 0) {
					return true;
				} else {
					return false;
				}
			}
		}
	}
	
	//Bishop obstacle scanning (also used for queen)
	public boolean bishopCheck(int startX, int startY, int endX, int endY) {
		int ycount;
		int xstart;
		int ystart;
		//setting correct diagonal for scanning
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
		
		for(int i = 0; i < Math.abs(startX - endX) - 1; i++) { 
			//scanning between source and destination for any pieces
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
			for(int i = beginning; i < end; i++) { //Scanning for pieces in path
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
			if(type2 > 6 && type1 > 6) {
				return false;
			}
			if(type2 <= 6 && type1 <= 6) {
				return false;
			}
			return true;
		} else {
			return true;
		}
	}
	
	//From online forum; not written by me
	public int[][] cloneArray() {
	    int length = board.length;
	    int[][] target = new int[length][board[0].length];
	    for (int i = 0; i < length; i++) {
	        System.arraycopy(board[i], 0, target[i], 0, board[i].length);
	    }
	    return target;
	}
}