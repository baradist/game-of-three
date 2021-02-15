package cf.baradist.gameofthree.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveResult extends ResultEvent {
    private boolean finished;
    private int nextSum;
    private String nextTurn;
    private int nextMoveVersion;
    private String winner;
}
