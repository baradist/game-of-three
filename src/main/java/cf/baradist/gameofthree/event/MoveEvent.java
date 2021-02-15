package cf.baradist.gameofthree.event;

import lombok.Data;

@Data
public class MoveEvent {
    private String gameId;
    private int moveVersion;
    private int action;
}
