package cf.baradist.gameofthree.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveResult {
    private boolean finished;
    private int sum;
}
