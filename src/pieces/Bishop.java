package pieces;

import board.BoardState;

import java.util.List;

public class Bishop extends AbstractPiece {

    public Bishop(Color color, int position){
        super(color, position);
        this.baseValue = 3;
    }

    public Bishop(Bishop bishop){
        super(bishop.getColor(), bishop.getPosition());
        baseValue = bishop.baseValue;
        hasMoved = bishop.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        return getDiagonalMoves(boardState);
    }
}
