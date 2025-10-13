package solver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
 * Represents the state of the game, including positions of items.
 * Uses a map to associate positions with characters representing items.
 * Provides methods to manipulate and query the game state.
 */
public class GameState {
    private final Map<Position, Character> itemsMap = new HashMap<>();
	private int sequenceHash;
	private String stringForm;
	
	private GameState() { }
	
	private void updateStringForm() {
		stringForm = toString();
	}
	
	/* Constructor to convert given char[][] to a state */
	public GameState(int sequenceHash, Character[][] items) {
		this.sequenceHash = sequenceHash;
		for (int i = 0; i < state.length; i++)
			for(int j = 0; j < state[i].length; j++)
				// Add to itemsMap when the object is a box or a player
				if ('$' == state[i][j] || '@' == state[i][j])
					setItem(i, j, state[i][j]); else;
		if (getPlayerPos() == null)
			throw new NullPointerException("No player found on the given item data"); else;
		// Always update string form when altering the contents of the state
		updateStringForm();
	}
	
	public GameState getCopy(Character[][] map) {
		Character[][] itemData = new Character[map.length][];
		for (Character[] row : itemData)
			Arrays.fill(row, ' ');
		for (Map.Entry<Position, Character> entry : itemsMap.entrySet())
			itemData[entry.getKey().getRow()][entry.getKey().getCol()] = entry.getValue();
		return new GameState(instanceHash, itemData);
	}
	
	public HashMap<Position, Character> getItemsPos() {
		return itemsMap;
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
			strb.append("|" + itemsMap.getValue() + "" + itemsMap.getKey().getRow() + 
				"," + itemsMap.getKey().getCol());
		}
		// Return the string
		return strb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof GameState ? stringForm.equals((GameState)obj.toString()) : false;
	}
	
	@Override
	public int hashCode() {
		// The hash of this class will come from its string form
		return Objects.hashCode(stringForm);
	}
}
