package cf.baradist.gameofthree.exception;

import cf.baradist.gameofthree.model.Game;

public class UserAlreadyParticipatedException extends RuntimeException {
    public UserAlreadyParticipatedException(Game game, String player) {
        super("Player=" + player + " tried to join a game, although they were started this game. Game="
                + game.toString());
    }
}
