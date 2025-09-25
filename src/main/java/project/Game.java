package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

public class Game {
	int[] SET = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
    
    int[] MOVE_COUNTS = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 
	int[] PIECE_VALUES = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	Crd[][] PIECE_MOVES = new Crd[13][];

	int BOARD_SIZE = 8;
	int START_COLOR = 6;

	int MIN_Y = 1;
	int MIN_X = 1;
	int MAX_Y = BOARD_SIZE;
	int MAX_X = BOARD_SIZE;
	
	MoveStack stack;
	ArrayList<Move> list;
	Player currentPlayer;
	Player white;
	Player black;

	int[][] board;
	int color;
	int status;

	public Move isMoveLegal(Crd init, Crd dest) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).isEqual(init, dest)) {
				return list.get(i);
			}
		}
		return null;
	}

	public void updateGameStatus() {
		if(list.isEmpty()) {
			status = isSquareAttacked(currentPlayer.king) ? 1 : 2;
		} else {
			status = 0;
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
		mv.enter();
	}

	//Incomplete
	public void undoMove() {
		Move mv = stack.pop();
		mv.undo();
	}

	//Incomplete
	public void canEnPassant() {
		
	}

	//Incomplete
	public void canCastle() {
		
	}

	public void handleCommand(String desc, WebSocket conn) {
		if(desc.equals("undo")) {
			undoMove();
			undoMove();
			findLegalMoves();
			sendBoard(conn);
			getOptions(conn);
		} else if(desc.equals("promote")) {
			white.changePromotion();
			sendPromote(conn);
		} else if(desc.equals("reset")) {
			reset();
			sendBoard(conn);
			sendPromote(conn);
			getOptions(conn);
		}
	}

	public void handleCrdInput(JSONArray move, WebSocket conn) {		
		Crd temp1 = new Crd(move.getInt(0), move.getInt(1));
		Crd temp2 = new Crd(move.getInt(2), move.getInt(3));

		Move result = isMoveLegal(temp1, temp2);
		if(result != null) {
			//Player move
			move(result);
			sendBoard(conn);
			findLegalMoves();
			handleStatus(conn);
			
			//Computer move response
			computerMove();
			sendBoard(conn);
			findLegalMoves();
			handleStatus(conn);

			getOptions(conn);
		}
	}

	public void getOptions(WebSocket conn) {
		int[][] holder = new int[64][28];
		int[] allowed = new int[64];

		for (int i = 0; i < list.size(); i++) {
			Crd current = list.get(i).getInit();
			int start = (8 * current.y) + current.x;

			holder[start][0] = start;
			allowed[start] = 0;

			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).getInit().equals(current)) {
					holder[start][allowed[start]] = (8 * list.get(j).getDest().y) 
						+ list.get(j).getDest().x;
					allowed[start]++;
				}
			}
		}

		JSONArray options = new JSONArray();
		for (int i = 0; i < 64; i++) {
			JSONArray moveset = new JSONArray();

			for (int j = 0; j < allowed[i]; j++) {
				moveset.put(holder[i][j]);
			}
			options.put(moveset);
		}

		JSONObject message = new JSONObject();
		message.put("desc", "loadSelect");
		message.put("options", options);
		conn.send(message.toString());
	}

	public void sendPromote(WebSocket conn) {
		JSONObject message = new JSONObject();
		message.put("desc", "promote");
		message.put("value", white.promoteType);
		conn.send(message.toString());
	}

	public void sendBoard(WebSocket conn) {
		JSONArray boardState = new JSONArray();
		for (int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				boardState.put(board[i][j]);
			}
		}

		JSONObject message = new JSONObject();
		message.put("desc", "boardState");
		message.put("squares", boardState);
		message.put("value", white.promoteType);

		String jsonString = message.toString();
		conn.send(jsonString);
	}

	public void handleStatus(WebSocket conn) {
		JSONObject message = new JSONObject();
		message.put("desc", "status");

		if(status != 0) {
			if(status == 1) {
				message.put("status", "Draw by stalemate!");
			} else {
				message.put("status", "Checkmate!");
			}
		} else {
			message.put("status", "   ");
		}
		conn.send(message.toString());
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

	//Makes sure a piece has no obstacles in between start and destination
	public boolean systemChecks(Crd init, Crd mv) {
		switch(board[init.y][init.x]) {
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
		stack = new MoveStack();
		list = new ArrayList<>();
		list.ensureCapacity(50);
		findLegalMoves();
    }

	public Game() {
		try {
			Scanner scan = new Scanner(new File("vectors.txt"));
			int read_Y, read_X;

			for(int i = 0; i < 13; i++) { 
				System.out.println(i);
				PIECE_MOVES[i] = new Crd[MOVE_COUNTS[i]];

				if(i > 7) {
					PIECE_MOVES[i] = PIECE_MOVES[i-6];
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

