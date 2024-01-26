package src;

public class EightPuzzleTestMain {

    public static void main(String[] args)
    {
        // Start with a test string.  Eventually we will randomize this
        String test = "235104876";  // 10th generation solve
        //String test = "123456780";  // not solvable yet

        // Initialize our rule set
        EightPuzzleMoveRules.initialize();

        // Create out solver class
        EightPuzzleSolver solver = new EightPuzzleSolver(test);
        solver.solvePuzzleBFS();
        solver.printSolution();
    }
}
