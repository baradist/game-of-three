package cf.baradist.gameofthree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinGameResultDto extends ResultDto {
    private String gameId;
    private String playerId;
    private GameDto game;
}
