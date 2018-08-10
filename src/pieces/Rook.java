package pieces;

import java.util.List;

public class Rook extends AbstractPiece {

    public Rook(Color color, int position){
        super(color, position);
        this.baseValue = 5;
    }

    @Override
    public List<Move> getMoves() {
        return null;
    }
}
