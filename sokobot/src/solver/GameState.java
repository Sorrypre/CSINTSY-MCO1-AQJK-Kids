package solver;

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
					itemsMap.put(new Position(i, j), state[i][j]); else;
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
}
