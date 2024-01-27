package src;

import java.util.*;

public class EightPuzzleSolver {

    private Vector<HashMap<String, EightPuzzleMove>> generationLog;
    public TreeSet<String> layoutHistory;
    private final String solutionLayout = "123804765";
    static private final String START = "START";
    static private final String EMPTY = "0";
    static private final int INITIAL_EMPTY_POS = 99;
    private int generationCounter = 0;
    private int moveCounter = 0;
    private EightPuzzleMove finalMove = null;

    public EightPuzzleSolver(String startingLayout)
    {
        generationLog = new Vector<>(50);
        layoutHistory = new TreeSet<>();

        HashMap<String, EightPuzzleMove> firstGeneration = new HashMap<>();
        firstGeneration.put(START, new EightPuzzleMove(START, INITIAL_EMPTY_POS, false, startingLayout));
        generationLog.add(generationCounter,firstGeneration);
    }

    public void solvePuzzleDFS()
    {

    }

    public void solvePuzzleBFS()
    {
        while(! iterateNextBFSGeneration())
        {
            System.out.println("Running Generation - " + generationCounter);
            try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public boolean iterateNextBFSGeneration()
    {
        // Get the moves from the current generation
        HashMap<String, EightPuzzleMove> currentGeneration;

        try {currentGeneration = generationLog.get(generationCounter); }
        catch (Exception e)
        {
            // End gracefully if the starting point is not solvable for this solution layout
            EightPuzzleMove startingPoint = generationLog.get(0).get(START);
            System.out.println("Puzzle is not solvable for start[" + startingPoint.layout +
                    "] and solution[" + solutionLayout + "]");
            return true;
        }

        Iterator<String> moveIds = currentGeneration.keySet().iterator();

        // move counter for next generation
        generationCounter++;

        while(moveIds.hasNext())
        {
            String anId = moveIds.next();
            EightPuzzleMove aMove = currentGeneration.get(anId);

            // Find where the empty position is in the layout
            int emptyPos = aMove.layout.indexOf(EMPTY);
            //System.out.println("current layout = " + aMove.layout + " emptyPos=" + emptyPos);

            // Determine what the next moves could be for this layout
            Iterator<String> moves = EightPuzzleMoveRules.getAvailableMoves(emptyPos).iterator();
            while (moves.hasNext())
            {
                Integer nextMove = Integer.parseInt(moves.next());

                // Skip past the move that would take us back to our previous empty position
                if(nextMove.equals(aMove.previousEmptyPos))
                    continue;

                // Skip past any moves that have already been flagged as pruned leaves of the tree
                if(aMove.prunedLeaf)
                    continue;

                // This is an available move, so swap the empty space and the tile at this position
                String tileToMove = aMove.layout.substring(nextMove, nextMove+1);
                char[] myArray = aMove.layout.toCharArray();
                myArray[emptyPos] = tileToMove.charAt(0);
                myArray[nextMove] = EMPTY.charAt(0);

                // Save this move to the generational map.  It will return the new move instance
                EightPuzzleMove newMove = addPuzzleMove(generationCounter, anId, emptyPos, String.valueOf(myArray));

                // See if the puzzle is solved
                if(solutionLayout.equals(newMove.layout))
                {
                    System.out.println("Puzzle is solved");
                    finalMove = newMove;
                    return true;
                }
            }
        }
        return false;
    }

    EightPuzzleMove addPuzzleMove(int generation, String previousMoveId, int previousEmptyPos, String newLayout)
    {
        // Determine if this new move is a leaf of the tree that needs to be pruned.  Since every leaf turns
        // into a root for subsequent generations, we don't need to add a redundant leaf if it has already been
        // added to the tree
        boolean pruneLeaf = layoutHistory.contains(newLayout);

        EightPuzzleMove newMove = new EightPuzzleMove(previousMoveId, previousEmptyPos, pruneLeaf, newLayout);

        // See if this generation has started yet, if not create it
        if(generationLog.size() <= generation)
            generationLog.add(new HashMap<>());

        // Get the map for this generation
        HashMap<String, EightPuzzleMove> genMap = generationLog.get(generation);
        String genId = generation + "-" + moveCounter++;
        genMap.put(genId, newMove);

        // Add this layout to the layout history
        layoutHistory.add(newLayout);

        System.out.println("Gen id["+ genId + "] move: " + newMove);

        return newMove;
    }

    public void printSolution()
    {
        if(finalMove != null)
        {
            EightPuzzleMove solutionMove = finalMove;
            System.out.println("Final Move: " + finalMove);

            while(! solutionMove.previousMoveId.equals(START))
            {
                String previousGenId = solutionMove.previousMoveId;
                int delimeter = previousGenId.indexOf("-");
                int previousGeneration = Integer.parseInt(previousGenId.substring(0,delimeter));
                solutionMove = generationLog.get(previousGeneration).get(solutionMove.previousMoveId);
                System.out.println("   Solution Move: " + solutionMove);
            }

            solutionMove = generationLog.get(0).get(START);
            System.out.println("Starting Point: " + solutionMove);
        }
    }
}
