package solver;
import java.util.*;

/*
 * SokoBot class that solves Sokoban puzzles using A* search algorithm
 *  used for integrating with the Sokoban framework provided solveSokobanPuzzle method
 *  creates a SokoBotSequence instance to handle the puzzle solving logic
 */
public class SokoBot {
    // Main method to solve Sokoban puzzle that takes width, height, mapData, and itemsData as parameters
	public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
		return new SokoBotSequence(width, height, mapData, itemsData).toString();
	}
	/*
	 * SokoBotSequence class that implements stateBasedModelFunctions interface
	 * handles the puzzle solving logic using A* search algorithm
	 * initializes mapData and goalTiles based on the provided mapData
	 * initializes the initial game state based on the provided itemsData using GameState class
	 */
	private class SokoBotSequence implements stateBasedModelFunctions
	{
        /*
         * Constructor that initializes mapData, goalTiles, and initialStateItemsData
         *  takes width, height, mapData, and itemsData as parameters
         *  throws IllegalArgumentException if mapData or itemsData is null
         */
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
			
			this.initialStateItemsData = new GameState(hashCode(), items, goalTiles);
		}
		
		/*
		    * toString method that implements the A* search algorithm to find the solution
		    * returns the solution as a string of moves
		
		@Override
		public String toString()
		{
			// Declarations
			HashSet<GameState> explored = new HashSet<>();
			ArrayList<Node> frontier = new ArrayList<>();
			StringBuilder outcome = new StringBuilder();
			Node solution_tree = new Node(initialStateItemsData, null, null, 0);
			Node minimum = solution_tree;
			GameState current = initialStateItemsData;
			ArrayList<Moveset> actions = null;
			Object[] next = null;
			Node i = null;
			// Traverse until solution is found or frontier is empty
			do {
				// Add current game state to explored states
				explored.add(current);
				// Find all possible actions on the current state
				actions = Actions(current);
				for (Moveset m : actions) {
					// For each possible action, find the succeeding state
					next = Succ(current, m);
					// If the next state is both not deadlocked and not explored,
					// then add the state to the frontier as a new Node
					//System.out.println((GameState)next[0] != null);
					//System.out.println(!explored.contains((GameState)next[0]));
					if ((GameState)next[0] != null && !explored.contains((GameState)next[0]))
						frontier.add(new Node((GameState)next[0], (Moveset)next[1], minimum, (Integer)next[2]));
				}
				if (!frontier.isEmpty()) {
					// Assuming the frontier is not empty, find the Node with the least f(n)
					minimum = Collections.min(frontier, Comparator.comparingInt(Node::getFScore));
					// Assign the state of the minimum to current so that it will be added to
					// the explored list when the loop repeats
					current = minimum.getState();
					// Remove that Node from the frontier
					frontier.remove(minimum);
				}
			} while (!(isEnd(current) || frontier.isEmpty()));
			// Concatenate the move sequences found in the connected nodes
			// from the leaf to the parent
			i = minimum;
			while (i != null) {
				if (i.getMoveset() != null)
					outcome.append(new StringBuilder(i.getMoveset().getMoveSequence())
						.reverse().toString());
				i = i.getParent();
			}
			System.out.println(outcome.reverse().toString());
			return outcome.reverse().toString();
		}
		*/
		
		/*
		@Override
		public String toString() {
			StringBuilder outcome = new StringBuilder();
			PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparing(Node::getFScore));
			HashSet<GameState> explored = new HashSet<>();
			Node solution_tree = new Node(initialStateItemsData, null, null, goalTiles);
			Node current = solution_tree;
			Node next;
			GameState current_state;
			Object[] next_state;
			ArrayList<Moveset> actions;
			Node i;
			frontier.add(current);
			while (!frontier.isEmpty()) {
				current = frontier.poll();
				current_state = current.getState();
				explored.add(current_state);
				if (isEnd(current_state)) {
					break;
				} else {
					actions = Actions(current_state);
					for (Moveset m : actions) {
						next_state = Succ(current_state, m);
						System.out.println((GameState)next_state[0] != null);
						System.out.println(!explored.contains((GameState)next_state[0]));
						if ((GameState)next_state[0] != null && !explored.contains((GameState)next_state[0])) {
							next = new Node((GameState)next_state[0], (Moveset)next_state[1], current, goalTiles);
							if (current_state.equals(initialStateItemsData) || next.getFScore() < current.getFScore())
								frontier.add(next);
						}
					}
				}
			}
			// Concatenate the move sequences found in the connected nodes
			// from the leaf to the parent
			i = current;
			while (i != null) {
				if (i.getMoveset() != null) {
					outcome.append(new StringBuilder(i.getMoveset().getMoveSequence())
						.reverse().toString());
				}
				i = i.getParent();
			}
			return outcome.toString();
		}
		*/
		
		@Override
		public String toString() {
			return compute();
		}
		
		private String compute() {
			StringBuilder result = new StringBuilder();
			Node leaf = search();
			Node i;
			if (leaf != null) {
				i = leaf;
				while (i != null) {
					result.insert(0, i.getMSSequence());
					i = i.getParent();
				}
			}
			return result.toString();
		}
		
		private Node search() {
			PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(Node::getFScore));
			HashSet<GameState> explored = new HashSet<>();
			ArrayList<Moveset> actions;
			Node current = new Node(initialStateItemsData, null, null);
			Object[] succ;
			frontier.add(current);
			while (!frontier.isEmpty()) {
				current = frontier.poll();
				if (isEnd(current.getState()))
					return current;
				explored.add(current.getState());
				actions = Actions(current.getState());
				for (Moveset m : actions) {
					succ = Succ((GameState)current.getState(), m);
					if ((GameState)succ[0] != null && !explored.contains((GameState)succ[0]))
						frontier.add(new Node((GameState)succ[0], (Moveset)succ[1], current));
				}
			}
			return null;
		}
		
		private SokoBotSequence() {}

        private GameState isDeadlock(GameState state) {
            return state.isAnyBoxCornered(mapData) ? state : null;
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
        public ArrayList<Moveset> Actions(GameState state) {
            ArrayList <Moveset> possibleMoves = new ArrayList<>();
            int[] rowInnerVicinity = {-1, 1, 0, 0};
            int[] colInnerVicinity = {0, 0, -1, 1};
            int[] rowOppositeVicinity = {1, -1, 0, 0};
            int[] colOppositeVicinity = {0, 0, 1, -1};
            BFS_Solver bfs_solver;
            Position originalplayerPos = state.getPlayerPos();

            for (Map.Entry<Position, Character> entry : state.getItemsPos().entrySet()) {
                if (entry.getValue().equals('$')) {
                    for (int i = 0; i < 4; i++) {
                        Position boxPos = new Position(entry.getKey().getRow(), entry.getKey().getCol());
                        Position playerPos = new Position(entry.getKey().getRow() + rowInnerVicinity[i], entry.getKey().getCol() + colInnerVicinity[i]);
                        Position targetPushingPos = new Position(entry.getKey().getRow() + rowOppositeVicinity[i],
                                entry.getKey().getCol() + colOppositeVicinity[i]);
                        // Check if the player position is not a wall and not occupied by another box
                        // Also check if the target pushing position is not a wall and not occupied by another box
                        if (!mapData[playerPos.getRow()][playerPos.getCol()].equals('#') && !state.getItemsPos().getOrDefault(playerPos, ' ').equals('$') &&
                                !mapData[targetPushingPos.getRow()][targetPushingPos.getCol()].equals('#') && !state.getItemsPos().getOrDefault(targetPushingPos, ' ').equals('$')) {
                            // Check if the player can reach the position to push the box
                            bfs_solver = new BFS_Solver(mapData, state.getItemsPos());
                            String path = bfs_solver.solve(originalplayerPos, playerPos);
                            if (path != null) {
                                // Create a new Moveset for this action and add it to the list
                                possibleMoves.add(new Moveset(boxPos, playerPos, path));
                            }
                        }
                    }
                }

            }
            return possibleMoves;
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
			StringBuilder move_sequence = new StringBuilder(m.getMoveSequence());
			// Declarations for process
			Position innerPos = null;
			Position outerPos = null;
			Position newPos = null;
			Position newBoxPos = null;
			Object[] playerVicinityData = null;
			boolean succFound = false;
			int innerIndex = 0;
			// Verify state by checking if player position in the given Moveset is not pointing to a wall
			if ((gnew.getItem(oldPos.getRow(), oldPos.getCol()).equals(' ') || gnew.getItem(oldPos.getRow(), oldPos.getCol()).equals('@'))
					&& !mapData[oldPos.getRow()][oldPos.getCol()].equals('#')) {
				// Move player on copy state
				gnew.removeItem(statePos.getRow(), statePos.getCol());
				gnew.setItem(oldPos.getRow(), oldPos.getCol(), '@');
				// Verify state by checking if box position in the given Moveset is indeed a box
				if (gnew.getItem(oldBoxPos.getRow(), oldBoxPos.getCol()).equals('$')) {
					playerVicinityData = gnew.getPlayerVicinityData();
					// Test each direction and push wherever oldBoxPos matches
					do {
						innerPos = (Position)playerVicinityData[innerIndex];
						outerPos = (Position)playerVicinityData[innerIndex + 2];
						if (innerPos.equals(oldBoxPos) && gnew.getItem(outerPos.getRow(), outerPos.getCol()).equals(' ')) {
							if (!mapData[outerPos.getRow()][outerPos.getCol()].equals('#')) {
								gnew.setItem(outerPos.getRow(), outerPos.getCol(), '$');
								gnew.setItem(innerPos.getRow(), innerPos.getCol(), '@');
								gnew.removeItem(oldPos.getRow(), oldPos.getCol());
								newPos = new Position(innerPos.getRow(), innerPos.getCol());
								newBoxPos = new Position(outerPos.getRow(), outerPos.getCol());
								switch (innerIndex) {
									case 0:
										move_sequence.append('u');
										break;
									case 4:
										move_sequence.append('d');
										break;
									case 8:
										move_sequence.append('l');
										break;
									case 12:
										move_sequence.append('r');
										break;
                                    default:
                                        throw new RuntimeException("Succ(g,m) on state#" + g.hashCode() + ": invalid innerIndex, got " + innerIndex);
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
                            gnew.isAnyBoxCornered(mapData) ? gnew : null,
							new Moveset(newBoxPos, newPos, move_sequence.toString())
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
					" col " + oldPos.getCol() + " is not vacant, found " + g.getItem(oldPos.getRow(), oldPos.getCol()));
		}
		
        // may need to be a local variable of the getSolutionAStar method alongside the frontier priority queue
		private ArrayList<Position> goalTiles = new ArrayList<>();
		private Character[][] mapData;
		private GameState initialStateItemsData;
	}
}
