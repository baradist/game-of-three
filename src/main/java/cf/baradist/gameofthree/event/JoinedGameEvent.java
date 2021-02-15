package cf.baradist.gameofthree.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinedGameEvent extends ResultEvent {
    private String gameId;
    private String playerId;
    private GameDto game;
}
