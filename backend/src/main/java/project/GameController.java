package project;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Random;

public class GameController {
    public WebSocketSession player1;
    public WebSocketSession player2;
    public Game game;
    boolean color;
    boolean isMulti;
    Random rand = new Random();

    public void initializeGame() {
        System.out.println("sent game initial state");
        game = new Game(color, isMulti);
        game.updateGameStatus();
        JSONObject message = game.sendBoard();
        refresh(game, message);
    }

    public void refresh(Game game, JSONObject message) {
        if(isMulti) {
            if(!game.currentPlayer.color) {
                message.put("turn", player2.getId());
            } else {    
                message.put("turn", player1.getId());
            }

            message.put("flipped", player2.getId());
            sendMessage(player1, message);
            sendMessage(player2, message);
        } else {
            message.put("turn", player1.getId());
            if(color == false) {
                message.put("flipped", player1.getId());
            }
            sendMessage(player1, message);
        }
    }

    public void handleMessage(JSONObject json, WebSocketSession session) {
        String desc = json.getString("desc");
        JSONObject message = null;
        
        if("move request".equals(desc)) {
            JSONArray values = json.getJSONArray("crd");
            message = game.handleCrdInput(values);
        } else if("undo".equals(desc)) {
            message = game.handleUndo();
        } else {
            return;
        }
        refresh(game, message);
    }

    public void sendMessage(WebSocketSession session, JSONObject message) {
        try {
            String res = message.toString();
            System.out.println(res);
            session.sendMessage(new TextMessage(res));
        } catch(Exception e) {
            System.out.println("board state sending failed.");
        }
    }

    public GameController(WebSocketSession player1, WebSocketSession player2) {
        this.color = rand.nextBoolean();
        if(color) {
            this.player1 = player2;
            this.player2 = player1;
        } else {
            this.player1 = player1;
            this.player2 = player2;
        }
        this.isMulti = true;
    }

    public GameController(WebSocketSession player, boolean color) {
       this.player1 = player;
       this.player2 = null;
       this.color = color;
       this.isMulti = false;
    }
}
