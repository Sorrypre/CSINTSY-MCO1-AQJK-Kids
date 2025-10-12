package solver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * Represents the state of the game, including positions of items.
 * Uses a map to associate positions with characters representing items.
 * Provides methods to manipulate and query the game state.
 */
public class GameState {
    private final Map<Position, Character> itemsMap = new HashMap<>();
	
	private GameState() { }
	
	/* Constructor to convert given char[][] to a state */
	public GameState(Character[][] items) {
		for (int i = 0; i < state.length; i++)
			for(int j = 0; j < state[i].length; j++)
				// Add to itemsMap when the object is a box or a player
				if ('$' == state[i][j] || '@' == state[i][j])
					setItem(i, j, state[i][j]); else;
		if (getPlayerPos() == null)
			throw new NullPointerException("No player found on the given item data"); else;
	}

	public GameState getCopy(Character[][] map) {
		Character[][] itemData = new Character[map.length][];
		for (Character[] row : itemData)
			Arrays.fill(row, ' ');
		for (Map.Entry<Position, Character> entry : itemsMap.entrySet())
			itemData[entry.getKey().getRow()][entry.getKey().getCol()] = entry.getValue();
		return new GameState(itemData);
	}
	
    public Character getItem(int row, int col) {
        Position pos = new Position (row, col);
        // If the position is not in the map, return a space character
        return itemsMap.getOrDefault(pos, ' ');
    }
	
    public void setItem(int row, int col, Character item) {
        Position pos = new Position (row, col);
        if (item == ' ') {
            itemsMap.remove(pos);// Remove the entry if the item is a space
        }
        else itemsMap.put(pos, item);
    }
	
	public void removeItem(int row, int col) {
		itemsMap.remove(new Position(row, col));
	}
	
	// Matches Wall-Wall, Wall-Box, Box-Box deadlock for each box
	public boolean isAnyBoxCornered(Character[][] map) {
		// Iterate through itemsMap
		Character[] vicinity;
		for (Map.Entry<Position, Character> entry : itemsMap.entrySet()) {
			// Deadlock only happens when the box is not on a goal tile
			if (entry.getValue() == '$' && map[entry.getKey().getRow()][entry.getKey().getCol()] != '.') {
				// Get map characters on the box's vicinity as follows:
				//     [0]
				//  [2] $ [3]
				//     [1]
				vicinity = new Character[4];
				vicinity[0] = map[entry.getKey().getRow() - 1][entry.getKey().getCol()];
				vicinity[1] = map[entry.getKey().getRow() + 1][entry.getKey().getCol()];
				vicinity[2] = map[entry.getKey().getRow()][entry.getKey().getCol() - 1];
				vicinity[3] = map[entry.getKey().getRow()][entry.getKey().getCol() + 1];
				// Check if there is a box or a wall adjacent to [0] or [1]
				if (("$#".indexOf(vicinity[0]) != -1 || "$#".indexOf(vicinity[1]) != -1) &&
					("$#".indexOf(vicinity[2]) != -1 || "$#".indexOf(vicinity[3]) != -1))
					return true;
				else
					vicinity = null;
			}
		}
		return false;
	}
	
	public boolean isSolution(Character[][] map, Integer expectedBoxes) {
		int counter = 0;
		if (map != null)
			for (Map.Entry<Position, Character> entry : itemsMap.entrySet())
				if ('$' == entry.getValue() && '.' == map[entry.getKey().getRow()][entry.getKey().getCol()])
					counter++;
		return map != null && expectedBoxes != null && expectedBoxes == counter;
	}
	
	public Position getPlayerPos() {
		for (Map.Entry<Position, Character> entry : itemsMap.entrySet())
			if (entry.getValue().equals('@'))
				return entry.getKey();
		return null; // pag ichcheck ng constructor this is convenient
	}
	
	public Object[] getPlayerVicinityData() {
		// 4 inner directions * 2 outer directions * 2 data types
		Object[] vicinityData = new Object[4 * 2 * 2];
		// Current player position
		Position pos = getPlayerPos();
		
		// Get objects surrounding the player as follows:
		//        [1]
		//        [0]
		//  [5][4] @ [6][7]
		//        [2]
		//        [3]
		// even indices are Positions, odd indices are Characters example: [0] = Position, [1] = Character
		// --Up--
        // [0] Inner Up
		vicinityData[0] = new Position(pos.getRow() - 1, pos.getCol());
		vicinityData[1] = getItem(pos.getRow() - 1, pos.getCol());
        // [1] Outer Up
		vicinityData[2] = new Position(pos.getRow() - 2, pos.getCol());
		vicinityData[3] = getItem(pos.getRow() - 2, pos.getCol());
		// --Down--
        // [2] Inner Down
		vicinityData[4] = new Position(pos.getRow() + 1, pos.getCol());
		vicinityData[5] = getItem(pos.getRow() + 1, pos.getCol());
        // [3] Outer Down
		vicinityData[6] = new Position(pos.getRow() + 2, pos.getCol());
		vicinityData[7] = getItem(pos.getRow() + 2, pos.getCol());
        // --Left--
        // [4] Inner Left
		vicinityData[8] = new Position(pos.getRow(), pos.getCol() - 1);
		vicinityData[9] = getItem(pos.getRow(), pos.getCol() - 1);
        // [5] Outer Left
		vicinityData[10] = new Position(pos.getRow(), pos.getCol() - 2);
		vicinityData[11] = getItem(pos.getRow(), pos.getCol() - 2);
		// --Right--
        // [6] Inner Right
		vicinityData[12] = new Position(pos.getRow(), pos.getCol() + 1);
		vicinityData[13] = getItem(pos.getRow(), pos.getCol() + 1);
        // [7] Outer Right
		vicinityData[14] = new Position(pos.getRow(), pos.getCol() + 2);
		vicinityData[15] = getItem(pos.getRow(), pos.getCol() + 2);
		return vicinityData;
	}
}
