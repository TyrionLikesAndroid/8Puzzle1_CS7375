package src;

public class EightPuzzleTestMain {

    public static void main(String[] args)
    {
        // Start with a test string.  Eventually we will randomize this
        String test = "123456780";

        // Initialize our rule set
        EightPuzzleMoveRules.initialize();

        // Create out solver class
        EightPuzzleSolver solver = new EightPuzzleSolver(test);
        solver.solvePuzzleBFS();
        solver.printSolution();
    }
}
