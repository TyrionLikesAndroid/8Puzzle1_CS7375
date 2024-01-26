package src;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class EightPuzzleMoveRules{

    static Vector<LinkedList<String>> ruleSet;

    static void initialize()
    {
        ruleSet = new Vector<>();
        ruleSet.add(0, new LinkedList<>(Arrays.asList("1","3")));
        ruleSet.add(1, new LinkedList<>(Arrays.asList("0","2","4")));
        ruleSet.add(2, new LinkedList<>(Arrays.asList("1","5")));
        ruleSet.add(3, new LinkedList<>(Arrays.asList("0","4","6")));
        ruleSet.add(4, new LinkedList<>(Arrays.asList("1","3","5","7")));
        ruleSet.add(5, new LinkedList<>(Arrays.asList("2","4","8")));
        ruleSet.add(6, new LinkedList<>(Arrays.asList("3","7")));
        ruleSet.add(7, new LinkedList<>(Arrays.asList("6","4","8")));
        ruleSet.add(8, new LinkedList<>(Arrays.asList("7","5")));
    }

    static LinkedList<String> getAvailableMoves(int emptyPosition)
    {
        return ruleSet.get(emptyPosition);
    }

}