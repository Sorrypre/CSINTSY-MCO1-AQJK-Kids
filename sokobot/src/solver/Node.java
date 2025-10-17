package solver;

public class Node {
    public Node(GameState state, Node parent, int gScore, int hScore) {
        this.state = state;
        this.parent = parent;
        // key component for guiding A* search algorithm
        this.fScore = gScore + hScore;
    }
    public GameState getState() {
        return state;
    }
    // getter for parent node or the previous state encapsulated in the node (yes di ko na alam )
    public Node getParent() {
        return parent;
    }
    public int getFScore() {
        return fScore;
    }

    private final GameState state;
    private final Node parent;
    private final int fScore;
    // computation of backtracking of previous players moves can be done using parent node -> parentNodeState -> parentNodeStatePlayer Position
    // this will be useful when reconstructing the solution path once the goal state is reached
}
