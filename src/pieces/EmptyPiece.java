package pieces;

import java.util.List;

public class EmptyPiece extends AbstractPiece{
	
	private static EmptyPiece emptyPiece = new EmptyPiece(Color.WHITE, 0);

	private EmptyPiece(Color color, int position) {
		super(color, position);
	}
	
	public static EmptyPiece getInstance(){
		return emptyPiece;
	}

	@Override
	public List<Move> getMoves() {
		return null;
	}
	
	@Override
	public boolean isEmpty(){
		return true;
	}
}
