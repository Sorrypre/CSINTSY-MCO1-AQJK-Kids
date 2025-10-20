package solver;

public class Node {
    public Node(GameState state, Moveset moveset, Node previous, int hScore) {
		if (hScore < 0)
			throw new IllegalArgumentException("hScore cannot be negative");
		int prevPathLength = previous != null ? previous.getPathLength() : -1;
        this.state = state;
		this.moveset = moveset;
        this.previous = previous;
        // key component for guiding A* search algorithm
		this.fScore = (prevPathLength != -1 ? prevPathLength : 0) +
			(moveset != null ? getPathLength() + hScore : 0);
    }
    public GameState getState() {
        return state;
    }
	public Moveset getMoveset() {
		return moveset;
	}
	public int getPathLength() {
		return moveset != null ? moveset.getMoveSequence().length() : -1;
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
