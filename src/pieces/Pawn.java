package pieces;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends AbstractPiece {

    private boolean doubleMove = false;

    public Pawn(Color color, int position){
        super(color, position);
        this.baseValue = 1;
    }

    public boolean isDoubleMoving(){
        return doubleMove;
    }

    public void doubleMoving(){
        doubleMove = true;
    }

    public void finishedDoubleMove(){
        doubleMove = false;
    }

    @Override
    public List<Move> getMoves() {
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
