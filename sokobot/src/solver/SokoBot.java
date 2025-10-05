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
		
		private boolean isDeadlock(Character[][] currentState)
		{
			boolean result = false;
			// Conditions for deadlock:
			// - One of the boxes reach a non-target corner of a map
			// - ?
			// - ?
			return result;
		}
		
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
