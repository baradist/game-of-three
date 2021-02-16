package cf.baradist.gameofthree.service;

import cf.baradist.gameofthree.event.AbstractResultEvent;
import cf.baradist.gameofthree.event.MoveResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifyService implements ApplicationListener<AbstractResultEvent> {

    private final SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(AbstractResultEvent event) {
        if (event instanceof MoveResultEvent) {
            ((MoveResultEvent) event).getReceivers().forEach(player ->
                    template.convertAndSendToUser(player, "/queue/games", event.getResultDto()));
        } else if (event instanceof AbstractResultEvent) {
            template.convertAndSend("/topic/games", event.getResultDto());
        }
    }
}
