package pieces;

import board.BoardState;

import java.util.List;

public class Queen extends AbstractPiece {

    public Queen(Color color, int position){
        super(color, position);
        this.baseValue = 9;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        List<Move> moves = getHorizontalVerticalMoves(boardState);
        moves.addAll(getDiagonalMoves(boardState));
        return moves;
    }
}
