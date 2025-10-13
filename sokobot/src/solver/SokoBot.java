package solver;
import java.util.HashSet;
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
			
			this.itemsData = new GameState(hashCode(), items);
		}
		
		public String get()
		{
			// Solution to the given Sokoban stage using A* search, etc.
			//
			//
			return finalSequence;
		}
		
		private SokoBotSequence() {}
		
		private Character[] Actions(GameState currentItemsData)
		{
                //			Boolean[] a = new Boolean[4];
                //			if (!currentItemsData.isAnyBoxCornered(mapData))
                //			{
                //				// IMPORTANT!!
                //				// The array of moves will follow the order: up, down, left, right.
                //				// When the move is not possible, please set that index to false.
                //				// For example, if up and left are not possible moves, then the contents
                //				// of the array would be: { false, true, false, true }.
                //
                //				// Obtain possible moves from state here
                //				//
                //				//
                //                if ()
                //			}
                            // Return resulting set of moves
            // actions represents {up 'u', down 'd', left 'l', right 'r'}
            // if the move is not possible, the index of the assigned Character is null
            Character[] actions = new Character[4];

            //-- makeMove Method Pseudocode (incomplete)

			// comment (deppy0): di na need magcall ng getPlayerPos() all the time,
			// see getPlayerVicinityData() and I think andun na lahat ng need mong
			// Position and the corresponding symbols on that position, you just need
			// to cast it to the proper type pag gagamitin mo na dito kasi array of Object sya

            //Player Move (No Box Push) if no box in the way
			
            // Check if up is possible
            if (mapData[currentItemsData.getPlayerPos().getRow() - 1][currentItemsData.getPlayerPos().getCol()] != '#' ||
                    mapData[currentItemsData.getPlayerPos().getRow() - 1][currentItemsData.getPlayerPos().getCol()] != '#')
                actions[0] = 'u'; // up
            // Check if down is possible
            // Check if left is possible
            // Check if right is possible

            // else if box in the way
            // check cell beyond the box so (position of box + direction of move = cell beyond the box)
            // if cell beyond the box is empty or a goal then player move and push box valid
            // else move invalid if wall or another box

            //optimization (focus on box pushes only rather than indiividual player moves) Use BFS to check if player can reach the position behind the box to push it

			return actions;
		}
		
		private GameState Succ(GameState currentItemsData, Character action)
		{
			// On hold
		}
		
		private HashSet<String> visited = new HashSet<String>();
		
		private Integer boxes;
		private Character[][] mapData;
		private GameState itemsData;
		private String finalSequence = "";
	}
}
