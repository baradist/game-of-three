package cf.baradist.gameofthree.websocket;

import cf.baradist.gameofthree.dto.CreateGameDto;
import cf.baradist.gameofthree.dto.JoinGameDto;
import cf.baradist.gameofthree.dto.MoveDto;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class GameEventController {

    private final GameService service;

    @MessageMapping("/games/create")
    public void create(CreateGameDto message, Principal principal) {
        service.startGame(principal.getName(), message.getSum());
    }

    @MessageMapping("/games/join")
    public void join(JoinGameDto message, Principal principal) {
        service.joinGame(message.getGameId(), principal.getName());
    }

    @MessageMapping("/move")
    public void move(MoveDto message, Principal principal) {
        service.move(message.getGameId(),
                message.getTurnNumber(),
                principal.getName(),
                MoveAction.ofValue(message.getAction()));
    }
}
