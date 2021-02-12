package cf.baradist.gameofthree.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
//@Table(indexes = {
//        @Index(columnList = "game, number", unique = true)
//})
@Data
public class Move implements Serializable {
    @ManyToOne
    @Id
    private Game game;
    @Id
    private Integer number;

    private String initiator;
    @Enumerated(EnumType.ORDINAL)
    private MoveAction action;
}
