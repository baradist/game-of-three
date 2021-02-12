package cf.baradist.gameofthree.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinedGameEvent {
    private String gameId;
    private String playerId;
}
