package cf.baradist.gameofthree.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveResult extends ResultEvent {
    private int nextSum;
    private String nextTurn;
    private int nextTurnNumber;
    private String winner;
}
