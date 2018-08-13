package pieces;

import board.BoardState;

import java.util.ArrayList;
import java.util.List;

public class InvalidPiece extends AbstractPiece{
	
	private static InvalidPiece invalidPiece = new InvalidPiece(Color.NONE, 0);

	private InvalidPiece(Color color, int position) {
		super(color, position);
	}
	
	public static InvalidPiece getInstance(){
		return invalidPiece;
	}

	@Override
	public List<Move> getMoves(BoardState boardState) {
		return new ArrayList<>();
	}
	
	@Override
	public boolean isValid(){
		return false;
	}
	
}
