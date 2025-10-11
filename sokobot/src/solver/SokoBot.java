package solver;
import java.util.Queue;

public class SokoBot {

	public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
		return new SokoBotSequence(width, height, mapData, itemsData).get();
	}
	
	private class SokoBotSequence
	{
		public SokoBotSequence(int width, int height, char[][] mapData, char[][] itemsData)
		{
			if (null == mapData || null == itemsData)
				throw new IllegalArgumentException("mapData or itemsData cannot be null");
			this.width = width;
			this.height = height;

			this.mapData = new Character[mapData.length][];
			for (int i = 0; i < mapData.length; i++)
				for (int j = 0; i < mapData[i].length; j++)
					this.mapData[i][j] = mapData[i][j];
			
			this.itemsData = new Character[itemsData.length][];
			for (int i = 0; i < itemsData.length; i++)
				for (int j = 0; j < itemsData[i].length; j++)
					this.itemsData[i][j] = itemsData[i][j];
		}
		
		public String get()
		{
			// Solution to the given Sokoban stage using A* search, etc.
			//
			//
			return finalSequence;
		}
		
		private SokoBotSequence() {}
		
		private Boolean[] Actions(Character[][] currentItemsData)
		{
			Boolean[] a = new Boolean[4];
			if (!isDeadlock(currentItemsData))
			{
				// IMPORTANT!!
				// The array of moves will follow the order: up, down, left, right.
				// When the move is not possible, please set that index to false.
				// For example, if up and left are not possible moves, then the contents
				// of the array would be: { false, true, false, true }.
				
				// Obtain possible moves from state here
				//
				//
			}
			// Return resulting set of moves
			return a;
		}
		
		private Character[][] Succ(Character[][] currentItemsData, char action)
		{
			Character[][] s;
			if (!isDeadlock(currentItemsData))
			{
				// Get a copy of the currentState
				s = new Character[width][height];
				for (int i = 0; i < currentItemsData.length; i++)
					System.arraycopy(currentItemsData[i], 0, s[i], 0, currentItemsData[i].length);
				
				// Update state with action here
				//
				//
				
				// Return resulting succeeding state
				return s;
			}
			else return currentItemsData;
		}
		
		/* Moved function to the GameState class
		// But the question is, need na rin ba nating ichange ung mga params dito
		// from Character[][] into GameState?
		
		private boolean isDeadlock(Character[][] currentState)
		{
			boolean result = false;
            // find each box in the current state
            int row, col;
            for (row = 0; row < width; row++) {
                for (col = 0; col < height; col++) {
                    //get the position of each box in the state
                    //before checking for deadlock, make sure box is not in the goal
                    //In this implementation, the cases will be checked together in one conditional
                    // Only one deadlock must be found for this function to return true
                    if (currentState[row][col] == '$' && mapData[row][col] != '.') {
                        //Case 1.1: WALL-WALL DEADLOCK (top-left corner)
                        if (mapData[row - 1][col] == '#' && mapData[row][col - 1] == '#') {
                            result = true;
                            break;
                        }

                    }
                }
            }
            // for each box found,
            // check if the box is in a deadlock position
			// Conditions for deadlock:

            // CASE 1: WALL-WALL CORNER DEADLOCK
            //
            //  XXX
            //  XO
            //  X
            //
            // CASE 2: WALL-BOX CORNER DEADLOCK
            //
            //  X O
            //  X O
            //  X
            //
            // CASE 3: BOX-BOX CORNER DEADLOCK
            //
            //    O
            //  O O
            //

			return result;
		}
		*/
		
		private boolean isSolution(Character[][] currentState)
		{
			boolean result = false;
			// Check if the given state is the solution for the puzzle
			//
			//
			return result;
		}
		
		Integer width, height;
		Character[][] mapData, itemsData;
		String finalSequence = "";
	}
}
