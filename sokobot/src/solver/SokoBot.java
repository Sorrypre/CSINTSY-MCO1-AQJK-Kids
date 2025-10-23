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
		
		@Override
		public String toString() {
			return compute();
		}
		
		private String compute() {
			StringBuilder result = new StringBuilder();
			Node leaf = search();
			System.out.println("Constructing path...");
			Node i;
			if (leaf != null) {
				i = leaf;
				while (i != null) {
					result.insert(0, i.getMSSequence());
					i = i.getParent();
				}
			}
			System.out.println(result);
			return result.toString();
		}
		
		private Node search() {
			PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(Node::getFScore));
			HashSet<GameState> explored = new HashSet<>();
			Node current = new Node(initialStateItemsData, null, null);
			ArrayList<Moveset> actions;
			Object[] succ;
			frontier.add(current);
			while (!frontier.isEmpty()) {
				current = frontier.poll();
				if (isEnd(current.getState()))
					break;
				explored.add(current.getState());
				actions = Actions(current.getState());
				for (Moveset m : actions) {
					succ = Succ((GameState)current.getState(), m);
					if ((GameState)succ[0] != null && !explored.contains((GameState)succ[0]))
						frontier.add(new Node((GameState)succ[0], (Moveset)succ[1], current));
				}
			}
			return current;
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

        // java
        @Override
        public ArrayList<Moveset> Actions(GameState state) {
            ArrayList<Moveset> possibleMoves = new ArrayList<>();
            int height = mapData.length;
            int width = mapData[0].length;
            int[] dr = {-1, 1, 0, 0};
            int[] dc = {0, 0, -1, 1};

            // Box position in 2D array for quick lookup
            boolean[][] isBoxInPos = new boolean[height][width];
            for (Map.Entry<Position, Character> e : state.getItemsPos().entrySet()) {
                if (e.getValue().equals('$')) {
                    Position p = e.getKey();
                    isBoxInPos[p.getRow()][p.getCol()] = true;
                }
            }

            // BFS to find all reachable positions for the player
            Position start = state.getPlayerPos();
            boolean[][] reachablePos = new boolean[height][width];
            Position[][] parentPos = new Position[height][width];
            ArrayDeque<Position> queue = new ArrayDeque<>();
            queue.add(start);
            reachablePos[start.getRow()][start.getCol()] = true;
            parentPos[start.getRow()][start.getCol()] = null;

            while (!queue.isEmpty()) {
                Position curr = queue.poll();
                int r = curr.getRow(), c = curr.getCol();
                for (int i = 0; i < 4; i++) {
                    int newRow = r + dr[i], newCol = c + dc[i];
                    if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width) continue;
                    if (!reachablePos[newRow][newCol] && !mapData[newRow][newCol].equals('#') && !isBoxInPos[newRow][newCol]) {
                        reachablePos[newRow][newCol] = true;
                        parentPos[newRow][newCol] = curr;
                        queue.add(new Position(newRow, newCol));
                    }
                }
            }

            // For each box, check pushes: ensure indices are in bounds before accessing arrays
            for (Map.Entry<Position, Character> e : state.getItemsPos().entrySet()) {
                if (!e.getValue().equals('$')) continue;
                Position box = e.getKey();
                for (int i = 0; i < 4; i++) {
                    int playerRow = box.getRow() - dr[i];
                    int playerCol = box.getCol() - dc[i];
                    int targetRow = box.getRow() + dr[i];
                    int targetCol = box.getCol() + dc[i];

                    // bounds checks
                    if (playerRow < 0 || playerRow >= height || playerCol < 0 || playerCol >= width) continue;
                    if (targetRow < 0 || targetRow >= height || targetCol < 0 || targetCol >= width) continue;

                    // target must be free and not a wall/box
                    if (reachablePos[playerRow][playerCol] &&  !isBoxInPos[targetRow][targetCol] && !mapData[targetRow][targetCol].equals('#')) {
                        // reconstruct path from start -> (playerRow,playerCol)
                        StringBuilder sb = new StringBuilder();
                        Position current = new Position(playerRow, playerCol);
                        Position player = parentPos[current.getRow()][current.getCol()];
                        // walk back using parent pointers, append moves from parent -> cur
                        while (player != null) {
                            player = parentPos[current.getRow()][current.getCol()];
                            if (player == null) break;
                            int drc = current.getRow() - player.getRow();
                            int dcc = current.getCol() - player.getCol();
                            char move;
                            if (drc == -1 && dcc == 0) move = 'u';
                            else if (drc == 1 && dcc == 0) move = 'd';
                            else if (drc == 0 && dcc == -1) move = 'l';
                            else if (drc == 0 && dcc == 1) move = 'r';
                            else throw new RuntimeException("Invalid parent linkage in BFS");
                            sb.append(move);
                            current = player;
                        }
                        sb.reverse(); // now path is start -> playerPos
                        possibleMoves.add(new Moveset(new Position(box.getRow(), box.getCol()),
                                new Position(playerRow, playerCol),
                                sb.toString()));
                    }
                }
            }
            return possibleMoves;
        }

		// Return value contains:a
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
                        return new Object[] {
							gnew.isAnyBoxCornered(mapData) ? null : gnew,
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
		private HashSet<Position> goalTiles = new HashSet<>();
		private Character[][] mapData;
		private GameState initialStateItemsData;
	}
}
