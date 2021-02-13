package cf.baradist.gameofthree.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@IdClass(MoveId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Move implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game", nullable = false, referencedColumnName = "id")
    private Game game;
    @Id
    @Column(name = "number", nullable = false)
    private Integer number;

    private String initiator;
    @Enumerated(EnumType.ORDINAL)
    private MoveAction action;
}
