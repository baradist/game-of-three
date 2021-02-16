package cf.baradist.gameofthree.dto;

import lombok.Data;

@Data
public class MoveDto {
    private String gameId;
    private int turnNumber;
    private int action;
}
