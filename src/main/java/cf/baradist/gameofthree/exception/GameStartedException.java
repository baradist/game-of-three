package cf.baradist.gameofthree.exception;

import cf.baradist.gameofthree.model.Game;

public class GameStartedException extends RuntimeException {
    public GameStartedException(Game game, String player) {
        super("Player=" + player + " tried to join a game, although it has been started already. Game=" + game.toString());
    }
}
