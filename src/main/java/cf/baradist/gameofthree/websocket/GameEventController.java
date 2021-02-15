package cf.baradist.gameofthree.websocket;

import cf.baradist.gameofthree.event.CreateGameEvent;
import cf.baradist.gameofthree.event.CreatedGameEvent;
import cf.baradist.gameofthree.event.JoinGameEvent;
import cf.baradist.gameofthree.event.JoinedGameEvent;
import cf.baradist.gameofthree.event.MoveEvent;
import cf.baradist.gameofthree.event.MoveResult;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.service.GameService;
import cf.baradist.gameofthree.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class GameEventController {

    private final GameService service;
    private final NotifyService notifyService;

    @MessageMapping("/games/create")
    public void create(CreateGameEvent message, Principal principal) {
        String gameId = service.startGame(principal.getName(), message.getSum());
        notifyService.broadcast(CreatedGameEvent.builder()
                .gameId(gameId)
                .build());
    }

    @MessageMapping("/games/join")
    public void join(JoinGameEvent message, Principal principal) {
        service.joinGame(message.getGameId(), principal.getName());
        notifyService.broadcast(JoinedGameEvent.builder()
                .gameId(message.getGameId())
                .playerId(principal.getName())
                .build());
    }

    @MessageMapping("/move")
    public void move(MoveEvent message, Principal principal) {
        MoveResult moveResult = service.move(message.getGameId(),
                message.getMoveVersion(),
                principal.getName(),
                MoveAction.ofValue(message.getAction()));
        notifyService.sendTo(moveResult.getNextTurn(), moveResult);
    }
}
