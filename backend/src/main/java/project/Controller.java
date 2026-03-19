package project;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class Controller {

    private final Map<String, Game> games = new ConcurrentHashMap<>();

    @GetMapping("/new")
    public ResponseEntity<String> startNewGame() {
        String gameId = UUID.randomUUID().toString();
		Game newGame = new Game();
		games.put(gameId, newGame);
        newGame.init();
        newGame.updateGameStatus();
        return ResponseEntity.status(201).body(gameId);
    }

    @PostMapping("/move/{gameId}")
    public ResponseEntity<String> postMethodName(@PathVariable String gameId, @RequestBody MoveRequest move) {

        Game game = games.get(gameId);
        game.handleCrdInput(move);
        return ResponseEntity.status(200).body("move entered");
    }

    @GetMapping("/board/{gameId}")
    public ResponseEntity<BoardState> loadBoard(@PathVariable String gameId) {

        Game game = games.get(gameId);
        BoardState state = game.getBoard();
        return ResponseEntity.status(200).body(state);
    }

    @GetMapping("/undoMove/{gameId}")
    public ResponseEntity<String> undo(@PathVariable String gameId) {

        Game game = games.get(gameId);
        game.handleUndo();
        return ResponseEntity.status(200).body("move undone");
    }
    
}
