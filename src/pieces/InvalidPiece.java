package pieces;

import java.util.List;

public class InvalidPiece extends AbstractPiece{
	
	private static InvalidPiece invalidPiece = new InvalidPiece(Color.BLACK, 0);

	private InvalidPiece(Color color, int position) {
		super(color, position);
	}
	
	public static InvalidPiece getInstance(){
		return invalidPiece;
	}

	@Override
	public List<Move> getMoves() {
		return null;
	}
	
	@Override
	public boolean isValid(){
		return false;
	}
	
}
