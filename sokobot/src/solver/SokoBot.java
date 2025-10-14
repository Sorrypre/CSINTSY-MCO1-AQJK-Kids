package solver;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

public class SokoBot {

	public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
		return new SokoBotSequence(width, height, mapData, itemsData).getSolutionAStar();
	}
	
	private class SokoBotSequence implements stateBasedModelFunctions
	{
		public SokoBotSequence(int width, int height, char[][] mapData, char[][] itemsData)
		{
			if (null == mapData || null == itemsData)
				throw new IllegalArgumentException("mapData or itemsData cannot be null");

			this.mapData = new Character[height][width];
			this.numGoals = 0;

			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					this.mapData[i][j] = mapData[i][j];
					if ('.' == mapData[i][j])
						this.numGoals++;
				}
			}

            //initialize first state by converting itemsData to Character[][]
			Character[][] items = new Character[height][width];
			for (int i = 0; i < itemsData.length; i++)
				for (int j = 0; j < itemsData[i].length; j++)
					items[i][j] = itemsData[i][j];
			
			this.intialStateItemsData = new GameState(hashCode(), items);
		}
		
		public String getSolutionAStar()
		{
			// Solution to the given Sokoban stage using A* search, etc.
			// implement frontier using priority queue
			// implement explored using hash set
            // use a heuristic function to estimate the cost from the current state to the goal state
            // use a cost function to estimate the cost from the start state to the current state
            // return the sequence of actions to reach the goal state
			return finalSequence;
		}
		
		private SokoBotSequence() {}

        private boolean isDeadlock(GameState state) {
            // Check for simple deadlocks: box in a corner not on a goal
            // Check for more complex deadlocks: boxes against walls or other boxes in a way that makes it impossible to move them to goals
            // Return true if a deadlock is detected, false otherwise
            return false; // Placeholder return value
        }
		
		private Character[] Actions(GameState currentItemsData)
		{
            Character[] actions = new Character[4];

			return actions;
		}

        @Override
        public boolean isEnd(GameState state) {
            return state.isSolution(mapData, numGoals);
        }

        @Override
        public int cost() {
            return 1;
        }

        @Override
        public Boolean actions(GameState state) {
            return null;
        }

        public GameState Succ(GameState currentItemsData, Character action)
		{
			// On hold
            return null;
		}

        // may need to be a local variable of the getSolutionAStar method alongside the frontier priority queue
		private HashSet<String> visited = new HashSet<String>();

		private Integer numGoals;
		private Character[][] mapData;
		private GameState intialStateItemsData;
		private String finalSequence = "";
	}
}
