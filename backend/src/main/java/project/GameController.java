package project;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Random;

public class GameController {
    public String player1;
    public String player2;
    public Game game;
    boolean color;
    boolean isMulti;
    Random rand = new Random();

    public String initializeGame() {
        game = new Game(color);
        game.updateGameStatus();
        return game.sendBoard();
    }

    public String handleMessage(JSONObject json) {
        String desc = json.getString("desc");

        if("move request".equals(desc)) {
            JSONArray values = json.getJSONArray("crd");
            return game.handleCrdInput(values);
        }
        if("undo move".equals(desc)) {
            return game.handleUndo();
        }

        return "Invalid message or request";
    }

    //Cleanup process
    public void handleClosedConnection() {
        
    }

    public GameController(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.color = rand.nextBoolean();
        this.isMulti = true;
    }

    public GameController(String player, boolean color) {
       this.player1 = player;
       this.player2 = null;
       this.color = color;
       this.isMulti = false;
    }
}
