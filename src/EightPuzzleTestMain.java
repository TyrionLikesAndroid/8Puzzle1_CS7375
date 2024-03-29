package src;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EightPuzzleTestMain {

    public static int ITERATIONS = 1;  // Define how many iterations we want the test to run

    // This class is the main test harness for the assignment.  It initializes the test classes,
    // generates the random starting point, runs the test, and prints out results

    public static void main(String[] args)
    {
        String testStart = "";

        // Save some test strings with known outcomes
        // testStart = "235104876";  // 10th generation BFS solve
        // testStart = "235810764";  // 15th generation BFS solve
        // testStart = "123845706";  // 3rd generation BFS solve
        // testStart = "123405876";  // 6th generation BFS solve
        // testStart = "123845760";  // 2nd generation BFS solve
        // testStart = "467351280";  // 26th generation BFS solve
        // testStart = "206358471";  // 27th generation BFS solve
        // testStart = "758631204";  // 23rd generation BFS solve
        // testStart = "048563712";  // 28th generation BFS solve
        // testStart = "547068312";  // 29th generation BFS solve
        // testStart = "123874605";  // not solvable

        int successCount = 0;
        int failCount = 0;

        int bfsWins = 0;
        int dfsWins = 0;
        int aStarWins = 0;

        int bfsIterations = 0;
        int dfsIterations = 0;
        int aStarIterations = 0;

        int totalBfsIterations = 0;
        int totalDfsIterations = 0;
        int totalAStarIterations = 0;

        // Initialize our static rule set
        EightPuzzleMoveRules.initialize();

        // Set up a testing loop for multiple iterations.  By default, just run it one time
        for(int j = 0; j < ITERATIONS; j++)
        {
            String randomStart = "";

            if(testStart.isEmpty())
            {
                // Create a random starting point based on all the possible tiles and shuffle them
                List<String> puzzleTiles = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8");
                Collections.shuffle(puzzleTiles);
                for (int i = 0; i <= 8; i++) {
                    randomStart = randomStart.concat(puzzleTiles.get(i));
                }
            }

            // One of them will always be empty, just makes it easier to pivot between test and random
            String start = testStart.concat(randomStart);

            // Print starting point to the terminal
            System.out.println("Starting Point = " + start + "\n");

            // Create an instance of the puzzle solver and solve with breadth first search (BFS).  Count
            // the successes and failures in the event we are doing a batch test
            EightPuzzleSolver solver = new EightPuzzleSolver(start);
            if(solver.solvePuzzleBFS())
            {
                successCount++;

                // Print the BFS solution stack to the terminal
                solver.printSolution(false);
                bfsIterations = solver.totalIterationsToSolve();

                // Reset to initial state
                solver.reset();

                // If BFS can solve it, go ahead and do a DFS solution.  There will never be a case where
                // BFS will solve and DFS will not
                solver.solvePuzzleDFS();

                // Print the DFS solution stack to the terminal
                solver.printSolution(false);
                dfsIterations = solver.totalIterationsToSolve();

                // Reset to initial state once more
                solver.reset();

                // Solve for A* since we know a solution exists
                solver.solvePuzzleAStar();

                // Print the A* solution stack to the terminal
                solver.printSolution(false);
                aStarIterations = solver.totalIterationsToSolve();

                // Determine which algorithm was the most efficient for this particular layout
                if(bfsIterations < dfsIterations)
                    if(bfsIterations < aStarIterations)
                        bfsWins++;
                    else
                        aStarWins++;
                else
                    if(dfsIterations < aStarIterations)
                        dfsWins++;
                    else
                        aStarWins++;

                // Save the total number of iterations
                totalBfsIterations += bfsIterations;
                totalDfsIterations += dfsIterations;
                totalAStarIterations += aStarIterations;
            }
            else
                failCount++;

            // Print the incremental results from the test series
            System.out.println("Success = " + successCount + " Failure = " + failCount);
        }

        // Compare the algorithm results
        System.out.println("BFS Wins = " + bfsWins + " DFS Wins = " + dfsWins + " A* Wins = " + aStarWins);

        if((bfsWins + dfsWins + aStarWins) > 0)
            System.out.println("Average Iterations:  BFS = " + totalBfsIterations/successCount +
                " DFS = " + totalDfsIterations/successCount + " A* = " + totalAStarIterations/successCount);
    }
}
