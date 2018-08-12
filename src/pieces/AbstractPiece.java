package pieces;

import java.util.List;

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

	public abstract List<Move> getMoves();
	
	public void makeMove(Move move){
		this.position = move.getEndPosition();
	}
	
	protected boolean isMoveLegal(Move move){
		return Utils.isPositionLegal(move.getEndPosition());
	}
}
