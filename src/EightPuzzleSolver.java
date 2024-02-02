package src;

import java.util.*;

public class EightPuzzleSolver {

    private Vector<HashMap<String, EightPuzzleMove>> generationLog;
    public TreeSet<String> layoutHistory;
    static private final String solutionLayout = "123804765";
    static private final String START = "0-1";
    static private final String EMPTY = "0";
    static private final int INITIAL_EMPTY_POS = 99;
    static private final int MAX_DEPTH_DFS_SEARCH = 33;
    private String startingLayout;
    private int generationCounter = 0;
    private int moveCounter = 0;
    private EightPuzzleMove finalMove = null;

    public EightPuzzleSolver(String startingLayout)
    {
        this.generationLog = new Vector<>(50);
        this.layoutHistory = new TreeSet<>();
        this.startingLayout = startingLayout;

        HashMap<String, EightPuzzleMove> firstGeneration = new HashMap<>();
        firstGeneration.put(START, new EightPuzzleMove(START, START, INITIAL_EMPTY_POS, false, startingLayout));
        generationLog.add(generationCounter,firstGeneration);
    }

    public void reset()
    {
        // Reset back to initial state
        generationCounter = 0;
        moveCounter = 0;
        finalMove = null;

        layoutHistory.clear();

        Iterator<HashMap<String, EightPuzzleMove>> generations = generationLog.iterator();
        while(generations.hasNext()) { generations.next().clear(); }

        // Reset the starting point
        generationLog.get(generationCounter).put(START,
                new EightPuzzleMove(START, START, INITIAL_EMPTY_POS, false, startingLayout));
    }

    public void solvePuzzleDFS()
    {
        // Set focal move to the first generation
        EightPuzzleMove focalMove = generationLog.get(0).get(START);

        // Set the available moves for our first focal move
        int emptyPos = focalMove.layout.indexOf(EMPTY);
        focalMove.addAvailableMoves(EightPuzzleMoveRules.getAvailableMoves(emptyPos));

        // while there is a focal move
        while(focalMove != null)
        {
            String focalMoveId = focalMove.moveId;

            // Evaluate for solution
            if(solutionLayout.equals(focalMove.layout))
            {
                System.out.println("Puzzle is solved with DFS");
                finalMove = focalMove;
                return;
            }

            // Check for depth max
            if(parseGenerationId(focalMove.moveId) >= MAX_DEPTH_DFS_SEARCH)
            {
                // Clear the available moves, we can't use them anyway and this will help
                // start the loop with correct iteration state
                focalMove.availableMoves.clear();

                // Recurse through previous moves until we find some available moves
                while(focalMove.availableMoves.isEmpty())
                {
                    int previousGen = parseGenerationId(focalMove.previousMoveId);
                    focalMove = generationLog.get(previousGen).get(focalMove.previousMoveId);
                    emptyPos = focalMove.layout.indexOf(EMPTY);

                    // Decrement the generation counter because we moved backward
                    generationCounter--;
                }

                // Continue with the loop once you have found a node with available moves
                continue;
            }

            // Take the next available move
            Integer nextMove = Integer.parseInt(focalMove.availableMoves.removeFirst());

            // Increment the counter because we are moving down the solution stack
            generationCounter++;

            // This is an available move, so swap the empty space and the tile at this position
            String tileToMove = focalMove.layout.substring(nextMove, nextMove+1);
            char[] myArray = focalMove.layout.toCharArray();
            myArray[emptyPos] = tileToMove.charAt(0);
            myArray[nextMove] = EMPTY.charAt(0);

            // Save this move to the generational map.  It will return a new focal move for next iteration
            focalMove = addPuzzleMove(generationCounter, focalMoveId, emptyPos, String.valueOf(myArray));
            emptyPos = focalMove.layout.indexOf(EMPTY);

            // If the new focal node has never set available moves, set them here
            if(! focalMove.movesAreSet)
                focalMove.addAvailableMoves(EightPuzzleMoveRules.getAvailableMoves(emptyPos));

            // Remove the move that would take us back to where we came from
            focalMove.availableMoves.remove(String.valueOf(focalMove.previousEmptyPos));
        }
    }

    public boolean solvePuzzleBFS()
    {
        while(! iterateNextBFSGeneration())
        {
            //System.out.println("Running Generation - " + generationCounter);
            try { Thread.sleep(10); } catch (Exception e) { e.printStackTrace(); }
        }

        // Return whether we solved it successfully
        return (finalMove != null);
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
                    System.out.println("Puzzle is solved with BFS");
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

        // See if this generation has started yet, if not create it
        if(generationLog.size() <= generation)
            generationLog.add(new HashMap<>());

        // Get the map for this generation
        HashMap<String, EightPuzzleMove> genMap = generationLog.get(generation);
        String genId = generation + "-" + moveCounter++;

        EightPuzzleMove newMove = new EightPuzzleMove(genId, previousMoveId, previousEmptyPos, pruneLeaf, newLayout);
        genMap.put(genId, newMove);

        // Add this layout to the layout history
        layoutHistory.add(newLayout);

        //System.out.println("Gen id["+ genId + "] move: " + newMove);

        return newMove;
    }

    private int parseGenerationId(String genId)
    {
        int delimeter = genId.indexOf("-");
        return Integer.parseInt(genId.substring(0,delimeter));
    }

    public void printSolution()
    {
        if(finalMove != null)
        {
            EightPuzzleMove solutionMove = finalMove;
            System.out.println("Final Move: " + finalMove);

            while(! solutionMove.previousMoveId.equals(START))
            {
                int previousGeneration = parseGenerationId(solutionMove.previousMoveId);
                solutionMove = generationLog.get(previousGeneration).get(solutionMove.previousMoveId);
                System.out.println("   Solution Move: " + solutionMove);
            }

            solutionMove = generationLog.get(0).get(START);
            System.out.println("Starting Point: " + solutionMove);
        }
    }
}
