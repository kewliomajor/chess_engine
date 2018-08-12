package pieces;

import java.util.List;

public class EmptyPiece extends AbstractPiece{

	public EmptyPiece(int position) {
		super(Color.WHITE, position);
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
