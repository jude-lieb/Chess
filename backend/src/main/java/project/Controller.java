package project;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    public Socket socket;

    @GetMapping("/new/multiplayer")
    public String newMultiplayer(@RequestParam String sessionId) {
        return socket.addPlayerToQueue(sessionId);
    }

    @GetMapping("/new/singleplayer")
    public String newSinglePlayerGame(@RequestParam String sessionId, @RequestParam boolean color) {
        return socket.addSinglePlayer(sessionId, color);
    }

    public Controller(Socket socket) {
        this.socket = socket;
    }
}
