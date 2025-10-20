package solver;

public class BFS_Node {
    private final Position position;
    private BFS_Node parent = null;

    // initialize Node with row and column
    public BFS_Node(int row, int col) {
        this.position = new Position(row, col);
    }
    // initialize Node with row, column and parent Node
    public BFS_Node(int row, int col, BFS_Node parent) {
        this.position = new Position(row, col);
        this.parent = parent;
    }
    public BFS_Node getParent() {
        return parent;
    }
    public Position getPosition() {
        return position;
    }
    @Override
    public boolean equals (Object o) {
        if (this == o) return true; //same memory reference
        if (o == null || getClass() != o.getClass()) return false;
        BFS_Node BFSNode = (BFS_Node) o;
        return position.equals(BFSNode.position); //same content
    }
}
