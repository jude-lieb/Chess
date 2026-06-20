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
        return "Added to Queue!";
    }

    //If enough players are in the queue, start a new multiplayer game
    public void checkQueue() {
        if(players.size() > 1) {
            WebSocketSession player1 = sessions.get(players.remove());
            WebSocketSession player2 = sessions.get(players.remove());

            if(player1 == null || player2 == null) {
                System.out.println("not enough active connections in the queue");
                return;
            }
            
            //Adding players to the new game controller
            GameController fresh = new GameController(player1, player2);
            games.put(player1.getId(), fresh);
            games.put(player2.getId(), fresh);

            //Starting the game and sending initial board state
            fresh.initializeGame();
        }
    }

    //Adding a single player to the game controller map
    public String addSinglePlayer(String sessionId, boolean color) {
        WebSocketSession session = sessions.get(sessionId);
        if(session == null) return "inactive session";
        if(games.get(sessionId) != null) return "connection already attached to game";
        
        //Preventing double assignment into queue and singleplayer
        players.remove(sessionId);

        //A new single player game is started
        GameController fresh = new GameController(session, color);
        games.put(sessionId, fresh);

        System.out.println("createNewSingleplayer");
        //Send initial game state
        fresh.initializeGame();
        return "singleplayer";
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        GameController game = games.get(session.getId());
        if(game == null) {
            JSONObject temp = new JSONObject();
            temp.put("desc", "connection not attached to a game");
            session.sendMessage(new TextMessage(temp.toString()));
        } else {
            JSONObject json = new JSONObject(message.getPayload());
            game.handleMessage(json, session);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New connection: " + session.getId());
        sessions.put(session.getId(), session);

        JSONObject res = new JSONObject();  
        res.put("desc", "sessionId");
        res.put("sessionId", session.getId());
        session.sendMessage(new TextMessage(res.toString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Closed connection: " + session.getRemoteAddress());
        System.out.println("Session ID: " + session.getId());
        System.out.println("Reason: " + status);

        sessions.remove(session.getId());
        games.remove(session.getId());
        players.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error: " + exception.getMessage());
    }    
}