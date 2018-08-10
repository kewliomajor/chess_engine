package board;

import pieces.*;

public class BoardState {

	private static int BOARD_SIZE = 120;

	private AbstractPiece[] board;
	
	/**
	 * Creates a board with all pieces in the starting formation
	 */
	public BoardState(){
		board = new AbstractPiece[BOARD_SIZE];
		setupStartingPieces();
	}
	
	
	public AbstractPiece[] getBoard(){
		return board;
	}


	public String toString(){
		String boardString = "|";

		for (int i = 0; i < BOARD_SIZE; i++){
			boardString += getPieceString(board[i]) + "|";
			int digit = i % 10;
			if (digit == 9 && i != BOARD_SIZE-1){
				boardString += "\n|";
			}
		}

		return boardString;
	}


	private String getPieceString(AbstractPiece piece){
		String pieceString = "";

		if (piece == null){
			pieceString = " N ";
		}
		else{
			if (piece instanceof EmptyPiece){
				pieceString = " E ";
			}
			else if (piece instanceof InvalidPiece){
				pieceString = " I ";
			}
			else if (piece instanceof Pawn){
				pieceString = " P ";
			}
			else if (piece instanceof Rook){
				pieceString = " R ";
			}
			else if (piece instanceof Knight){
				pieceString = " N ";
			}
			else if (piece instanceof Bishop){
				pieceString = " B ";
			}
			else if (piece instanceof Queen){
				pieceString = " Q ";
			}
			else if (piece instanceof King){
				pieceString = " K ";
			}
		}

		return pieceString;
	}
	
	
	private void setupStartingPieces(){
		for (int i = 0; i < BOARD_SIZE; i++){
			int digit = i % 10;
			if (i > 20 && i < 99 && digit != 0 && digit != 9){
				board[i] = getPieceForPosition(i);
			}
			else{
				board[i] = InvalidPiece.getInstance();				
			}
		}
	}

	private AbstractPiece getPieceForPosition(int position){
		switch (position){
			case 21:
				return new Rook(Color.BLACK, position);
			case 22:
				return new Knight(Color.BLACK, position);
			case 23:
				return new Bishop(Color.BLACK, position);
			case 24:
				return new Queen(Color.BLACK, position);
			case 25:
				return new King(Color.BLACK, position);
			case 26:
				return new Bishop(Color.BLACK, position);
			case 27:
				return new Knight(Color.BLACK, position);
			case 28:
				return new Rook(Color.BLACK, position);
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
				return new Pawn(Color.BLACK, position);
			case 81:
			case 82:
			case 83:
			case 84:
			case 85:
			case 86:
			case 87:
			case 88:
				return new Pawn(Color.WHITE, position);
			case 91:
				return new Rook(Color.WHITE, position);
			case 92:
				return new Knight(Color.WHITE, position);
			case 93:
				return new Bishop(Color.WHITE, position);
			case 94:
				return new Queen(Color.WHITE, position);
			case 95:
				return new King(Color.WHITE, position);
			case 96:
				return new Bishop(Color.WHITE, position);
			case 97:
				return new Knight(Color.WHITE, position);
			case 98:
				return new Rook(Color.WHITE, position);
			default:
				return EmptyPiece.getInstance();
		}
	}
}
