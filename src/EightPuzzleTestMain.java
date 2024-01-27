package src;

public class EightPuzzleTestMain {

    public static void main(String[] args)
    {
        // Start with a test string.  Eventually we will randomize this
        //String test = "235104876";  // 10th generation solve
        //String test = "235810764";  // 15th generation solve
        //String test = "123845706";  // 3rd generation solve
        //String test = "123845760";  // 2nd generation solve
        //String test = "123456780";  // not solvable
        //String test = "163452870";  // not solvable
        //String test = "127804365";  // not solvable
        //String test = "523804761";  // not solvable
        String test = "123874605";  // not solvable

        // Initialize our rule set
        EightPuzzleMoveRules.initialize();

        // Create out solver class
        EightPuzzleSolver solver = new EightPuzzleSolver(test);
        solver.solvePuzzleBFS();
        solver.printSolution();
    }
}
