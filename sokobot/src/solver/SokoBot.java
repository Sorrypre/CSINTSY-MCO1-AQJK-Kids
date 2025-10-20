package solver;
import java.util.*;

public class SokoBot {

	public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
		return new SokoBotSequence(width, height, mapData, itemsData).toString();
	}
	
	private class SokoBotSequence implements stateBasedModelFunctions
	{
		public SokoBotSequence(int width, int height, char[][] mapData, char[][] itemsData)
		{
			if (null == mapData || null == itemsData)
				throw new IllegalArgumentException("mapData or itemsData cannot be null");

			this.mapData = new Character[height][width];

			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					this.mapData[i][j] = mapData[i][j];
					if ('.' == mapData[i][j])
						this.goalTiles.add(new Position(i, j));
				}
			}

            //initialize first state by converting itemsData to Character[][]
			Character[][] items = new Character[height][width];
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++)
					items[i][j] = itemsData[i][j];
			
			this.initialStateItemsData = new GameState(hashCode(), items);
		}
		
		@Override
		public String toString()
		{
			// Declarations
			HashSet<GameState> explored = new HashSet<GameState>();
			ArrayList<Node> frontier = new ArrayList<Node>();
			StringBuilder outcome = new StringBuilder();
			Node solution_tree = new Node(initialStateItemsData, null, null, 0);
			Node minimum = solution_tree;
			GameState current = initialStateItemsData;
			ArrayList<Moveset> actions = null;
			Object[] next = null;
			Node i = null;
			// Traverse until solution is found or frontier is empty
			explored.add(current);
			while (!(isEnd(current) || frontier.isEmpty())) {
				// Find all possible actions on the current state
				actions = Actions(current);
				for (Moveset m : actions) {
					// For each possible action, find the succeeding state
					next = Succ(current, m);
					// If the next state is both not deadlocked and not explored,
					// then add the state to the frontier as a new Node
					if ((GameState)next[0] != null && !explored.contains((GameState)next[0]))
						frontier.add(new Node((GameState)next[0], (Moveset)next[1], minimum, (Integer)next[2]));
				}
				if (!frontier.isEmpty()) {
					// Assuming the frontier is not empty, find the Node with the least f(n)
					minimum = Collections.min(frontier, Comparator.comparing(Node::getFScore));
					current = minimum.getState();
					// Remove that Node from the frontier and mark its state as explored
					frontier.remove(minimum);
					explored.add(current);
				}
			}
			// Concatenate the move sequences found in the connected nodes
			// from the leaf to the parent
			i = minimum;
			while (i != null) {
				outcome.append(new StringBuilder(minimum.getMoveset().getMoveSequence())
					.reverse().toString());
				i = i.getParent();
			}
			return outcome.reverse().toString();
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
            return state.isSolution(mapData, goalTiles.size());
        }

        @Override
        public int cost() {
            return 1;
        }

        @Override
        public ArrayList<Character> Actions(GameState state) {
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
		
		// Return value contains:
		// - new GameState, or null if the succeeding state is deadlocked
		// - same MoveSet except the identified push direction appended to ArrayList<Character>
		// - Manhattan distance between the box of the new state and the goal tile in the mapData
		//   (this is computed on a separate method)
		@Override
        public Object[] Succ(GameState g, Moveset m) {
			// Gather information from parameters
			GameState gnew = g.getCopy(mapData);
			Position statePos = g.getPlayerPos();
			Position oldPos = m.getMSPlayerPos();
			Position oldBoxPos = m.getBoxPos();
			ArrayList<Character> move_sequence = m.getMoveSequence();
			// Declarations for process
			Position innerPos = null;
			Position outerPos = null;
			Position newPos = null;
			Position newBoxPos = null;
			Object[] playerVicinityData = null;
			boolean succFound = false;
			int innerIndex = 0;
			// Verify state by checking if player position in the given Moveset is not pointing to a wall
			if (gnew.getItem(oldPos.getRow(), oldPos.getCol()) == ' ' && mapData[oldPos.getRow()][oldPos.getCol()] != '#') {
				// Move player on copy state
				gnew.removeItem(statePos.getRow(), statePos.getCol());
				gnew.setItem(oldPos.getRow(), oldPos.getCol(), '@');
				// Verify state by checking if box position in the given Moveset is indeed a box
				if (gnew.getItem(oldBoxPos.getRow(), oldBoxPos.getCol()) == '$') {
					playerVicinityData = gnew.getPlayerVicinityData();
					// Test each direction and push wherever oldBoxPos matches
					do {
						innerPos = (Position)playerVicinityData[innerIndex];
						outerPos = (Position)playerVicinityData[innerIndex + 2];
						if (innerPos.equals(oldBoxPos) && gnew.getItem(outerPos.getRow(), outerPos.getCol()) == ' ') {
							if (mapData[outerPos.getRow()][outerPos.getCol()] != '#') {
								gnew.setItem(outerPos.getRow(), outerPos.getCol(), '$');
								gnew.setItem(innerPos.getRow(), innerPos.getCol(), '@');
								gnew.removeItem(oldPos.getRow(), oldPos.getCol());
								newPos = new Position(innerPos.getRow(), innerPos.getCol());
								newBoxPos = new Position(outerPos.getRow(), outerPos.getCol());
								switch (innerIndex) {
									default:
										throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": invalid innerIndex, got " + innerIndex);
									case 0:
										move_sequence.add('u');
										break;
									case 4:
										move_sequence.add('d');
										break;
									case 8:
										move_sequence.add('l');
										break;
									case 12:
										move_sequence.add('r');
										break;
								}
								succFound = true;
							}
							else
								// Error handling in case of a mistake somewhere else
								throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": expecting row " + outerPos.getRow() +
									" col " + outerPos.getCol() + " to be ' ', but found '" + gnew.getItem(outerPos.getRow(), outerPos.getCol()) + "'");
						} else innerIndex += 4;
					} while (!succFound && innerIndex <= 4 * 3);
					// Box push successful
					if (succFound) {
						// Return value contains:
						// - new GameState
						// - same MoveSet except the identified push direction appended to ArrayList<Character>
						// - Manhattan distance between the box of the new state and the goal tile in the mapData
						//   (this is computed on a separate method)
						return new Object[] {
							gnew.isAnyBoxCornered(mapData) ? null : gnew,
							new Moveset(newBoxPos, newPos, move_sequence),
							computeManhattan(newBoxPos)
						};
					}
					else
						// Error handling in case of a mistake somewhere else
						throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": no boxes found on desired direction");
				}
				else
					// Error handling in case of a mistake somewhere else
					throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": expecting row " + oldBoxPos.getRow() + " col " +
                            oldBoxPos.getCol() + " to be '$', but found '" + gnew.getItem(oldBoxPos.getRow(), oldBoxPos.getCol()) + "'");
			}
			else
				// Error handling in case of a mistake somewhere else
				throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": row " + oldPos.getRow() +
					" col " + oldPos.getCol() + " is not vacant");
		}
		
		private Integer computeManhattan(Position boxPos) {
			ArrayList<Integer> distances = new ArrayList<Integer>();
			for (Position goal : goalTiles)
				distances.add(Math.abs(boxPos.getCol() - goal.getCol()) +
					Math.abs(boxPos.getRow() - goal.getRow()));
			return Collections.max(distances);
		}
		
        // may need to be a local variable of the getSolutionAStar method alongside the frontier priority queue
		private ArrayList<Position> goalTiles = new ArrayList<Position>();
		private Character[][] mapData;
		private GameState initialStateItemsData;
	}
}
