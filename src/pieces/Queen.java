package pieces;

import board.BoardState;

import java.util.List;

public class Queen extends AbstractPiece {

    private static double QUEEN_BASE_VALUE = 9;

    public Queen(Color color, int position){
        super(color, position);
        this.baseValue = QUEEN_BASE_VALUE;
    }

    public Queen(Queen queen){
        super(queen.getColor(), queen.getPosition());
        baseValue = QUEEN_BASE_VALUE;
        hasMoved = queen.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        List<Move> moves = getHorizontalVerticalMoves(boardState);
        moves.addAll(getDiagonalMoves(boardState));
        this.baseValue = QUEEN_BASE_VALUE + (moves.size() * AVAILABLE_MOVE_SCORE);
        return moves;
    }
}
