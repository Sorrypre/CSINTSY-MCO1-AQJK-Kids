package solver;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/*
 * Represents the state of the game, including positions of items.
 * Uses a map to associate positions with characters representing items.
 * Provides methods to manipulate and query the game state.
 */
public class GameState {
    private final Map<Position, Character> itemsMap = new HashMap<>();
	private HashSet<Position> goalTiles;
	private Position playerPos = null;
	private int sequenceHash;
	private String stringForm;

	private GameState() { }
	
	private void updateStringForm() {
		stringForm = toString();
	}

	/* Constructor to convert given char[][] to a state */
	public GameState(int sequenceHash, Character[][] items, HashSet<Position> goalTiles) {
		this.goalTiles = goalTiles;
		this.sequenceHash = sequenceHash;
		/*
		for (int i = 0; i < items.length; i++)
			for(int j = 0; j < items[i].length; j++)
				// Add to itemsMap when the object is a box or a player
				if (items[i][j].equals('$') || items[i][j].equals('@'))
					setItem(i, j, items[i][j]);
		*/
		
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < items[i].length; j++) {
				if (items[i][j].equals('$') || items[i][j].equals('@')) {
					setItem(i, j, items[i][j]);
					if (items[i][j].equals('@'))
						this.playerPos = new Position(i, j);
				}
			}
		}
		
		if (getPlayerPos() == null)
			throw new NullPointerException("No player found on the given item data");
		// Always update string form when altering the contents of the state
		updateStringForm();
	}

	public GameState getCopy(Character[][] map) {
		Character[][] itemData = new Character[map.length][];
		for (int i = 0; i < map.length; i++) {
			itemData[i] = new Character[map[i].length];
			for (int j = 0; j < map[i].length; j++)
				itemData[i][j] = ' ';
		}
		for (Map.Entry<Position, Character> entry : itemsMap.entrySet())
			itemData[entry.getKey().getRow()][entry.getKey().getCol()] = entry.getValue();
		return new GameState(sequenceHash, itemData, goalTiles);
	}

	public HashMap<Position, Character> getItemsPos() {
		return (HashMap<Position, Character>) itemsMap;
	}
	
    public Character getItem(int row, int col) {
        Position pos = new Position (row, col);
        // If the position is not in the map, return a space character
        return itemsMap.getOrDefault(pos, ' ');
    }

    public Integer getManhattan() {
        if (goalTiles == null || goalTiles.isEmpty())
            return 0;

        int manh = 0;
        for (Map.Entry<Position, Character> entry : itemsMap.entrySet()) {
            if (!entry.getValue().equals('$'))
                continue;
            int boxRow = entry.getKey().getRow();
            int boxCol = entry.getKey().getCol();

            int minDist = Integer.MAX_VALUE;
            for (Position goal : goalTiles) {
                int dist = Math.abs(boxRow - goal.getRow()) + Math.abs(boxCol - goal.getCol());
                if (dist < minDist) minDist = dist;
                if (minDist == 0) break; // can't get better than this
            }
            if (minDist == Integer.MAX_VALUE) minDist = 0;
            manh += minDist;
        }
        return manh;
    }

	
    public void setItem(int row, int col, Character item) {
        Position pos = new Position (row, col);
		if (item.equals('@'))
			playerPos = pos;
        if (item.equals(' ')) {
            itemsMap.remove(pos);// Remove the entry if the item is a space
        }
        else itemsMap.put(pos, item);
		// Always update string form when altering the contents of the state
		updateStringForm();
    }
	
	public void removeItem(int row, int col) {
		itemsMap.remove(new Position(row, col));
		// Always update string form when altering the contents of the state
		updateStringForm();
	}
	
	// Matches Wall-Wall, Wall-Box, Box-Box deadlock for each box
	public boolean isAnyBoxCornered(Character[][] map) {
        // Iterate through itemsMap
        Character[] vicinity;
        for (Map.Entry<Position, Character> entry : itemsMap.entrySet()) {
            // only boxes
            if (!entry.getValue().equals('$')) continue;
            int row = entry.getKey().getRow();
            int col = entry.getKey().getCol();
            // ignore boxes on goals
            if (map[row][col].equals('.')) continue;

            int rows = map.length;
            int cols = map[0].length;
            vicinity = new Character[8];
            vicinity[0] = (row > 0) ? map[row - 1][col] : null;
            vicinity[1] = (row > 0) ? getItem(row - 1, col) : null;
            vicinity[2] = (row < rows - 1) ? map[row + 1][col] : null;
            vicinity[3] = (row < rows - 1) ? getItem(row + 1, col) : null;
            vicinity[4] = (col > 0) ? map[row][col - 1] : null;
            vicinity[5] = (col > 0) ? getItem(row, col - 1) : null;
            vicinity[6] = (col < cols - 1) ? map[row][col + 1] : null;
            vicinity[7] = (col < cols - 1) ? getItem(row, col + 1) : null;

            if ((isWallOrBox(vicinity[0]) && isWallOrBox(vicinity[2])) &&
                    (isWallOrBox(vicinity[4]) && isWallOrBox(vicinity[6]))) {
                return true;
            }
        }
        return false;
    }
    private boolean isWallOrBox(Character c) {
        return c != null && (c.equals('#') || c.equals('$'));
    }
	public boolean isSolution(Character[][] map, Integer expectedBoxes) {
		int counter = 0;
		if (map != null)
			for (Map.Entry<Position, Character> entry : itemsMap.entrySet())
				if (entry.getValue().equals('$') && map[entry.getKey().getRow()][entry.getKey().getCol()].equals('.'))
					counter++;
		return map != null && expectedBoxes != null && expectedBoxes == counter;
	}
	
	public Position getPlayerPos() {
		/*
		for (Map.Entry<Position, Character> entry : itemsMap.entrySet())
			if (entry.getValue().equals('@'))
				return entry.getKey();
		
		return null; // pag ichcheck ng constructor this is convenient
		*/
		return playerPos;
	}
	
	public Object[] getPlayerVicinityData() {
		// 4 inner directions * 2 outer directions * 2 data types
		Object[] vicinityData = new Object[4 * 2 * 2];
		// Current player position
		Position pos = getPlayerPos();
		
		// Get objects surrounding the player as follows:
		//         [2]
		//         [0]
		//  [10][8] @ [12][14]
		//         [4]
		//         [6]
		// * Odd numbers will store the item symbols (Character) corresponding to
		//   the previous even number.

		// Up
		vicinityData[0] = new Position(pos.getRow() - 1, pos.getCol());
		vicinityData[1] = getItem(pos.getRow() - 1, pos.getCol());
		vicinityData[2] = new Position(pos.getRow() - 2, pos.getCol());
		vicinityData[3] = getItem(pos.getRow() - 2, pos.getCol());
		// Down
		vicinityData[4] = new Position(pos.getRow() + 1, pos.getCol());
		vicinityData[5] = getItem(pos.getRow() + 1, pos.getCol());
		vicinityData[6] = new Position(pos.getRow() + 2, pos.getCol());
		vicinityData[7] = getItem(pos.getRow() + 2, pos.getCol());
		// Left
		vicinityData[8] = new Position(pos.getRow(), pos.getCol() - 1);
		vicinityData[9] = getItem(pos.getRow(), pos.getCol() - 1);
		vicinityData[10] = new Position(pos.getRow(), pos.getCol() - 2);
		vicinityData[11] = getItem(pos.getRow(), pos.getCol() - 2);
		// Right
		vicinityData[12] = new Position(pos.getRow(), pos.getCol() + 1);
		vicinityData[13] = getItem(pos.getRow(), pos.getCol() + 1);
		vicinityData[14] = new Position(pos.getRow(), pos.getCol() + 2);
		vicinityData[15] = getItem(pos.getRow(), pos.getCol() + 2);
		return vicinityData;
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder("" + sequenceHash);
		// With the player and the boxes contained in the HashMap,
		// parse the contents of the HashMap accordingly into a string.
		//
		// For example ang contents ng HashMap is:
		// @ 0,0
		// $ 3,8
		// $ 4,7
		// $ 2,2
		//
		// Then ang string form is, assuming SokoBotSequence hash is 13739415:
		// "13739415|@0,0|$3,8|$4,7|$2,2"
		for (Map.Entry<Position, Character> entry : itemsMap.entrySet()) {
			strb.append("|").append(entry.getValue()).append(entry.getKey().getRow())
				.append(",").append(entry.getKey().getCol());
		}
		// Return the string
		return strb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return obj instanceof GameState &&
			//Objects.equals(stringForm, obj.toString());
			this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		// The hash of this class will come from its string form
		return Objects.hashCode(stringForm);
	}
	
	
}
