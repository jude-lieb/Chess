package project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class Socket extends TextWebSocketHandler {

    private final Map<WebSocketSession, Game> games = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New connection: " + session.getRemoteAddress());

        // Create and store game instance
        Game newGame = new Game(session);
        games.put(session, newGame);

        newGame.reset();
        newGame.updateGameStatus();
        newGame.sendBoard();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Game game = games.get(session);
        if (game == null) return;

        JSONObject json = new JSONObject(message.getPayload());
        String desc = json.getString("desc");

        // Determine purpose of the message
        if ("move request".equals(desc)) {
            JSONArray values = json.getJSONArray("crd");
            game.handleCrdInput(values);
        } else {
            game.handleCommand(desc);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Closed connection: " + session.getRemoteAddress());
        games.remove(session);

        System.out.println("Session ID: " + session.getId());
        System.out.println("Reason: " + status);
    }
}