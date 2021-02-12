package cf.baradist.gameofthree.event;

import cf.baradist.gameofthree.model.MoveAction;
import lombok.Data;

@Data
public class MoveEvent {
    private String gameId;
    private Integer number;
    private MoveAction action;
}
