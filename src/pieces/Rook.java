package pieces;

import board.BoardState;

import java.util.List;

public class Rook extends AbstractPiece {

    private static double ROOK_BASE_VALUE = 5;

    public Rook(Color color, int position){
        super(color, position);
        this.baseValue = ROOK_BASE_VALUE;
    }

    public Rook(Rook rook){
        super(rook.getColor(), rook.getPosition());
        baseValue = ROOK_BASE_VALUE;
        hasMoved = rook.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        List<Move> moves = getHorizontalVerticalMoves(boardState);
        this.baseValue = ROOK_BASE_VALUE + (moves.size() * AVAILABLE_MOVE_SCORE);
        return moves;
    }
}
