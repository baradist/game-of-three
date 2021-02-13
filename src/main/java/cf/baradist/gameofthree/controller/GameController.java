package cf.baradist.gameofthree.controller;

import cf.baradist.gameofthree.event.JoinGameEvent;
import cf.baradist.gameofthree.event.JoinedGameEvent;
import cf.baradist.gameofthree.event.MoveEvent;
import cf.baradist.gameofthree.event.MoveResult;
import cf.baradist.gameofthree.event.StartGameEvent;
import cf.baradist.gameofthree.event.StartedGameEvent;
import cf.baradist.gameofthree.model.Game;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
public class GameController {
    private final GameService service;

    @GetMapping
    public List<Game> getAvailableGameSessions() {
        return service.getAvailableGameSessions();
    }

    @PostMapping
    public StartedGameEvent startGame(@RequestBody StartGameEvent event,
                                      @RequestHeader("Player") String player) {
        String gameId = service.startGame(player, event.getSum());
        return StartedGameEvent.builder()
                .gameId(gameId)
                .build();
    }

    @PutMapping
    public JoinedGameEvent joinGame(@RequestBody JoinGameEvent event,
                                    @RequestHeader("Player") String player) {
        service.joinGame(event.getGameId(), player);
        return JoinedGameEvent.builder()
                .gameId(event.getGameId())
                .playerId(player)
                .build();
    }

    @PostMapping("/{gameId}/move")
    public MoveResult move(@PathVariable String gameId,
                           @RequestBody MoveEvent event,
                           @RequestHeader("Player") String player) {
        return service.move(gameId, event.getNumber(), player, MoveAction.ofValue(event.getAction()));
    }
}
