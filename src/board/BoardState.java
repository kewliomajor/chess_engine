package board;

import pieces.AbstractPiece;
import pieces.EmptyPiece;
import pieces.InvalidPiece;

public class BoardState {

	private AbstractPiece[] board;
	
	/**
	 * Creates a board with all pieces in the starting formation
	 */
	public BoardState(){
		board = new AbstractPiece[120];
		setupStartingPieces();
	}
	
	
	public AbstractPiece[] getBoard(){
		return board;
	}
	
	
	private void setupStartingPieces(){
		for (int i = 0; i < 120; i++){
			int digit = i % 10;
			if (i > 40 && i < 79 && digit != 0 && digit != 9){
				board[i] = EmptyPiece.getInstance();
			}
			else{
				board[i] = InvalidPiece.getInstance();				
			}
		}
	}
}
