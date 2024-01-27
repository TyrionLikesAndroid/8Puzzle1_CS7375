package src;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EightPuzzleTestMain {

    // This class is the main test harness for the assignment.  It initializes the test classes,
    // generates the random starting point, runs the test, and prints out results

    public static void main(String[] args)
    {
        String testStart = new String();
        String randomStart = new String();

        // Save some test strings with known outcomes
        // testStart = "235104876";  // 10th generation solve
        // testStart = "235810764";  // 15th generation solve
        // testStart = "123845706";  // 3rd generation solve
        // testStart = "123845760";  // 2nd generation solve
        // testStart = "467351280";  // 26th generation solve
        // testStart = "206358471";  // 27th generation solve
        // testStart = "758631204";  // 23rd generation solve
        // testStart = "048563712";  // 28th generation solve
        // testStart = "123874605";  // not solvable

        int successCount = 0;
        int failCount = 0;

        // Initialize our static rule set
        EightPuzzleMoveRules.initialize();

        // Set up a testing loop for multiple iterations.  By default, just run it one time
        for(int j = 0; j < 1; j++)
        {
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
            System.out.println("Starting Point = " + start);

            // Create an instance of the puzzle solver and solve with breadth first search (BFS).  Count
            // the successes and failures in the event we are doing a batch test
            EightPuzzleSolver solver = new EightPuzzleSolver(start);
            if(solver.solvePuzzleBFS())
                successCount++;
            else
                failCount++;

            // Print the solution stack to the terminal
            solver.printSolution();

            // Print the incremental results from the test series
            System.out.println("Success = " + successCount + " Failure = " + failCount);
        }
    }
}
