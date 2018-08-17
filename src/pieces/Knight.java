package pieces;

import board.BoardState;

import java.util.ArrayList;
import java.util.List;

public class Knight extends AbstractPiece {

    private static final double KNIGHT_BASE_VALUE = 3;

    public Knight(Color color, int position){
        super(color, position);
        this.baseValue = KNIGHT_BASE_VALUE;
    }

    public Knight(Knight knight){
        super(knight.getColor(), knight.getPosition());
        baseValue = KNIGHT_BASE_VALUE;
        hasMoved = knight.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        int[] possibleMoves = new int[]{21, 19, 12, 8, -8, -12, -19, -21};
        List<Move> moves = new ArrayList<>();
        AbstractPiece[] board = boardState.getBoard();
        for (int possibleMove : possibleMoves){
            if (knightMoveValid(board[position], board[position+possibleMove])){
                moves.add(new Move(position, position + possibleMove));
            }
        }
        this.baseValue = KNIGHT_BASE_VALUE + (moves.size() * AVAILABLE_MOVE_SCORE);
        return moves;
    }

    private boolean knightMoveValid(AbstractPiece fromPiece, AbstractPiece toPiece){
        if (toPiece instanceof InvalidPiece){
            return false;
        }
        if (toPiece instanceof EmptyPiece || fromPiece.getColor() == Color.getOpposite(toPiece.getColor())){
            return true;
        }
        return false;
    }
}
