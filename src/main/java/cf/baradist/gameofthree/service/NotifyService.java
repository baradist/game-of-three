package cf.baradist.gameofthree.service;

import cf.baradist.gameofthree.event.ResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifyService {

    private final SimpMessagingTemplate template;
    private final SimpUserRegistry userRegistry;

    public void broadcast(ResultEvent event) {
        template.convertAndSend("/topic/games", event);
    }

    public void sendTo(String player, ResultEvent event) {
        template.convertAndSendToUser(player, "/queue/games", event);
    }
}
