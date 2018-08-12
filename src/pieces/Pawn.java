package pieces;

import board.BoardState;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends AbstractPiece {

    private boolean doubleMove = false;

    public Pawn(Color color, int position){
        super(color, position);
        this.baseValue = 1;
    }

    @Override
    public void move(Move move){
        doubleMove = false;
        int startPosition = move.getStartPosition();
        int endPosition = move.getEndPosition();
        if (startPosition + 20 == endPosition || startPosition - 20 == endPosition){
            doubleMove = true;
        }
        super.move(move);
    }

    public boolean isDoubleMoving(){
        return doubleMove;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        int offset = 1;
        if (color == Color.WHITE){
            offset = -1;
        }
        List<Move> moves = new ArrayList<>();
        moves.add(new Move(position, position + 10 * offset));
        if (!hasMoved){
            moves.add(new Move(position, position + 20 * offset));
        }
        moves.add(new Move(position, position + 11 * offset));
        moves.add(new Move(position, position + 9 * offset));
        return moves;
    }
}
