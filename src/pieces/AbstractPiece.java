package pieces;

import java.util.List;

import board.Utils;

public abstract class AbstractPiece {
	
	private final Color color;
	private int position;
	
	
	public AbstractPiece(Color color, int position){
		this.color = color;
		this.position = position;
	}
	
	public Color getColor(){
		return color;
	}
	
	public int getPosition(){
		return position;
	}

	public abstract List<Move> getMoves();
	
	public void makeMove(Move move){
		this.position = move.getEndPosition();
	}
	
	protected boolean isMoveLegal(Move move){
		return Utils.isPositionLegal(move.getEndPosition());
	}

}
