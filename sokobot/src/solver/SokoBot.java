package solver;
import java.util.*;

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
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++)
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

        private boolean isValidMove(GameState state, Character move) {
            // Check if the move is valid based on the current state and mapData
            Position playerPos = state.getPlayerPos();
            Position movePos;
            int moveNumerical;

            // convert move character to vicinity coordinate
            switch (move) {
                case 'U':
                    moveNumerical = 0;
                    movePos = (Position)state.getPlayerVicinityData()[moveNumerical];
                    break;
                case 'D':
                    moveNumerical = 4;
                    movePos = (Position)state.getPlayerVicinityData()[moveNumerical];
                    break;
                case 'L':
                    moveNumerical = 8;
                    movePos = (Position)state.getPlayerVicinityData()[moveNumerical];
                    break;
                case 'R':
                    moveNumerical = 12;
                    movePos = (Position)state.getPlayerVicinityData()[moveNumerical];
                    break;
                default:
                    return false; // Invalid move character
            }

            // Check if the move is within bounds and not into a wall
            if (mapData[movePos.getRow()][movePos.getCol()] == '#') {
                return false; // Wall
            }

            // Check if pushing a box is valid
            if (state.getPlayerVicinityData()[moveNumerical + 1].equals('$')) { // If there's a box in the direction of the move
                // Check if the space beyond the box is free (not a wall or another box)
                Position outerVicinity = ((Position)state.getPlayerVicinityData()[moveNumerical + 2]);
                return mapData[outerVicinity.getRow()][outerVicinity.getCol()] != '#'
                        && !state.getPlayerVicinityData()[moveNumerical + 3].equals('$'); // Can't push the box because there's a wall or another box
            }

            // Return true if the move is valid
            return true;
        }

        private boolean isDeadlock(GameState state, Character move) {
            // Check for simple deadlocks: box in a corner not on a goal
            // Check for more complex deadlocks: boxes against walls or other boxes in a way that makes it impossible to move them to goals
            // Return true if a deadlock is detected, false otherwise
            return false; // Placeholder return value
        }

        /*  ----------------------------------------------------------------
         *  Methods required by the stateBasedModelFunctions interface
         * ----------------------------------------------------------------
         */
        @Override
        public boolean isEnd(GameState state) {
            return state.isSolution(mapData, numGoals);
        }

        @Override
        public int cost() {
            return 1;
        }

        @Override
        public ArrayList<Character> actions(GameState state) {
            ArrayList<Character> actions = new ArrayList<>(Arrays.asList('u', 'd', 'l', 'r'));

            // Remove actions that would lead to deadlocks and invalid moves
            for (Character action : actions) {
                // first, check the move if it is a valid move
                if (!isValidMove(state, action)) {
                    actions.remove(action);
                }
                // then, check if the move would lead to a deadlock
                else if (isDeadlock(state, action)) {
                    actions.remove(action);
                }
            }
            return actions;
        }
		
		// If successful, returns a new GameState where the target box of the given Moveset is
		// pushed by the player
		// Returns null if given GameState is already a deadlock and shall not be traversed further
		// Returns the given GameState if the box cannot be pushed towards the desired direction
		// of the given Moveset
		@Overrides
        public GameState Succ(GameState g, Moveset m) {
			// Check if the current state is a deadlock
			if (g.isAnyBoxCornered(mapData))
				return null; // Dead state
			// Gather information from parameters
			GameState gnew = g.getCopy();
			Position statePos = g.getPlayerPos();
			Position oldPos = m.getMSPlayerPos();
			Position boxPos = m.getBoxPos();
			// Declarations for process
			Position innerPos = null;
			Position outerPos = null;
			Object[] playerVicinityData = null;
			boolean succFound = false;
			int innerIndex = 0;
			// Verify state by checking if player position in the given Moveset is not pointing to a wall
			if (gnew.getItem(oldPos.getRow(), oldPos.getCol()) == ' ' && mapData[oldPos.getRow()][oldPos.getCol()] != '#') {
				// Move player on copy state
				gnew.removeItem(statePos.getRow(), statePos.getCol());
				gnew.setItem(oldPos.getRow(), oldPos.getCol(), '@');
				// Verify state by checking if box position in the given Moveset is indeed a box
				if (gnew.getItem(boxPos.getRow(), boxPos.getCol()) == '$') {
					playerVicinityData = gnew.getPlayerVicinityData();
					// Test each direction and push wherever boxPos matches
					do {
						innerPos = (Position)playerVicinityData[innerIndex];
						outerPos = (Position)playerVicinityData[innerIndex + 2];
						if (innerPos.equals(boxPos) && gnew.getItem(outerPos.getRow(), outerPos.getCol()) == ' ') {
							if (mapData[outerPos.getRow()][outerPos.getCol()] != '#') {
								gnew.setItem(outerPos.getRow(), outerPos.getCol(), '$');
								gnew.setItem(innerPos.getRow(), innerPos.getCol(), '@');
								gnew.removeItem(oldPos);
								succFound = true;
							}
							else
								// Error handling in case of a mistake somewhere else
								throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": expecting row " + outerPos.getRow() +
									" col " + outerPos.getCol() + " to be ' ', but found '" + gnew.getItem(outerPos.getRow(), outerPos.getCol()) + "'");
						} else innerIndex += 4;
					} while (!succFound && innerIndex <= 4 * 3);
					// Box push successful
					if (succFound)
						return gnew.isAnyBoxCornered(mapData) ? null : gnew;
					else
						// Error handling in case of a mistake somewhere else
						throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": no boxes found on desired direction");
				}
				else
					// Error handling in case of a mistake somewhere else
					throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": expecting row " + boxPos.getRow() + " col " +
						boxPos.getCol() + " to be '$', but found '" + gnew.getItem(boxPos.getRow(), boxPos.getCol()) + "'");
			}
			else
				// Error handling in case of a mistake somewhere else
				throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": row " + oldPos.getRow() +
					" col " + oldPos.getCol() + " is not vacant");
		}
		
        // may need to be a local variable of the getSolutionAStar method alongside the frontier priority queue
		private HashSet<String> visited = new HashSet<String>();
		private Integer numGoals;
		private Character[][] mapData;
		private GameState intialStateItemsData;
		private String finalSequence = "";
	}
}
