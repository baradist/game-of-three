package cf.baradist.gameofthree.exception;

public class IncorrectInitialSumException extends RuntimeException {
    public IncorrectInitialSumException(int sum) {
        super("Attempt to create a game with incorrect sum=" + sum);
    }
}
