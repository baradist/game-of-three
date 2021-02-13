package cf.baradist.gameofthree.event;

import lombok.Data;

@Data
public class MoveEvent {
    private String gameId;
    private int number;
    private int action;
}
