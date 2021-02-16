package cf.baradist.gameofthree.controller;

import cf.baradist.gameofthree.dto.CreatGameResultDto;
import cf.baradist.gameofthree.dto.CreateGameDto;
import cf.baradist.gameofthree.dto.JoinGameDto;
import cf.baradist.gameofthree.dto.JoinGameResultDto;
import cf.baradist.gameofthree.dto.MoveDto;
import cf.baradist.gameofthree.dto.MoveResultDto;
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

    @GetMapping("/current")
    public Game getCurrentByPlayer(Principal principal) {
        return service.getCurrentGameByPlayer(principal.getName()).orElseThrow(GameNotFoundException::new);
    }

    @PostMapping
    public CreatGameResultDto startGame(@RequestBody CreateGameDto event, Principal principal) {
        String player = principal.getName();
        String gameId = service.startGame(player, event.getSum()).getId();
        return CreatGameResultDto.builder()
                .gameId(gameId)
                .build();
    }

    @PutMapping
    public JoinGameResultDto joinGame(@RequestBody JoinGameDto event, Principal principal) {
        String player = principal.getName();
        service.joinGame(event.getGameId(), player);
        return JoinGameResultDto.builder()
                .gameId(event.getGameId())
                .playerId(player)
                .build();
    }

    @PostMapping("/{gameId}/move")
    public MoveResultDto move(@PathVariable String gameId,
                              @RequestBody MoveDto event,
                              Principal principal) {
        return service.move(gameId, event.getTurnNumber(), principal.getName(), MoveAction.ofValue(event.getAction()));
    }
}
