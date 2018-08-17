package pieces;

import board.BoardState;

import java.util.List;

public class Bishop extends AbstractPiece {

    private static final double BISHOP_BASE_VALUE = 3;

    public Bishop(Color color, int position){
        super(color, position);
        this.baseValue = BISHOP_BASE_VALUE;
    }

    public Bishop(Bishop bishop){
        super(bishop.getColor(), bishop.getPosition());
        baseValue = BISHOP_BASE_VALUE;
        hasMoved = bishop.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        List<Move> moves = getDiagonalMoves(boardState);
        this.baseValue = BISHOP_BASE_VALUE + (moves.size() * AVAILABLE_MOVE_SCORE);
        return moves;
    }
}
