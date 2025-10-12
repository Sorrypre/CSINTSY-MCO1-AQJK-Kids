package solver;
import java.util.Queue;

public class SokoBot {

	public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
		return new SokoBotSequence(mapData, itemsData).get();
	}
	
	private class SokoBotSequence
	{
		public SokoBotSequence(char[][] mapData, char[][] itemsData)
		{
			if (null == mapData || null == itemsData)
				throw new IllegalArgumentException("mapData or itemsData cannot be null");
			
			Character[][] items;
			this.boxes = 0;
			this.mapData = new Character[mapData.length][];
			for (int i = 0; i < mapData.length; i++)
			{
				for (int j = 0; i < mapData[i].length; j++)
				{
					this.mapData[i][j] = mapData[i][j];
					if ('.' == mapData[i][j])
						this.boxes++;
				}
			}
			if (0 == boxes)
				throw new NullPointerException("There are no boxes found in this game level");
			
			items = new Character[itemsData.length][];
			for (int i = 0; i < itemsData.length; i++)
				for (int j = 0; j < itemsData[i].length; j++)
					items[i][j] = itemsData[i][j];
			
			this.itemsData = new GameState(items);
		}
		
		public String get()
		{
			// Solution to the given Sokoban stage using A* search, etc.
			//
			//
			return finalSequence;
		}
		
		private SokoBotSequence() {}
		
		private Boolean[] Actions(GameState currentItemsData)
		{
			Boolean[] a = new Boolean[4];
			if (!currentItemsData.isAnyBoxCornered(mapData))
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
		
		private GameState Succ(GameState currentItemsData, char action)
		{
			Object[] vicinityData;
			Character objInner, objOuter;
			Position objPosInner, objPosOuter, playerPos;
			GameState succ;
			int move;
			if (!currentItemsData.isAnyBoxCornered(mapData))
			{
				// Clone the data so the parameter would not be overwritten
				succ = currentItemsData.getCopy(mapData);
				playerPos = currentItemsData.getPlayerPos();
				// Represent the index based on the chosen action
				move =
					'u' == action ? 0 :
					'd' == action ? 4 :
					'l' == action ? 8 : 12;
				// Get respective vicinity data based on the action
				vicinityData = succ.getPlayerVicinityData();
				objPosInner = (Position)vicinityData[move + 0];
				objInner = (Character)vicinityData[move + 1];
				objPosOuter = (Position)vicinityData[move + 2];
				objOuter = (Character)vicinityData[move + 3];
				// Make sure player is not towards a wall
				if (mapData[objPosInner.getRow()][objPosInner.getCol()] != '#')
				{
					// If player is towards a box, verify if it's clear for pushing by checking if there is
					// no wall nor another box behind it
					if ('$' == objInner && '#' != mapData[objPosOuter.getRow()][objPosOuter.getCol()] &&
						' ' == objOuter)
					{
						// Player is clear for pushing the box
						succ.setItem(objPosInner.getRow(), objPosInner.getCol(), '@');
						succ.setItem(objPosOuter.getRow(), objPosOuter.getCol(), '$');
						// Make sure to remove the last position of the player from the HashMap
						succ.removeItem(playerPos.getRow(), playerPos.getCol());
					}
					// If the player is not towards a box whilst assuming player is not towards a wall as
					// made sure by the outer if-block, then the player is towards a vacant tile and is free
					// to move towards that way
					else if ('$' != objInner)
					{
						// Player is clear for moving
						succ.setItem(objPosInner.getRow(), objPosInner.getCol(), '@');
						// Make sure to remove the last position of the player from the HashMap
						succ.removeItem(playerPos.getRow(), playerPos.getCol());
					}
					// If none of these conditions fit, the player must be pushing a box with a
					// wall or another box behind it, so the player cannot move, i.e. do nothing
					else;
				}
				// Return resulting succeeding state
				return succ;
			}
			// The state is deadlocked, the algorithm should not proceed further
			else return null;
		}
		
		Integer boxes;
		Character[][] mapData;
		GameState itemsData;
		String finalSequence = "";
	}
}
