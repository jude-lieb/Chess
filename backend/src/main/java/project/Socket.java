package project;

import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class Socket extends TextWebSocketHandler {

    private final Map<String, GameController> games = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public final Queue<String> players = new LinkedList<>();

    //Entering a connection into the multiplayer queue
    public String addPlayerToQueue(String sessionId) {
        if(sessions.get(sessionId) == null) return "inactive session";
        if(games.get(sessionId) != null) return "connection already attached to game";
        if(players.contains(sessionId)) return "already present in connection queue";

        players.add(sessionId); 
        checkQueue();
        return "Success";
    }

    //If enough players are in the queue, start a new multiplayer game
    public void checkQueue() {
        if(players.size() > 1) {
            String player1 = players.remove();
            String player2 = players.remove();

            if(player1 == null || player2 == null) {
                System.out.println("not enough active connections in the queue");
                return;
            }
            
            //Adding players to the new game controller
            GameController fresh = new GameController(player1, player2);
            games.put(player1, fresh);
            games.put(player2, fresh);

            //Starting the game and sending initial board state
            String gameMessage = fresh.initializeGame();
            broadcast(player1, gameMessage);
            broadcast(player2, gameMessage);
        }
    }

    //Adding a single player to the game controller map
    public String addSinglePlayer(String sessionId, boolean color) {
        if(sessions.get(sessionId) == null) return "inactive session";
        if(games.get(sessionId) != null) return "connection already attached to game";
        
        //Preventing double assignment into queue and singleplayer
        players.remove(sessionId);

        //A new single player game is started
        GameController fresh = new GameController(sessionId, null);
        games.put(sessionId, fresh);

        //Send initial game state
        broadcast(sessionId, fresh.initializeGame());

        return "Success";
    }

    //Used for initial multiplayer game startup
    public void broadcast(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        
        try {
            session.sendMessage(new TextMessage(message));
        } catch(Exception e) {
            System.out.println("bruh. broadcast failed");
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String response;
        GameController game = games.get(session.getId());
        if(game == null) response = "connection not attached to a game";
        
        JSONObject json = new JSONObject(message.getPayload());
        response = game.handleMessage(json);
        session.sendMessage(new TextMessage(response));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New connection: " + session.getRemoteAddress());
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Closed connection: " + session.getRemoteAddress());
        System.out.println("Session ID: " + session.getId());
        System.out.println("Reason: " + status);

        sessions.remove(session.getId());
        GameController game = games.remove(session.getId());
        if(game != null) game.handleClosedConnection();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error: " + exception.getMessage());
    }    
}