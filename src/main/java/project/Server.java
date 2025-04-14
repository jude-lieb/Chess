package project;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import org.json.*;

public class Server extends WebSocketServer {

	private final Map<WebSocket, Game> games = new ConcurrentHashMap<>();
    
    public Server(int port) { 
        super(new InetSocketAddress(port)); 
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());

		//Adding game to session map
		Game newGame = new Game();
		games.put(conn, newGame);

		newGame.sendBoard(conn);
		newGame.sendPromote(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
		Game game = games.get(conn);
		if (game == null) return;

		JSONObject json = new JSONObject(message);
		String desc = json.getString("desc");
		System.out.println("Message Received");
		System.out.println(desc);

		//Determine purpose of the message using description
		if(desc.equals("coordinate")) {
			JSONArray values = json.getJSONArray("crd");
			int firstValue = values.getInt(0);
			int secondValue = values.getInt(1);
			game.handleCrdInput(firstValue, secondValue, conn);
		} else {
			game.handleCommand(desc, conn);
		}
    }

    @Override
    public void onStart() {
        System.out.println("Starting server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
		games.remove(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        System.err.println("Error: " + e.getMessage());
    }

    public static void main(String[] args) {
        Server server = new Server(3000);
        server.start();
        System.out.println("Server running on localhost:3000");
    }
}

