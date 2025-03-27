package project;

import java.util.*;
import java.io.*;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import org.json.*;

public class Server extends WebSocketServer {

    String[] names = {"blank.jpg", "wp.png", "wn.png","wb.png","wr.png","wq.png",
    		"wk.png", "bp.png","bn.png","bb.png","br.png","bq.png", "bk.png"};
    
    int[] promoteTypes = {2,3,4,5};

	int[] set = {10,8,9,11,12,9,8,10,7,7,7,7,7,7,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,4,2,3,5,6,3,2,4};
    
    //Number of potential moves for each piece
    int[] moveAmount = {0, 4, 8, 28, 28, 56, 8, 4, 8, 28, 28, 56, 8}; 
    //Relative material values of pieces (king excluded)
	int[] values = {0,1,3,3,5,9,20,1,3,3,5,9,20};
	   
    //Stores potential move coordinate shifts for each type
    Piece[] pieces = new Piece[13];
    
    //User piece selection mode (start square or end square)
    boolean mode = true;
    
    //Start and end square coordinates
    Crd init = new Crd(0,0);
    Crd dest = new Crd(0,0);
    
    Grid gameGrid;
    CrdPair[] moves;

    public Server(int port) { 
        super(new InetSocketAddress(port)); 
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
		sendBoard(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received: " + message);

		JSONArray values = new JSONArray(message);
		int firstValue = values.getInt(0);
		int secondValue = values.getInt(1);
		System.out.println("First Value: " + firstValue);
		System.out.println("Second Value: " + secondValue);

		JSONObject response = new JSONObject();
		response.put("desc", "text");
		response.put("info", message);

		String jsonString = response.toString();
		conn.send(jsonString);
    }

    @Override
    public void onStart() {
        System.out.println("Starting server");
		initial();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }

    public static void main(String[] args) {
        Server server = new Server(3000);
        server.start();
        System.out.println("Server running on localhost:3000");
    }

	public void initial() {
        //User game created
        gameGrid = new Grid(set, pieces, 39, 39, 6, 5);
        
        //Preparing to read x and y shifts for each pieces' moves
        File file = new File("newMoves.txt");

		try {
			Scanner scan = new Scanner(file);
			int readInt1, readInt2;
			
			//Setting up list of images (one for each piece type)
			//Creating each piece type
			//Reading all potential piece coordinate shifts from text file
			for(int i = 0; i < 13; i++) { 
				//images[i] = new Image(getClass().getResource("/images/" + names[i]).toExternalForm());

				//images[i] = new Image(names[i]);
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
			System.out.println("error");
			System.exit(0);
		}
		//Finding legal moves in starting position
		updateLegalMoves();
		
		//Setting image views for visual board
	    int count = 0;
	    for(int y = 0; y < 8; y++) {
	    	for(int x = 0; x < 8; x++, count++) {
	    		//ImageView temp = new ImageView(images[set[count]]);
	    		//grid.getChildren().removeAll();
	    		//iViews[y][x] = temp;
	    		//temp.setFitHeight(80);
	    		//temp.setPreserveRatio(true);
	    		//grid.add(temp, x, y);
	    	}
	    }
	}

	public void sendBoard(WebSocket conn) {
		JSONArray boardState = new JSONArray();
		for (int i = 0; i <= 63; i++) {
			boardState.put(set[i]);
		}

		JSONObject message = new JSONObject();
		message.put("desc", "boardState");
		message.put("squares", boardState);

		String jsonString = message.toString();
		conn.send(jsonString);
	}


    public void updateLegalMoves() {
		moves = new CrdPair[100];
	    gameGrid.getLegalMoves(moves, gameGrid.color);
	}
	
	//Entering player move choices (updating board contents)
	public void enterMove(CrdPair move) {
		gameGrid.move(new Move(gameGrid, move));	
		updateImages(gameGrid);
	}
	
	//Entering computer move choices (updating board contents)
	public void computerPlay() {
		gameGrid.compMove();
		updateImages(gameGrid);
	}
	
	//Refreshing images on board using board array
	public void updateImages(Grid gameGrid) {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				//iViews[i][j].setImage(images[gameGrid.board[i][j]]);
			}
		}	
	}

	public void undo() {
		gameGrid.undoMove();
		updateImages(gameGrid);
		updateLegalMoves();
		gameGrid.print();
	}
	
	public void changePromotion() {
		if(gameGrid.promote < 5) {
			gameGrid.promote++;
		} else {
			gameGrid.promote = 2;
		}
		//changeBtn.setText("Promote " + gameGrid.promote);
	}
	
	public void exit() {
		System.exit(0);
	}
}

