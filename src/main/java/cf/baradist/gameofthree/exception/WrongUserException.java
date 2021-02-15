package cf.baradist.gameofthree.exception;

import cf.baradist.gameofthree.model.Game;

public class WrongUserException extends RuntimeException {
    public WrongUserException(Game game, String player) {
        super("Player=" + player + " tried to move, although they aren't in the player-list of the game. Game="
                + game.toString());
    }
}
