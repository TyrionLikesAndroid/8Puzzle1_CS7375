package src;

public class EightPuzzleMove {

    public String previousMoveId;
    public String layout;
    public int previousEmptyPos;
    boolean prunedLeaf;

    public EightPuzzleMove(String previousMoveId, int previousEmptyPos, boolean prunedLeaf, String layout)
    {
        this.previousMoveId = previousMoveId;
        this.previousEmptyPos = previousEmptyPos;
        this.prunedLeaf = prunedLeaf;
        this.layout = layout;
    }

    @Override
    public String toString() {
        return "EightPuzzleMove{ " + layout + " }";
    }
}
