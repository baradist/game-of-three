package cf.baradist.gameofthree.model;

public enum MoveAction {
    DECREMENT(-1),
    DONT_CHANGE(0),
    INCREMENT(1);


    private final int value;

    MoveAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
