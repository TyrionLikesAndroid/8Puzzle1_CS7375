package src;

import java.util.*;

public class EightPuzzleSolver {

    private Vector<HashMap<String, EightPuzzleMove>> generationLog;
    private final String solutionLayout = "123804765";
    static private final String START = "START";
    static private final String EMPTY = "0";
    private int generationCounter = 0;
    private int moveCounter = 0;

    public EightPuzzleSolver(String startingLayout)
    {
        generationLog = new Vector<>(50);

        HashMap<String, EightPuzzleMove> firstGeneration = new HashMap<>();
        firstGeneration.put(START, new EightPuzzleMove(START, startingLayout));
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
        HashMap<String, EightPuzzleMove> currentGeneration = generationLog.get(generationCounter);
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

            // Determine what the next moves would be for this layout
            Iterator<String> moves = EightPuzzleMoveRules.getAvailableMoves(emptyPos).iterator();
            while (moves.hasNext())
            {
                Integer nextMove = Integer.parseInt(moves.next());

                // Skip past the move that we made previously
               // if(nextMove.equals(aMove.previousMoveId))
                  //  continue;

                // This is an available move, so swap the empty space and the tile at this position
                String tileToMove = aMove.layout.substring(nextMove, nextMove+1);
                char[] myArray = aMove.layout.toCharArray();
                myArray[emptyPos] = tileToMove.charAt(0);
                myArray[nextMove] = EMPTY.charAt(0);

                //StringBuilder sb = new StringBuilder(aMove.layout);
                //sb.setCharAt(emptyPos, tileToMove.charAt(0));
                //sb.setCharAt(nextMove, EMPTY.charAt(0));

                // Save this move to the generational map.  It will return true if we have solved the puzzle
                if(addPuzzleMove(generationCounter, anId, String.valueOf(myArray)))
                {
                    System.out.println("Puzzle is solved");
                    return true;
                }
            }
        }
        return false;
    }

    boolean addPuzzleMove(int generation, String previousMoveId, String newLayout)
    {
        EightPuzzleMove newMove = new EightPuzzleMove(previousMoveId, newLayout);

        // See if this generation has started yet, if not create it
        if(generationLog.size() <= generation)
            generationLog.add(new HashMap<>());

        // Get the map for this generation
        HashMap<String, EightPuzzleMove> genMap = generationLog.get(generation);
        String genId = generation + "-" + moveCounter++;
        genMap.put(genId, newMove);

        System.out.println("Gen id["+ genId + "] move: " + newMove);

        return (solutionLayout.equals(newMove.layout));
    }

    public void printSolution()
    {
        System.out.println(solutionLayout);
    }
}
