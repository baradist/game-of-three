package cf.baradist.gameofthree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto extends ResultDto {
    private String id;
    private String player1;
    private String player2;
    private String nextTurn;
    private int sum;
    private String winner;
    private int turns;
}
