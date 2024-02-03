package src;

import java.util.*;

public class EightPuzzleSolver {

    // This class contains the algorithms for both BFS and DFS searches.  Both searches leverage the
    // generationLog below to store all the move information that can be used to reconstruct the entire
    // search tree or simply dump the solution move set.  Every move has a pointer to its previous move,
    // so reverse navigation is easy as well as top down navigation by walking the generations in order.

    private Vector<HashMap<String, EightPuzzleMove>> generationLog;  // Log of all move data, collected by generation
    public HashMap<String, Integer> layoutHistory;  // History of moves, used for tree pruning exclusively
    static private final String solutionLayout = "123804765";  // Solution layout for our puzzle
    static private final String START = "0-1";  // Initial move id for our starting point
    static private final String EMPTY = "0";  // The empty position in the puzzle is represented by zero
    static private final int INITIAL_EMPTY_POS = 99;  // The previous empty position of our starting move
    static private final int MAX_DEPTH_DFS_SEARCH = 33;  // Maximum depth allowed for our DFS search
    private String startingLayout;  // Stores the starting layout, passed in during construction
    private int generationCounter = 0;  // Counter for the current move generation
    private int moveCounter = 0;  // Counter for the current move taken
    private EightPuzzleMove finalMove = null;  // Stores the final solution move (if one is found)

    public EightPuzzleSolver(String startingLayout) {
        this.generationLog = new Vector<>(50);
        this.layoutHistory = new HashMap<>();
        this.startingLayout = startingLayout;

        HashMap<String, EightPuzzleMove> firstGeneration = new HashMap<>();

        // Initialize the first generation by putting our starting layout with default positional data
        firstGeneration.put(START, new EightPuzzleMove(START, START, INITIAL_EMPTY_POS, false, startingLayout));
        generationLog.add(generationCounter, firstGeneration);
    }

    public void reset() {
        // Reset the instance back to initial state.  This is done to leverage the same instance for back to
        // back BFS and DFS searches
        generationCounter = 0;
        moveCounter = 0;
        finalMove = null;

        layoutHistory.clear();

        Iterator<HashMap<String, EightPuzzleMove>> generations = generationLog.iterator();
        while (generations.hasNext()) {
            generations.next().clear();
        }

        // Reset the starting point
        generationLog.get(generationCounter).put(START,
                new EightPuzzleMove(START, START, INITIAL_EMPTY_POS, false, startingLayout));
    }

    public void solvePuzzleDFS() {
        // This is the entry point for the Depth First Search algorithm.  It will traverse all the way down the tree
        // then start recursing the tree and finding all available moves as it moves left to right

        // Set the initial focal move to the first generation node
        EightPuzzleMove focalMove = generationLog.get(0).get(START);

        // Initialize the location of the empty square in our focal move
        int emptyPos = focalMove.layout.indexOf(EMPTY);

        // Determine the available moves for our first focal move based on the position of the empty slot.  The
        // available moves are stored in the move itself, which allows us to remove a move from the available
        // list once it has been used.
        focalMove.addAvailableMoves(EightPuzzleMoveRules.getAvailableMoves(emptyPos));

        // Loop forever until we solve the puzzle.  Since we have already solved via BFS, we
        // know a solution exists
        while (true)
        {
            // Grab the unique id for this focal move.  The unique id is a key which concatenates the
            // generation of this move with the move id, separated by a hyphen
            String focalMoveId = focalMove.moveId;

            // Determine whether this layout matches the solution for our puzzle
            if (solutionLayout.equals(focalMove.layout))
            {
                System.out.println("\nPuzzle is solved with DFS in [" +  parseGenerationId(focalMoveId) + "] moves");
                finalMove = focalMove;
                return;
            }

            // Check all of our conditions for recursing back up the tree
            // 1) we've achieved maximum DFS search depth
            // 2) we've reached a layout that we've already seen at an earlier generation.  No reason to
            //    go farther in that case because we've already encountered it with more stack space
            if ((parseGenerationId(focalMove.moveId) >= MAX_DEPTH_DFS_SEARCH) ||
                    (layoutHistory.containsKey(focalMove.layout) &&
                    (layoutHistory.get(focalMove.layout) < parseGenerationId(focalMove.moveId))))
            {
                // Clear the available moves, we can't use them anyway and this will help
                // start the loop with correct iteration state
                focalMove.availableMoves.clear();

                // Recurse through previous moves until we find some available moves
                focalMove = recurseForNextAvailableMove(focalMove);

                // Reset the position of the blank
                emptyPos = focalMove.layout.indexOf(EMPTY);

                // Continue with the loop once you have found a node with available moves
                continue;
            }

            // Take the next available move
            Integer nextMove = Integer.parseInt(focalMove.availableMoves.removeFirst());

            // Increment the counter because we are moving down the solution stack
            generationCounter++;

            // Generate the new layout based on swapping the next move and empty position
            String newLayout = calculateNextLayout(focalMove, nextMove, emptyPos);

            // Save the new move on the generational map.  It will return a new focal move for next iteration
            focalMove = addPuzzleMove(generationCounter, focalMoveId, emptyPos, newLayout);
            emptyPos = focalMove.layout.indexOf(EMPTY);

            // If the new focal node has never set available moves, set them here
            if (!focalMove.movesAreSet)
                focalMove.addAvailableMoves(EightPuzzleMoveRules.getAvailableMoves(emptyPos));

            // Remove the move that would take us back to where we came from.  This is another simple
            // optimization to help performance
            focalMove.availableMoves.remove(String.valueOf(focalMove.previousEmptyPos));
        }
    }

    public boolean solvePuzzleBFS()
    {
        // This is the entry point for the Breadth First Search algorithm.  It simply calls the generational
        // function, which will return false until it finds a solution or we exhaust the tree completely

        while (! iterateNextBFSGeneration())
        {
            //System.out.println("Running Generation - " + generationCounter);

            // Put a short sleep between the generations in case we need to observe the traces
            try { Thread.sleep(10); } catch (Exception e) { e.printStackTrace(); }
        }

        // Return whether we solved it successfully
        return (finalMove != null);
    }

    public boolean iterateNextBFSGeneration()
    {
        // Retrieve the moves for the current generation out of the log
        HashMap<String, EightPuzzleMove> currentGeneration;
        try { currentGeneration = generationLog.get(generationCounter); }
        catch (Exception e)
        {
            // End gracefully if this starting point is not solvable for this solution layout.  Testing
            // has shown that this will happen 50% of the time, which is confirmed by internet research
            EightPuzzleMove startingPoint = generationLog.get(0).get(START);
            System.out.println("Puzzle is not solvable for start[" + startingPoint.layout +
                    "] and solution[" + solutionLayout + "]");
            return true;
        }

        // Create an iterator so we can walk all the available moves for this generation
        Iterator<String> moveIds = currentGeneration.keySet().iterator();

        // Move counter for next generation since we are going down the tree
        generationCounter++;

        // Iterate through the moves available for this generation
        while (moveIds.hasNext())
        {
            String anId = moveIds.next();
            EightPuzzleMove aMove = currentGeneration.get(anId);

            // Find where the empty position is in the layout
            int emptyPos = aMove.layout.indexOf(EMPTY);

            // Determine what the next moves should be for this layout
            Iterator<String> moves = EightPuzzleMoveRules.getAvailableMoves(emptyPos).iterator();
            while (moves.hasNext())
            {
                Integer nextMove = Integer.parseInt(moves.next());

                // Skip past the move that would take us back to our previous empty position
                if (nextMove.equals(aMove.previousEmptyPos))
                    continue;

                // Skip past any moves that have already been flagged as pruned leaves of the tree.  The
                // criteria for setting this flag are below, but we basically stop searching any node with
                // a layout that we have already encountered in a previous generation.
                if (aMove.prunedLeaf)
                    continue;

                // This is an available move, so swap the empty space and the tile at this position
                String newLayout = calculateNextLayout(aMove, nextMove, emptyPos);

                // Save this move to the generational map.  It will return the new move instance
                EightPuzzleMove newMove = addPuzzleMove(generationCounter, anId, emptyPos, newLayout);

                // See if the puzzle is solved by comparing against our solution
                if (solutionLayout.equals(newMove.layout))
                {
                    System.out.println("\nPuzzle is solved with BFS in [" +  parseGenerationId(newMove.moveId) + "] moves");
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
        // into a root for subsequent generations, we don't need to explore leaves that have already been seen
        // NOTE - this pruneLeaf variable is only used by the BFS algorithm
        boolean pruneLeaf = layoutHistory.containsKey(newLayout);

        // See if this generation has started yet, if not create it in the generational log
        if (generationLog.size() <= generation)
            generationLog.add(new HashMap<>());

        // Get the map for this generation
        HashMap<String, EightPuzzleMove> genMap = generationLog.get(generation);

        // Generate an id for this move based on its generation and the move counter
        String genId = generation + "-" + moveCounter++;

        // Construct the next puzzle move instance and put it into the map/log
        EightPuzzleMove newMove = new EightPuzzleMove(genId, previousMoveId, previousEmptyPos, pruneLeaf, newLayout);
        genMap.put(genId, newMove);

        // Add any newly encountered layouts to the history and update history any time we see the
        // same layout in an earlier generation (NOTE - only used for DFS algorithm)
        if ((!layoutHistory.containsKey(newLayout)) || (layoutHistory.get(newLayout) > generation))
            layoutHistory.put(newLayout, generation);

        //System.out.println("Gen id["+ genId + "] move: " + newMove);

        return newMove;
    }

    private int parseGenerationId(String genId)
    {
        // Helper function to parse the generation value out of an id
        int delimeter = genId.indexOf("-");
        return Integer.parseInt(genId.substring(0, delimeter));
    }

    private EightPuzzleMove recurseForNextAvailableMove(EightPuzzleMove initialMove)
    {
        // Helper function for the DFS algorithm used to recurse back up the stack in search
        // of the next available move

        EightPuzzleMove out = initialMove;

        // Look to see if this node has any available moves
        while(out.availableMoves.isEmpty())
        {
            // Grab the previous move based on the previousMoveId
            int previousGen = parseGenerationId(out.previousMoveId);
            out = generationLog.get(previousGen).get(out.previousMoveId);

            // Decrement the generation counter because we moved backward
            generationCounter--;
        }

        return out;
    }

    public String calculateNextLayout(EightPuzzleMove move, int movingTilePosition, int blankPosition)
    {
        // Helper function for generating a string representation for the next move on the puzzle

        String tileToMove = move.layout.substring(movingTilePosition, movingTilePosition + 1);
        char[] myArray = move.layout.toCharArray();
        myArray[blankPosition] = tileToMove.charAt(0);
        myArray[movingTilePosition] = EMPTY.charAt(0);

        return String.valueOf(myArray);
    }

    public void printSolution(boolean useAsciiArt)
    {
        // Helper function to print the entire solution stack when a solution exists
        if(finalMove != null)
        {
            EightPuzzleMove solutionMove = finalMove;

            if(! useAsciiArt)
                System.out.println("Final Move: " + finalMove);
            else
                finalMove.printAsciiImage();

            while(! solutionMove.previousMoveId.equals(START))
            {
                int previousGeneration = parseGenerationId(solutionMove.previousMoveId);
                solutionMove = generationLog.get(previousGeneration).get(solutionMove.previousMoveId);

                if(! useAsciiArt)
                    System.out.println("   Solution Move: " + solutionMove);
                else
                    solutionMove.printAsciiImage();
            }

            solutionMove = generationLog.get(0).get(START);

            if(! useAsciiArt)
                System.out.println("Starting Point: " + solutionMove + "\n");
            else
                solutionMove.printAsciiImage();
        }
    }
}