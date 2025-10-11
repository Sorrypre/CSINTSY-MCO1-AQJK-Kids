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
}
