package cf.baradist.gameofthree.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinedGameEvent extends ResultEvent {
    private String gameId;
    private String playerId;
    private GameDto game;
}
