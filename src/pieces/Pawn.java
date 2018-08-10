package pieces;

import java.util.List;

public class Pawn extends AbstractPiece {

    public Pawn(Color color, int position){
        super(color, position);
        this.baseValue = 1;
    }

    @Override
    public List<Move> getMoves() {
        return null;
    }
}
