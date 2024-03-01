package src;

public class EightPuzzleAStarOption {

    public EightPuzzleMove previousLayout;
    public int heuristicValue;
    public String moveOption;

    public EightPuzzleAStarOption(EightPuzzleMove previousLayout, int heuristicValue, String moveOption) {
        this.previousLayout = previousLayout;
        this.heuristicValue = heuristicValue;
        this.moveOption = moveOption;
    }

    @Override
    public String toString() {
        return "EightPuzzleAStarOption{" +
                "previousLayout=" + previousLayout +
                ", heuristicValue=" + heuristicValue +
                ", moveOption='" + moveOption + '\'' +
                '}';
    }
}
