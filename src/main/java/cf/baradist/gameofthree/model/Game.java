package cf.baradist.gameofthree.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game implements Serializable {
    @Id
    private String id;
    private String player1;
    private String player2;
    private String nextTurn;
    private int sum;
    private String winner;
    private int turns;
}
