package solver;
import java.util.ArrayList;
public class Node {
    public Node(GameState state, Moveset moveset, Node previous) {
		int pathLength;
        this.state = state;
		this.moveset = moveset;
        this.previous = previous;
		pathLength = getPathLength();
        // key component for guiding A* search algorithm
		this.fScore = (pathLength != -1 ? pathLength : 0) + state.getManhattan();
    }
    public GameState getState() {
        return state;
    }
	public String getMSSequence() {
		if (moveset != null)
			return moveset.getMoveSequence();
		return "";
	}
	public int getPathLength() {
		if (null == moveset)
			return -1;
		if (null == previous)
			return getMSSequence().length();
		return previous.getPathLength() + getMSSequence().length();
	}
    // getter for parent node or the previous state encapsulated in the node (yes di ko na alam )
    public Node getParent() {
        return previous;
    }
    public int getFScore() {
        return fScore;
    }
    private final GameState state;
	private final Moveset moveset;
    private final Node previous;
    private final int fScore;
    // computation of backtracking of previous players moves can be done using parent node -> parentNodeState -> parentNodeStatePlayer Position
    // this will be useful when reconstructing the solution path once the goal state is reached
}
