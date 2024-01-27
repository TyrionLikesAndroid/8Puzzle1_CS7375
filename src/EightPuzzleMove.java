package src;

public class EightPuzzleMove {

    // This class is used to record information about each move that is taken.  It is stored in the
    // Solver class and is used to track the complete history of the graph searches.

    public String previousMoveId;   // The id of the move that precedes this one, used for reconstruction
    public String layout;           // The layout of a particular tile set, stored as a 9-character string
    public int previousEmptyPos;    // The previous position of the blank tile before this move
    boolean prunedLeaf;             // Boolean indicator noting whether this leaf has been pruned

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
