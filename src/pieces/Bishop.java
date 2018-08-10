package pieces;

import java.util.List;

public class Bishop extends AbstractPiece {

    public Bishop(Color color, int position){
        super(color, position);
        this.baseValue = 3;
    }

    @Override
    public List<Move> getMoves() {
        return null;
    }
}
