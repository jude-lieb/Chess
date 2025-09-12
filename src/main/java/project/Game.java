package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
	int[] SET = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
    
    int[] MOVE_COUNTS = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 
	int[] PIECE_VALUES = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	Crd[][] PIECE_MOVES = new Crd[13][];

	int INITIAL_PIECE_COUNT = 16;
	int PIECE_TYPE_COUNT = 13;
	int BOARD_SIZE = 8;
	int START_COLOR = 6;

	int MIN_Y = 1;
	int MIN_X = 1;
	int MAX_Y = BOARD_SIZE;
	int MAX_X = BOARD_SIZE;
	
	MoveStack stack;
	ArrayList<Move> list;
	Move chosenMove;
	Player currentPlayer;
	Player white;
	Player black;

	int[][] board;
	int color;

	public Move verifyChosenMove() {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).isEqual(chosenMove)) {
				return list.get(i);
			}
		}
		return null;
	}

	public int getGameStatus() {
		if(list.isEmpty()) {
			return isSquareAttacked(currentPlayer.king) ? 1 : 2;
		} else {
			return 0;
		}
	}

	public int materialDiff() {
		return black.material - white.material;
	}

	public boolean inBounds(Crd dest) {
		return (dest.y <= MAX_Y && dest.y >= MIN_Y) && (dest.x <= MAX_X && dest.x >= MIN_X);
	}

	//Incomplete
	public void computerMove() {
		
	}

	//Incomplete
	public void move(Move mv) {

	}
	//Incomplete
	public void undoMove() {

	}

	//Incomplete
	public void canEnPassant() {
		
	}

	//Incomplete
	public void canCastle() {
		
	}

	public void findLegalMoves() {
		list.clear();
		canCastle();
		canEnPassant();

		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(!diffColor(board[i][j], color)) {
					Crd[] mvs = PIECE_MOVES[board[i][j]];
					for(int q = 0; q < mvs.length; q++) {
						Crd init = new Crd(i, j);
						Crd mv = new Crd(i+mvs[q].y, j+mvs[q].x);
						
						if(inBounds(mv) && diffColor(color, board[mv.y][mv.x]) && systemChecks(init, mv)) {
							int piece = board[init.y][init.x];
							Mod start = new Mod(init, piece, 0);

							//Checking for promotion and adjusting end location piece
							if((piece == 1 || piece == 7) && (mv.y == 1 || mv.y == 7)) {
								if(color < 7) {
									piece = white.promoteType;
								} else {
									piece = black.promoteType;
								}
							}

							Mod end = new Mod(mv, board[mv.y][mv.x], piece);

							Move stat = new Move(this, start, end, null, null);
							move(stat);
							boolean result = !isSquareAttacked(currentPlayer.king);
							undoMove();
							
							if(result) {
								list.add(stat);
							}
						}
					}
				}
			}
		}
	}

	public boolean isSquareAttacked(Crd square) {
		if(!inBounds(square))
			return false;
		int type, x, y;
		for(int i = 0; i < 8; i++) {
			for(int q = 0; q < 8; q++) {
				type = board[i][q];
				if(type != 0 && diffColor(type, color)) {
					Crd[] moves = PIECE_MOVES[type];
					for(int j = 0; j < moves.length; j++) {
						x = q + moves[j].x;
						y = i + moves[j].y;
						if(inBounds(new Crd(y, x))) {
							if(square.x == x && square.y == y) {
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

	//Incomplete
	public boolean systemChecks(Crd init, Crd mv) {
		return true;
	}
	//Incomplete
	public boolean rookCheck() {
		return true;
	}
	//Incomplete
	public boolean bishopCheck() {
		return true;
	}
	//Incomplete
	public boolean pawnCheck() {
		return true;
	}

	public void printBoard() {
		for(int i = 0; i < 8; i++) {
			System.out.println();
			for(int j = 0; j < 8; j++) {
				System.out.printf("%-2s ", board[i][j] != 0 ? board[i][j] : "_");
			}
		}
	}

	public static boolean diffColor(int type1, int type2) { 
		if(type2 != 0 && type1 != 0) {
			if((type2 > 6 && type1 > 6) || (type2 <= 6 && type1 <= 6)) {
				return false;
			}
		}
		return true;
	}

	public void reset() {
		white = new Player("white");
		black = new Player("black");

		board = new int[BOARD_SIZE][BOARD_SIZE];
		int piece;
		int count = 0; 
		for(int i = 0; i < BOARD_SIZE; i++) {
			for(int j = 0; j < BOARD_SIZE; j++) {
				piece = SET[count];
				board[i][j] = piece;

				//For king, set inital location
				if(piece == 6 || piece == 12) {
					if(piece == 6) {
						white.king = new Crd(i, j);
					} else {
						black.king = new Crd(i, j);
					}
				} else { //For other pieces, add material value to total
					if(SET[count] < 7) {
						white.material += PIECE_VALUES[SET[count]];
					} else {
						black.material += PIECE_VALUES[SET[count]];
					}
				}
				count++;
			}
		}
		color = START_COLOR;
		pastMoves = new MoveStack();
		list = new ArrayList<>();
		list.ensureCapacity(50);
		findLegalMoves();
    }

	public Game() {
		try {
			Scanner scan = new Scanner(new File("vectors.txt"));
			int read_Y, read_X;

			for(int i = 0; i < PIECE_TYPE_COUNT; i++) { 
				PIECE_MOVES[i] = new Crd[MOVE_COUNTS[i]];

				if(i > 7) {
					PIECE_MOVES[i] = PIECE_MOVES[i-INITIAL_PIECE_COUNT];
				} else {
					for(int j = 0; j < MOVE_COUNTS[i]; j++) {
						read_Y = Integer.parseInt(scan.next());
						read_X = Integer.parseInt(scan.next());
						PIECE_MOVES[i][j] = new Crd(read_Y, read_X);
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Error");
		}
    }
}

