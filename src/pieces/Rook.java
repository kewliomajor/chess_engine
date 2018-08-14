package pieces;

import board.BoardState;

import java.util.List;

public class Rook extends AbstractPiece {

    public Rook(Color color, int position){
        super(color, position);
        this.baseValue = 5;
    }

    public Rook(Rook rook){
        super(rook.getColor(), rook.getPosition());
        baseValue = rook.baseValue;
        hasMoved = rook.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        return getHorizontalVerticalMoves(boardState);
    }
}
