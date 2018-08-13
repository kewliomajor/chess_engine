package pieces;

import board.BoardState;

import java.util.ArrayList;
import java.util.List;

public class EmptyPiece extends AbstractPiece{

	public EmptyPiece(int position) {
		super(Color.NONE, position);
	}

	@Override
	public List<Move> getMoves(BoardState boardState) {
		return new ArrayList<>();
	}
	
	@Override
	public boolean isEmpty(){
		return true;
	}
}
