package solver;

import java.util.ArrayList;

public interface stateBasedModelFunctions {
    public boolean isEnd(GameState state);
    // The cost of each action is 1 so we return 1
    public int cost();
    public ArrayList<Character> actions(GameState state);
    public GameState Succ(GameState g, Moveset m);
}
