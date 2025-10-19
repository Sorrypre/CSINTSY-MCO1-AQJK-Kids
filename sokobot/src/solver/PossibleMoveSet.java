package solver;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class PossibleMoveSet {

    public PossibleMoveSet(GameState state, Character[][] mapData) {
        this.state = state;
        this.mapData = mapData;
    }
    public ArrayList<Position> reachablePositionsBFS() {
        Queue<Position> frontier = new LinkedList<>();
        HashSet<Position> explored = new HashSet<>();

        frontier.offer(state.getPlayerPos());
        return null; // Placeholder return
    }
    private final Character[][] mapData;
    private final GameState state;
}
