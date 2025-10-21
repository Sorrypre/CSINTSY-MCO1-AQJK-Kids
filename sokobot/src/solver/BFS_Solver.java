package solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BFS_Solver {
    private int mapWidth;
    private int mapHeight;
    private Character[][] mapData;
    HashMap<Position, Character> itemsData;

    int moveCount = 0;
    private boolean reachedGoal = false;
    int nodesLeftInLayer = 1;
    int nodesInNextLayer = 0;

    private Queue<BFS_Node> frontier = new LinkedList<>();
    private boolean[][] explored;

    private BFS_Node currentBFSNode;
    private BFS_Node parentBFSNode = null;

    public BFS_Solver(Character[][] mapData, HashMap<Position, Character> itemsData) {
        this.mapWidth = mapData[0].length;
        this.mapHeight = mapData.length;
        this.explored = new boolean[mapHeight][mapWidth];
        this.mapData = mapData;
        this.itemsData = itemsData;
    }
    // BFS algorithm to go from the player's position to the adjacent position of a box
    // This method should return the path as a string of directions (U, D, L, R)
    public String solve(Position playerPos, Position adjBoxPos) {
        parentBFSNode = new BFS_Node(playerPos.getRow(), playerPos.getCol());
        frontier.offer(parentBFSNode);
        explored[playerPos.getRow()][playerPos.getCol()] = true;

        while (!frontier.isEmpty()) {
            currentBFSNode = frontier.poll();
            if (currentBFSNode.getPosition().equals(adjBoxPos)) {
                reachedGoal = true;
            }
            exploreNeighborNodes(currentBFSNode);
            nodesLeftInLayer--;
            if (nodesLeftInLayer == 0) {
                nodesLeftInLayer = nodesInNextLayer;
                nodesInNextLayer = 0;
                if (!currentBFSNode.equals(parentBFSNode))
                    moveCount++;
            }
            if (reachedGoal) {
                System.out.println("BFS Solved in " + moveCount + " moves.");
                return backTrackPath(currentBFSNode);
            }
        }
        return null; // No path found
    }
    public void exploreNeighborNodes(BFS_Node currentBFSNode) {
        // Up, Down, Left, Right

        int[] directionsRow = {-1, 1, 0, 0};
        int[] directionsCol = {0, 0, -1, 1};
        int newRow, newCol;
        for (int i = 0; i < 4; i++) {
            newRow = currentBFSNode.getPosition().getRow() + directionsRow[i];
            newCol = currentBFSNode.getPosition().getCol() + directionsCol[i];

            if (newRow >= 0 && newCol >= 0 && newRow < mapHeight && newCol < mapWidth
                    && !explored[newRow][newCol] && !mapData[newRow][newCol].equals('#') && !itemsData.getOrDefault(currentBFSNode.getPosition(), ' ').equals('$')) {
                frontier.offer(new BFS_Node(newRow, newCol, currentBFSNode));
                explored[newRow][newCol] = true;
                nodesInNextLayer++;
            }
        }
    }
    public String backTrackPath(BFS_Node BFSNode) {
        StringBuilder path = new StringBuilder();
        while (BFSNode.getParent() != null) {
            Character direction = convertPositionDifferenceToDirection(BFSNode.getParent().getPosition(), BFSNode.getPosition());
            if (direction != null) {
                path.insert(0, direction);
            }
            BFSNode = BFSNode.getParent();
        }
        return path.toString();
    }
    public Character convertPositionDifferenceToDirection(Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();

        if (rowDiff == -1 && colDiff == 0) {
            return 'u'; // Up
        } else if (rowDiff == 1 && colDiff == 0) {
            return 'd'; // Down
        } else if (rowDiff == 0 && colDiff == -1) {
            return 'l'; // Left
        } else if (rowDiff == 0 && colDiff == 1) {
            return 'r'; // Right
        }
        return null; // Invalid move
    }

}
