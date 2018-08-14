package board;

import pieces.*;

import java.util.ArrayList;
import java.util.List;

public class BoardState {

	public static int BOARD_SIZE = 120;

	private AbstractPiece[] board;
	private AbstractPiece blackKing;
	private AbstractPiece whiteKing;
	private Color currentMove = Color.WHITE;
	
	/**
	 * Creates a board with all pieces in the starting formation
	 */
	public BoardState(){
		board = new AbstractPiece[BOARD_SIZE];
		setupStartingPieces();
	}


	public BoardState(BoardState boardState){
		//TODO copy the board state
		currentMove = boardState.getCurrentMoveColor();
		board = new AbstractPiece[BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; i++){
			AbstractPiece existingPiece = boardState.getBoard()[i];
			if (existingPiece instanceof InvalidPiece){
				board[i] = InvalidPiece.getInstance();
			}
			else if (existingPiece instanceof EmptyPiece){
				board[i] = new EmptyPiece(i);
			}
			else if (existingPiece instanceof Pawn){
				board[i] = new Pawn((Pawn)existingPiece);
			}
			else if (existingPiece instanceof Rook){
				board[i] = new Rook((Rook)existingPiece);
			}
			else if (existingPiece instanceof Knight){
				board[i] = new Knight((Knight)existingPiece);
			}
			else if (existingPiece instanceof Bishop){
				board[i] = new Bishop((Bishop)existingPiece);
			}
			else if (existingPiece instanceof Queen){
				board[i] = new Queen((Queen)existingPiece);
			}
			else if (existingPiece instanceof King){
				King king = new King((King)existingPiece);
				if (existingPiece.getColor() == Color.BLACK){
					blackKing = king;
				}
				else if (existingPiece.getColor() == Color.WHITE){
					whiteKing = king;
				}
				else{
					throw new RuntimeException("King has an impossible color: " + existingPiece.getColor());
				}
				board[i] = king;
			}
		}
	}
	
	
	public AbstractPiece[] getBoard(){
		return board;
	}


	public AbstractPiece getPiece(int i, int j){
		int row = (10 * i) + 20;
		j+= 1;
		return board[row + j];
	}


	public Color getCurrentMoveColor(){
		return currentMove;
	}


	public double getBoardScore(){
		double boardScore = 0;
		for (int i = 0; i < BOARD_SIZE; i++){
			if (board[i] instanceof InvalidPiece || board[i] instanceof EmptyPiece){
				continue;
			}
			int offset = 1;
			if (board[i].getColor() != currentMove){
				offset = -1;
			}
			int score = board[i].getBaseValue() * offset;
			boardScore += score;
		}
		return boardScore;
	}


	/**
	 * Excepts that the move is valid, must check isMoveValid before calling
	 *
	 * @param move
	 */
	public void makeMove(Move move){
		board[move.getEndPosition()] = board[move.getStartPosition()];
		board[move.getStartPosition()] = new EmptyPiece(move.getStartPosition());
		board[move.getEndPosition()].move(move);
		currentMove = Color.getOpposite(currentMove);
	}

	public void makeMove(Move move, boolean swapMove){
		board[move.getEndPosition()] = board[move.getStartPosition()];
		board[move.getStartPosition()] = new EmptyPiece(move.getStartPosition());
		board[move.getEndPosition()].move(move);
		if (swapMove){
			currentMove = Color.getOpposite(currentMove);
		}
	}


	public List<Move> getAllValidMoves(Color color){
		List<Move> validMoves = new ArrayList<>();
		for (int i = 21; i < 99; i++){
			if (board[i].getColor() != color){
				continue;
			}
			List<Move> moves = board[i].getMoves(this);
			for (Move move : moves){
				if (isMoveValid(move)){
					validMoves.add(move);
				}
			}
		}
		return validMoves;
	}


	public boolean isMoveValid(Move move){
		AbstractPiece fromPiece = board[move.getStartPosition()];
		AbstractPiece toPiece = board[move.getEndPosition()];
		if (toPiece instanceof InvalidPiece){
			return false;
		}
		else if (fromPiece.getColor() == toPiece.getColor()){
			return false;
		}
		//now the actual logic for individual pieces
		else if (fromPiece instanceof Pawn){
			if (!isPawnMoveValid((Pawn)fromPiece, toPiece)){
				return false;
			}
		}
		else if (fromPiece instanceof Rook){
			if (!isBasicMoveValid(fromPiece, toPiece)){
				return false;
			}
		}
		else if (fromPiece instanceof Knight){
			if (!isBasicMoveValid(fromPiece, toPiece)){
				return false;
			}
		}
		else if (fromPiece instanceof Bishop){
			if (!isBasicMoveValid(fromPiece, toPiece)){
				return false;
			}
		}
		else if (fromPiece instanceof Queen){
			if (!isBasicMoveValid(fromPiece, toPiece)){
				return false;
			}
		}
		else if (fromPiece instanceof King){
			if (!isKingMoveValid((King)fromPiece, toPiece)){
				return false;
			}
		}
		else{
			throw new RuntimeException("Piece is not any known type: " + fromPiece.getClass());
		}

		BoardState afterMoveBoard = new BoardState(this);
		afterMoveBoard.makeMove(move);
		return !afterMoveBoard.kingInCheck(fromPiece.getColor());
	}


	public boolean kingInCheck(Color color){
		AbstractPiece targetKing;
		if (color == Color.WHITE){
			targetKing = whiteKing;
		}
		else if (color == Color.BLACK){
			targetKing = blackKing;
		}
		else{
			throw new RuntimeException("Trying to determine if King is in check but given bad king color: " + color);
		}

		if (pawnChecks(targetKing)){
			return true;
		}
		if (knightChecks(targetKing)){
			return true;
		}
		if (kingChecks(targetKing)){
			return true;
		}
		if (horizontalVerticalChecks(targetKing)){
			return true;
		}
		if (diagonalChecks(targetKing)){
			return true;
		}

		return false;
	}


	private boolean pawnChecks(AbstractPiece targetKing){
		int offset = 1;
		if (targetKing.getColor() == Color.WHITE){
			offset = -1;
		}
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		AbstractPiece potentialPawn = board[targetKing.getPosition()+11*offset];
		if (potentialPawn instanceof Pawn && potentialPawn.getColor() == oppositeColor){
			return true;
		}
		potentialPawn = board[targetKing.getPosition()+9*offset];
		if (potentialPawn instanceof Pawn && potentialPawn.getColor() == oppositeColor){
			return true;
		}
		return false;
	}

	private boolean knightChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		AbstractPiece potentialKnight = board[targetKing.getPosition()+21];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}
		potentialKnight = board[targetKing.getPosition()+19];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}
		potentialKnight = board[targetKing.getPosition()+12];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}
		potentialKnight = board[targetKing.getPosition()+8];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}
		potentialKnight = board[targetKing.getPosition()-8];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}
		potentialKnight = board[targetKing.getPosition()-12];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}
		potentialKnight = board[targetKing.getPosition()+19];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}
		potentialKnight = board[targetKing.getPosition()-21];
		if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
			return true;
		}

		return false;
	}

	private boolean kingChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		AbstractPiece potentialKing = board[targetKing.getPosition()+9];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}
		potentialKing = board[targetKing.getPosition()+10];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}
		potentialKing = board[targetKing.getPosition()+11];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}
		potentialKing = board[targetKing.getPosition()+1];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}
		potentialKing = board[targetKing.getPosition()-1];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}
		potentialKing = board[targetKing.getPosition()-9];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}
		potentialKing = board[targetKing.getPosition()-10];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}
		potentialKing = board[targetKing.getPosition()-11];
		if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
			return true;
		}

		return false;
	}

	private boolean horizontalVerticalChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		for (int i = targetKing.getPosition()+10; i < BOARD_SIZE; i+= 10){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-10; i > 0; i-= 10){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()+1; i < targetKing.getPosition()+8; i++){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-1; i > targetKing.getPosition()-8; i--){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		return false;
	}

	private boolean diagonalChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		for (int i = targetKing.getPosition()+11; i < BOARD_SIZE; i+= 11){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()+9; i < BOARD_SIZE; i+= 9){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-11; i > 0; i-= 11){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-9; i > 0; i-= 9){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		return false;
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


	private boolean isBasicMoveValid(AbstractPiece fromPiece, AbstractPiece toPiece){
		if (toPiece instanceof EmptyPiece || fromPiece.getColor() == Color.getOpposite(toPiece.getColor())){
			return true;
		}
		return false;
	}


	private boolean isPawnMoveValid(Pawn fromPiece, AbstractPiece toPiece){
		int offset = 1;
		if (fromPiece.getColor() == Color.WHITE){
			offset = -1;
		}
		//forward one
		if (fromPiece.getPosition() + 10 * offset == toPiece.getPosition()){
			return toPiece instanceof EmptyPiece;
		}
		//forward two
		else if (fromPiece.getPosition() + 20 * offset == toPiece.getPosition()){
			if (board[fromPiece.getPosition() + 10 * offset] instanceof EmptyPiece){
				return toPiece instanceof EmptyPiece && !fromPiece.hasMoved();
			}
			return false;
		}
		//capturing
		else{
			if (fromPiece.getColor() == Color.getOpposite(toPiece.getColor())){
				System.out.println("move is valid " + fromPiece.getPosition() + " to " + toPiece.getPosition());
				return true;
			}
			//rest is en passant rules
			else if (fromPiece.getPosition() + 9 * offset == toPiece.getPosition()){
				AbstractPiece potentialPawn = board[fromPiece.getPosition()-1];
				if (potentialPawn instanceof Pawn){
					return ((Pawn) potentialPawn).isDoubleMoving() && fromPiece.getColor() == Color.getOpposite(toPiece.getColor());
				}
			}
			else if (fromPiece.getPosition() + 11 * offset == toPiece.getPosition()){
				AbstractPiece potentialPawn = board[fromPiece.getPosition()+1];
				if (potentialPawn instanceof Pawn){
					return ((Pawn) potentialPawn).isDoubleMoving() && fromPiece.getColor() == Color.getOpposite(toPiece.getColor());
				}
			}
		}
		return false;
	}


	private boolean isKingMoveValid(King fromPiece, AbstractPiece toPiece){
		//case for castling
		Move move;
		BoardState afterMoveBoard;
		if (fromPiece.getPosition()+2 == toPiece.getPosition()){
			afterMoveBoard = new BoardState(this);
			move = new Move(fromPiece.getPosition(), fromPiece.getPosition()+1);
			afterMoveBoard.makeMove(move);
			if (afterMoveBoard.kingInCheck(fromPiece.getColor())){
				return false;
			}
		}
		else if (fromPiece.getPosition()-2 == toPiece.getPosition()){
			afterMoveBoard = new BoardState(this);
			move = new Move(fromPiece.getPosition(), fromPiece.getPosition()-1);
			afterMoveBoard.makeMove(move);
			if (afterMoveBoard.kingInCheck(fromPiece.getColor())){
				return false;
			}
		}
		if (toPiece instanceof EmptyPiece || fromPiece.getColor() == Color.getOpposite(toPiece.getColor())){
			return true;
		}
		return false;
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
				blackKing = new King(Color.BLACK, position);
				return blackKing;
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
				whiteKing = new King(Color.WHITE, position);
				return whiteKing;
			case 96:
				return new Bishop(Color.WHITE, position);
			case 97:
				return new Knight(Color.WHITE, position);
			case 98:
				return new Rook(Color.WHITE, position);
			default:
				return new EmptyPiece(position);
		}
	}
}
