package cf.baradist.gameofthree.websocket;

import cf.baradist.gameofthree.dto.CreateGameDto;
import cf.baradist.gameofthree.dto.GameDto;
import cf.baradist.gameofthree.dto.JoinGameDto;
import cf.baradist.gameofthree.dto.MoveDto;
import cf.baradist.gameofthree.dto.MoveResultDto;
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
    public void create(CreateGameDto message, Principal principal) {
        GameDto gameDto = service.startGame(principal.getName(), message.getSum());
        notifyService.broadcast(gameDto);
    }

    @MessageMapping("/games/join")
    public void join(JoinGameDto message, Principal principal) {
        GameDto gameDto = service.joinGame(message.getGameId(), principal.getName());
        notifyService.broadcast(gameDto);
    }

    @MessageMapping("/move")
    public void move(MoveDto message, Principal principal) {
        String player = principal.getName();
        MoveResultDto moveResult = service.move(message.getGameId(),
                message.getTurnNumber(),
                player,
                MoveAction.ofValue(message.getAction()));
        notifyService.sendTo(moveResult.getNextTurn(), moveResult);
        notifyService.sendTo(player, moveResult);
    }
}
