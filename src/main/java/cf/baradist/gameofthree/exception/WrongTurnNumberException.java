package cf.baradist.gameofthree.exception;

import cf.baradist.gameofthree.model.Game;

public class WrongTurnNumberException extends RuntimeException {
    public WrongTurnNumberException(Game game, String player, int turnNumber) {
        super("Player=" + player + " tried to move, although it was a wrong turn number=" + turnNumber
                + ". Game=" + game.toString());
    }
}
