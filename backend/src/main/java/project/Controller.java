package project;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = {"http://localhost:5173", "https://judelieb.com"})
@RestController
public class Controller {
    public Socket socket;

    @GetMapping("/new/multiplayer")
    public ResponseEntity<String> newMultiplayer(@RequestParam String sessionId) {
        String message = socket.addPlayerToQueue(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @GetMapping("/new/singleplayer")
    public ResponseEntity<String> newSinglePlayerGame(@RequestParam String sessionId, @RequestParam boolean color) {
        String message = socket.addSinglePlayer(sessionId, color);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    public Controller(Socket socket) {
        this.socket = socket;
    }
}
