package src;

public class EightPuzzleMove {

    public String previousMoveId;
    public String layout;

    public EightPuzzleMove(String previousMoveId, String layout)
    {
        this.previousMoveId = previousMoveId;
        this.layout = layout;
    }

    @Override
    public String toString() {
        return "EightPuzzleMove{" +
                "previousMoveId='" + previousMoveId + '\'' +
                ", layout='" + layout + '\'' +
                '}';
    }
}
