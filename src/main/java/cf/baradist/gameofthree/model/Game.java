package cf.baradist.gameofthree.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    private String id;
    private String player1;
    private String player2;
    private String nextTurn;
    private int sum;
    private boolean finished;
    private String winner;
}
