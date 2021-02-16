package cf.baradist.gameofthree.exception;

import cf.baradist.gameofthree.model.Game;

public class WrongSumException extends RuntimeException {
    public WrongSumException(Game game, String player, int nextSum) {
        super("Player=" + player + " tried to do a wrong move. "
                + nextSum + " is not divided by 3. Game=" + game.toString());
    }
}
