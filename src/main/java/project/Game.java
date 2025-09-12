package project;

import java.util.*;
import java.io.*;
import org.java_websocket.WebSocket;
import org.json.*;

public class Game {
	int[] SET = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
    
    int[] MOVE_COUNTS = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 
	int[] PIECE_VALUES = {0,1,3,3,5,9,20,1,3,3,5,9,20};
    Crd[][] PIECE_MOVES = new Crd[13][];
	int INITIAL_PIECE_COUNT = 16;
	int PIECE_TYPE_COUNT = 13;
	int BOARD_SIZE = 8;
	boolean BLACK = false;
	boolean WHITE = true;
	boolean START_COLOR = true;
	boolean DEFAULT_PROMOTE_WHITE = true;
	boolean DEFAULT_PROMOTE_BLACK = false;
	boolean DEFAULT_HASMOVED = false;
	boolean CASTLING_ENABLED = true;
	boolean CASTLING_DISABLED = false;

	int MIN_Y = MIN_X = 1;
	int MAX_Y = MAX_X = BOARD_SIZE;
	
	MoveStack pastMoves;
	CrdPair[] moves;
	int[][] board;
	int legalMoveCount, color, promote;
	int bMat, wMat;
	boolean bK, wK, bQ, wQ;

	public CrdPair verifyChosenMove() {
		for(int i = 0; i < 100 && moves[i] != null; i++) {
			if(moves[i].equals(chosenMove)) {
				return moves[i];
			}
		}
		return null;
	}

	public int getGameStatus() {
		if(legalMoveCount == 0) {
			return inCheck(color) ? CHECKMATE : STALEMATE;
		} else {
			return CAN_MOVE;
		}
	}

	public void computerMove() {
		if(legalMoveCount == 0) return;
		Eval ev = new Eval(this);
		move(ev.getBestMove());
	}

	public void move() {

	}

	public void undoMove() {

	}

	public int canEnPassant() {

	}

	public boolean inBounds() {
		return (dest.y <= MAX_Y && dest.y >= MIN_Y) && (dest.x <= MAX_X && dest.x >= MIN_X);
	}

	public int canCastle() {

	}

	public void findLegalMoves() {

	}

	public void isMoveLegal() {
		
	}

	public boolean inCheck() {

	}

	public boolean isSquareAttacked() {

	}

	public boolean systemChecks() {

	}

	public boolean rookCheck() {

	}

	public boolean bishopCheck() {
		
	}

	public boolean pawnCheck() {
		
	}

	public void printBoard() {
		for(int i = 0; i < 8; i++) {
			System.out.println();
			for(int j = 0; j < 8; j++) {
				System.out.printf("%-2s ", board[i][j] != 0 ? board[i][j] : "_");
			}
		}
	}

	public void reset() {
		board = new int[BOARD_SIZE][BOARD_SIZE];
		
		int count = 0; 
		for(int i = 0; i < BOARD_SIZE; i++) {
			for(int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = SET[count];
				count++;
			}
		}
		
		color = START_COLOR;
		promoteW = DEFAULT_PROMOTE_WHITE;
		promoteB = DEFUALT_PROMOTE_BLACK;
		bKingSide = wKingSide = CASTLING_ENABLED;
		BQueenside = wQueenSide = CASTLING_ENABLED;

		bMat = countMaterial(BLACK);
		wMat = countMaterial(WHITE);
		pastMoves = new MoveStack();
		findLegalMoves();
    }

	public Game() {
		try {
			Scanner scan = new Scanner(new File("vectors.txt"));
			int read_Y, read_X;

			for(int i = 0; i < PIECE_TYPE_COUNT; i++) { 
				pieceMoves[i] = new Crd[moveAmount[i]];

				if(i > 7) {
					pieceMoves[i] = pieceMoves[i-INITIAL_PIECE_COUNT];
				} else {
					for(int j = 0; j < moveAmount[i]; j++) {
						read_Y = Integer.parseInt(scan.next());
						read_X = Integer.parseInt(scan.next());
						pieceMoves[i][j] = new Crd(read_Y, read_X);
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Error");
		}
    }
}

