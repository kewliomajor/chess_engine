package pieces;

public class Move {

	private int startPosition;
	private int endPosition;
	
	public Move(int startPosition, int endPosition){
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	public int getStartPosition(){
		return startPosition;
	}
	
	public int getEndPosition(){
		return endPosition;
	}

	@Override
	public boolean equals(Object object){
		if (!(object instanceof Move)){
			return false;
		}
		Move move = (Move)object;
		return move.getStartPosition() == startPosition && move.getEndPosition() == endPosition;
	}
}
