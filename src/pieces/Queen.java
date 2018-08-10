package pieces;

import java.util.List;

public class Queen extends AbstractPiece {

    public Queen(Color color, int position){
        super(color, position);
        this.baseValue = 1;
    }

    @Override
    public List<Move> getMoves() {
        return null;
    }
}
