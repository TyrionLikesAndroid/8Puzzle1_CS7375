package src;

public class EightPuzzleMove {

    public String previousMoveId;
    public String layout;
    public int previousEmptyPos;

    public EightPuzzleMove(String previousMoveId, int previousEmptyPos, String layout)
    {
        this.previousMoveId = previousMoveId;
        this.previousEmptyPos = previousEmptyPos;
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
