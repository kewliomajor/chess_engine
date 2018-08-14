package pieces;

import java.util.ArrayList;
import java.util.List;

import board.BoardState;
import board.Utils;

public abstract class AbstractPiece {
	
	protected final Color color;
	protected int position;
	protected int baseValue = 0;
	protected boolean hasMoved = false;
	
	
	public AbstractPiece(Color color, int position){
		this.color = color;
		this.position = position;
	}

	public void move(Move move){
		hasMoved = true;
		position = move.getEndPosition();
	}
	
	public Color getColor(){
		return color;
	}
	
	public int getPosition(){
		return position;
	}

	public int getBaseValue() {
		return baseValue;
	}
	
	public boolean isValid(){
		return true;
	}
	
	public boolean isEmpty(){
		return false;
	}

	public boolean hasMoved(){
		return hasMoved;
	}

	public void moved(){
		hasMoved = true;
	}

	public abstract List<Move> getMoves(BoardState boardState);

	public List<Move> getAllValidMoves(BoardState boardState){
		List<Move> validMoves = new ArrayList<>();
		List<Move> moves = getMoves(boardState);
		for (Move move : moves){
			if (boardState.isMoveValid(move)){
				validMoves.add(move);
			}
		}
		return validMoves;
	}

	protected List<Move> getHorizontalVerticalMoves(BoardState boardState){
		List<Move> moves = new ArrayList<>();
		moves.addAll(addMovesGreaterThan(boardState, position+10, BoardState.BOARD_SIZE, 10));
		moves.addAll(addMovesLessThan(boardState, position-10, 0, 10));
		moves.addAll(addMovesGreaterThan(boardState, position+1, position+8, 1));
		moves.addAll(addMovesLessThan(boardState, position-1, position-8, 1));
		return moves;
	}

	protected List<Move> getDiagonalMoves(BoardState boardState){
		List<Move> moves = new ArrayList<>();
		moves.addAll(addMovesGreaterThan(boardState, position+11, BoardState.BOARD_SIZE, 11));
		moves.addAll(addMovesGreaterThan(boardState, position+9, BoardState.BOARD_SIZE, 9));
		moves.addAll(addMovesLessThan(boardState, position-11, 0, 11));
		moves.addAll(addMovesLessThan(boardState, position-9, 0, 9));
		return moves;
	}

	private List<Move> addMovesGreaterThan(BoardState boardState, int start, int limit, int increment){
		List<Move> moves = new ArrayList<>();
		for (int i = start; i < limit; i+=increment){
			AbstractPiece piece = boardState.getBoard()[i];
			if (piece instanceof InvalidPiece || piece.getColor() == color){
				break;
			}
			else if (Color.getOpposite(piece.getColor()) == color){
				moves.add(new Move(position, i));
				break;
			}
			moves.add(new Move(position, i));
		}
		return moves;
	}

	private List<Move> addMovesLessThan(BoardState boardState, int start, int limit, int increment){
		List<Move> moves = new ArrayList<>();
		for (int i = start; i > limit; i-=increment){
			AbstractPiece piece = boardState.getBoard()[i];
			if (piece instanceof InvalidPiece || piece.getColor() == color){
				break;
			}
			else if (Color.getOpposite(piece.getColor()) == color){
				moves.add(new Move(position, i));
				break;
			}
			moves.add(new Move(position, i));
		}
		return moves;
	}
	
	public void makeMove(Move move){
		this.position = move.getEndPosition();
	}
	
	protected boolean isMoveLegal(Move move){
		return Utils.isPositionLegal(move.getEndPosition());
	}
}
