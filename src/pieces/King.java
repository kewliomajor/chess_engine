package pieces;

import board.BoardState;

import java.util.List;

public class King extends AbstractPiece {

    public King(Color color, int position){
        super(color, position);
        this.baseValue = 1;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        return null;
    }
}
