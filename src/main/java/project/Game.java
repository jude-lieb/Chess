package project;

import java.util.*;
import java.io.*;
import org.java_websocket.WebSocket;
import org.json.*;

/**
 * Game class
 * Holds information for a single client's game connection
 * Initialization and resetting of games
 * Handles piece selection
 */

public class Game {

    String[] names = {"blank.jpg", "wp.png", "wn.png","wb.png","wr.png","wq.png",
    		"wk.png", "bp.png","bn.png","bb.png","br.png","bq.png", "bk.png"};
    
    int[] promoteTypes = {2,3,4,5};

	int[] set = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
    
    //Number of potential moves for each piece
    int[] moveAmount = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 

    //Relative material values of pieces (king arbitrary)
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	   
    //Stores potential move coordinate shifts for each type
    Piece[] pieces = new Piece[13];
    
    //User piece selection mode (start square or end square)
    boolean mode;
    
    //Start and end square coordinates
    Crd init;
    Crd dest;

	//Holding which squares need to be selected or deselected
	int[] squares;

	//Game board instance
    Grid gameGrid;

    public Game() {
        //Preparing to read x and y shifts for each pieces' moves
        File file = new File("newMoves.txt");

		try {
			Scanner scan = new Scanner(file);
			int readInt1, readInt2;

			//Creating each piece type
			//Reading all potential piece coordinate shifts from text file
			for(int i = 0; i < 13; i++) { 
				Crd[] temp = new Crd[moveAmount[i]];
				for(int j = 0; j < moveAmount[i]; j++) {
					readInt1 = Integer.parseInt(scan.next());
					readInt2 = Integer.parseInt(scan.next());
					temp[j] = new Crd(readInt1, readInt2);
				}
				pieces[i] = new Piece(i, temp);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Error");
		}

		reset();
    }

	public void handleCommand(String desc, WebSocket conn) {
		if(desc.equals("undo")) {
			gameGrid.undoMove();
			gameGrid.undoMove();
			toggleSelect(squares, false, conn);
			sendBoard(conn);
		} else if(desc.equals("promote")) {
			changePromotion();
			sendPromote(conn);
		} else if(desc.equals("reset")) {
			reset();
            sendBoard(conn);
            sendPromote(conn);
		}
	}

	public void handleCrdInput(int y, int x, WebSocket conn) {
		//remove highlights around squares
		toggleSelect(squares, false, conn);
		
		if(mode) { //Selecting starting square
			init = new Crd(y, x); 
			if(!Grid.colorCompare(gameGrid.board[init.y][init.x], gameGrid.color)) {
				if(gameGrid.board[init.y][init.x] != 0) {
					//set image to larger size or highlight it
					mode = false;

					int[] sq = new int[gameGrid.legalMoveCount];
					int count = 0;

					for(int i = 0; i < gameGrid.legalMoveCount; i++) {
						if(gameGrid.moves[i].getInit().equals(init)) {
							sq[count] = (8 * gameGrid.moves[i].getDest().y) + gameGrid.moves[i].getDest().x;
							count = count + 1;
						}
					}

					squares = new int[count+1];

					for(int i = 0; i < count; i++) {
						squares[i] = sq[i];
					}
					squares[count] = (8 * init.y) + init.x;
					toggleSelect(squares, true, conn);
				}	
			}
		} else { //Selecting destination square
			dest = new Crd(y, x);
			CrdPair chosenMove = new CrdPair(init.y, init.x, y, x);
			
			CrdPair result = gameGrid.isLegal(chosenMove);
			if(result != null) {
				//Player move
				gameGrid.move(new Move(gameGrid, result));
				sendBoard(conn);
				gameGrid.findLegalMoves();
				handleStatus(gameGrid.status(), conn);
				
				//Computer move response
				gameGrid.compMove();
				sendBoard(conn);
				gameGrid.findLegalMoves();
				handleStatus(gameGrid.status(), conn);
				//gameGrid.print();
			}
			
			mode = true;
		}
	}

	public void sendPromote(WebSocket conn) {
		JSONObject message = new JSONObject();
		message.put("desc", "promote");
		message.put("value", gameGrid.promote);
		String jsonString = message.toString();
		conn.send(jsonString);
	}

	public void sendBoard(WebSocket conn) {
		JSONArray boardState = new JSONArray();
		for (int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				boardState.put(gameGrid.board[i][j]);
			}
		}

		JSONObject message = new JSONObject();
		message.put("desc", "boardState");
		message.put("squares", boardState);
		message.put("value", gameGrid.promote);

		String jsonString = message.toString();
		conn.send(jsonString);
	}

	//Changing the promotion piece type selection
	public void toggleSelect(int squares[], boolean status, WebSocket conn) {
		if(squares == null) {
			return;
		}
		JSONArray crd = new JSONArray();
		
		for(int i = 0; i < squares.length; i++) {
			crd.put(squares[i]);
		}
		
		JSONObject message = new JSONObject();
		
		if(status) {
			message.put("desc", "select");
		} else {
			message.put("desc", "deselect");
		}
		message.put("squares", crd);
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
		String jsonString = message.toString();
		conn.send(jsonString);
	}

	public void reset() {
        mode = true;
        init = new Crd(0,0);
        dest = new Crd(0,0);

        //User game created
        gameGrid = new Grid(set, pieces, 39, 39, 6, 5);
    }
	
	public void changePromotion() {
		if(gameGrid.promote < 5) {
			gameGrid.promote++;
		} else {
			gameGrid.promote = 2;
		}
	}
	
	public void exit() {
		System.exit(0);
	}
}

