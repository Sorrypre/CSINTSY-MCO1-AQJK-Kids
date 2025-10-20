package solver;

public class Moveset
{
	public Moveset(Position boxPos, Position playerPos, String move_sequence)
	{
		this.boxPos = boxPos;
		this.playerPos = playerPos;
		this.move_sequence = move_sequence;
	}
	
	public Position getBoxPos() { return boxPos; }
	
	public Position getMSPlayerPos() { return playerPos; }
	
	public String getMoveSequence() { return move_sequence; }
	
	private Moveset() {}
	
	private Position boxPos, playerPos;
	private String move_sequence;
}
