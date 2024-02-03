package src;

import java.util.LinkedList;

public class EightPuzzleMove {

    // This class is used to record information about each move that is taken.  It is stored in the
    // Solver class and is used to track the complete history of the graph searches.

    public String previousMoveId;   // The id of the move that precedes this one, used for reconstruction
    public String moveId;           // The id of this move
    public String layout;           // The layout of a particular tile set, stored as a 9-character string
    public int previousEmptyPos;    // The previous position of the blank tile before this move
    boolean prunedLeaf;             // Boolean indicator noting whether this leaf has been pruned
    boolean movesAreSet;            // Boolean indicator noting whether available moves have been set
    LinkedList<String> availableMoves;  // Linked list of available moves left for this move

    public EightPuzzleMove(String moveId, String previousMoveId, int previousEmptyPos, boolean prunedLeaf, String layout)
    {
        this.moveId = moveId;
        this.previousMoveId = previousMoveId;
        this.previousEmptyPos = previousEmptyPos;
        this.prunedLeaf = prunedLeaf;
        this.layout = layout;

        movesAreSet = false;
        availableMoves = new LinkedList<>();
    }

    public void addAvailableMoves(LinkedList<String> moves)
    {
        availableMoves.addAll(moves);
    }

    @Override
    public String toString() {
        return "EightPuzzleMove{" +
                "moveId='" + moveId + '\'' +
                ", layout='" + layout + '\'' +
                '}';
    }

    public void printAsciiImage()
    {
        char[] array = layout.toCharArray();
        System.out.println("_____________");
        System.out.print("| " + array[0] + " | " + array[1] + " | " + array[2] + " | \n");
        System.out.print("| " + array[3] + " | " + array[4] + " | " + array[5] + " | \n");
        System.out.print("| " + array[6] + " | " + array[7] + " | " + array[8] + " | \n");
        System.out.println("_____________");
    }
}
