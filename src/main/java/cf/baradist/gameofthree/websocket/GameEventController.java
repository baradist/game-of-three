package cf.baradist.gameofthree.websocket;

import cf.baradist.gameofthree.event.CreateGameEvent;
import cf.baradist.gameofthree.event.GameDto;
import cf.baradist.gameofthree.event.JoinGameEvent;
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
        GameDto gameDto = service.startGame(principal.getName(), message.getSum());
        notifyService.broadcast(gameDto);
    }

    @MessageMapping("/games/join")
    public void join(JoinGameEvent message, Principal principal) {
        GameDto gameDto = service.joinGame(message.getGameId(), principal.getName());
//        JoinedGameEvent joinedGameEvent = JoinedGameEvent.builder()
//                .gameId(message.getGameId())
//                .playerId(principal.getName())
//                .build();
        notifyService.broadcast(gameDto);
    }

    @MessageMapping("/move")
    public void move(MoveEvent message, Principal principal) {
        String player = principal.getName();
        MoveResult moveResult = service.move(message.getGameId(),
                message.getMoveVersion(),
                player,
                MoveAction.ofValue(message.getAction()));
        notifyService.sendTo(moveResult.getNextTurn(), moveResult);
        notifyService.sendTo(player, moveResult);
    }
}
