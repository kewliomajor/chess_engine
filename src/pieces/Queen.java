package pieces;

import board.BoardState;

import java.util.List;

public class Queen extends AbstractPiece {

    public Queen(Color color, int position){
        super(color, position);
        this.baseValue = 9;
    }

    public Queen(Queen queen){
        super(queen.getColor(), queen.getPosition());
        baseValue = queen.baseValue;
        hasMoved = queen.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        List<Move> moves = getHorizontalVerticalMoves(boardState);
        moves.addAll(getDiagonalMoves(boardState));
        return moves;
    }
}
