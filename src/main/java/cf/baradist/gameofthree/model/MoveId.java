package cf.baradist.gameofthree.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class MoveId implements Serializable {
    private String game;
    private Integer number;
}
