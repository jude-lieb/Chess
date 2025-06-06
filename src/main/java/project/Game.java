package project;

import java.util.*;
import java.io.*;
import org.java_websocket.WebSocket;
import org.json.*;

/**
 * Game class
 * Holds information for a single client's game connection
 * Initialization and resetting of games
 * Processes user input
 */

public class Game {

    String[] names = {"blank.jpg", "wp.png", "wn.png","wb.png","wr.png","wq.png",
    		"wk.png", "bp.png","bn.png","bb.png","br.png","bq.png", "bk.png"};

	int[] set = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
    
    //Number of potential moves for each piece
    int[] moveAmount = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 

    //Relative material values of pieces (king arbitrary)
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	   
    //Stores possible move coordinate shifts for each type
    Crd[][] pieceMoves = new Crd[13][];

	//Game board instance
    Grid game;

    public Game() {
        //Preparing to read x and y shifts for each piece's moves
        File file = new File("vectors.txt");

		try {
			Scanner scan = new Scanner(file);
			int readInt1, readInt2;

			//Creating each piece type
			//Reading all potential piece coordinate shifts from text file
			for(int i = 0; i < 13; i++) { 
				pieceMoves[i] = new Crd[moveAmount[i]];

				if(i > 7) {
					pieceMoves[i] = pieceMoves[i-6];
				} else {
					for(int j = 0; j < moveAmount[i]; j++) {
						readInt1 = Integer.parseInt(scan.next());
						readInt2 = Integer.parseInt(scan.next());
						pieceMoves[i][j] = new Crd(readInt1, readInt2);
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Error");
		}

		reset();
    }

	public void handleCommand(String desc, WebSocket conn) {
		if(desc.equals("undo")) {
			game.undoMove();
			game.undoMove();
			game.findLegalMoves();
			sendBoard(conn);
			getOptions(conn);
		} else if(desc.equals("promote")) {
			game.changePromotion();
			sendPromote(conn);
		} else if(desc.equals("reset")) {
			reset();
			sendBoard(conn);
			sendPromote(conn);
			getOptions(conn);
		}
	}

	public void handleCrdInput(JSONArray move, WebSocket conn) {		
		CrdPair chosenMove = new CrdPair(move.getInt(0), move.getInt(1), 
			move.getInt(2), move.getInt(3));

		CrdPair result = game.isLegal(chosenMove);
		if(result != null) {
			//Player move
			game.move(new Move(game, result));
			sendBoard(conn);
			game.findLegalMoves();
			handleStatus(game.status(), conn);
			
			//Computer move response
			game.compMove();
			sendBoard(conn);
			game.findLegalMoves();
			handleStatus(game.status(), conn);

			getOptions(conn);
		}
	}

	public void getOptions(WebSocket conn) {
		int[][] holder = new int[64][28];
		int[] allowed = new int[64];

		for (int i = 0; i < game.legalMoveCount; i++) {
			Crd current = game.moves[i].getInit();
			int start = (8 * current.y) + current.x;

			holder[start][0] = start;
			allowed[start] = 0;

			for (int j = 0; j < game.legalMoveCount; j++) {
				if (game.moves[j].getInit().equals(current)) {
					holder[start][allowed[start]] = (8 * game.moves[j].endY) + game.moves[j].endX;
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
		message.put("value", game.promote);
		conn.send(message.toString());
	}

	public void sendBoard(WebSocket conn) {
		JSONArray boardState = new JSONArray();
		for (int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				boardState.put(game.board[i][j]);
			}
		}

		JSONObject message = new JSONObject();
		message.put("desc", "boardState");
		message.put("squares", boardState);
		message.put("value", game.promote);

		String jsonString = message.toString();
		conn.send(jsonString);
	}

	public void handleStatus(int status, WebSocket conn) {
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

	//User game initialization
	public void reset() {
        game = new Grid(set, pieceMoves, values, 39, 39, 6, 5);
    }
}

