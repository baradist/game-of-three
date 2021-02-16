package cf.baradist.gameofthree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveResultDto extends ResultDto {
    private int nextSum;
    private String nextTurn;
    private int nextTurnNumber;
    private String winner;
}
