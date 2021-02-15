package cf.baradist.gameofthree.controller;

import cf.baradist.gameofthree.event.CreateGameEvent;
import cf.baradist.gameofthree.event.CreatedGameEvent;
import cf.baradist.gameofthree.event.JoinGameEvent;
import cf.baradist.gameofthree.event.JoinedGameEvent;
import cf.baradist.gameofthree.event.MoveEvent;
import cf.baradist.gameofthree.event.MoveResult;
import cf.baradist.gameofthree.exception.GameNotFoundException;
import cf.baradist.gameofthree.model.Game;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService service;

    @GetMapping
    public List<Game> getAvailableGameSessions() {
        return service.getAvailableGameSessions();
    }

    @GetMapping("/{id}")
    public Game getById(@PathVariable String id) {
        return service.getById(id).orElseThrow(GameNotFoundException::new);
    }

    @PostMapping
    public CreatedGameEvent startGame(@RequestBody CreateGameEvent event, Principal principal) {
        String player = principal.getName();
        String gameId = service.startGame(player, event.getSum());
        return CreatedGameEvent.builder()
                .gameId(gameId)
                .build();
    }

    @PutMapping
    public JoinedGameEvent joinGame(@RequestBody JoinGameEvent event, Principal principal) {
        String player = principal.getName();
        service.joinGame(event.getGameId(), player);
        return JoinedGameEvent.builder()
                .gameId(event.getGameId())
                .playerId(player)
                .build();
    }

    @PostMapping("/{gameId}/move")
    public MoveResult move(@PathVariable String gameId,
                           @RequestBody MoveEvent event,
                           Principal principal) {
        return service.move(gameId, event.getMoveVersion(), principal.getName(), MoveAction.ofValue(event.getAction()));
    }
}
