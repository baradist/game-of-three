package cf.baradist.gameofthree.exception;

import cf.baradist.gameofthree.model.Game;

public class WrongTurnException extends RuntimeException {
    public WrongTurnException(Game game, String player) {
        super("Player=" + player + " tried to move, although it wasn't their turn. Game=" + game.toString());
    }
}
