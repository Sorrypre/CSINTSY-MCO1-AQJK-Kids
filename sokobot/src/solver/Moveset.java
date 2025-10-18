public class Moveset
{
	public Moveset(Position boxPos, Position playerPos, ArrayList<Character> move_sequence)
	{
		this.boxPos = boxPos;
		this.playerPos = playerPos;
		this.move_sequence = move_sequence;
	}
	
	public Position getBoxPos() { return boxPos; }
	
	public Position getMSPlayerPos() { return playerPos; }
	
	public ArrayList<Character> getMoveSequence() { return move_sequence; }
	
	private Moveset() {}
	
	private Position boxPos, playerPos;
	private ArrayList<Character> move_sequence;
}
