package pieces;

import board.BoardState;

import java.util.ArrayList;
import java.util.List;

public class Knight extends AbstractPiece {

    public Knight(Color color, int position){
        super(color, position);
        this.baseValue = 3;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        List<Move> moves = new ArrayList<>();
        moves.add(new Move(position, position + 21));
        moves.add(new Move(position, position + 19));
        moves.add(new Move(position, position + 12));
        moves.add(new Move(position, position + 8));
        moves.add(new Move(position, position - 8));
        moves.add(new Move(position, position - 12));
        moves.add(new Move(position, position - 19));
        moves.add(new Move(position, position - 21));
        return moves;
    }
}
