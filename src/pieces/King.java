package pieces;

import board.BoardState;

import java.util.ArrayList;
import java.util.List;

public class King extends AbstractPiece {

    public King(Color color, int position){
        super(color, position);
        this.baseValue = 0;
    }

    public King(King king){
        super(king.getColor(), king.getPosition());
        baseValue = king.baseValue;
        hasMoved = king.hasMoved;
    }

    @Override
    public List<Move> getMoves(BoardState boardState) {
        List<Move> moves = new ArrayList<>();
        moves.add(new Move(position, position + 9));
        moves.add(new Move(position, position + 10));
        moves.add(new Move(position, position + 11));
        moves.add(new Move(position, position + 1));
        moves.add(new Move(position, position - 1));
        moves.add(new Move(position, position - 9));
        moves.add(new Move(position, position - 10));
        moves.add(new Move(position, position - 11));
        //castling
        if (!boardState.kingInCheck(color)){
            AbstractPiece piece = boardState.getBoard()[position + 3];
            if (!hasMoved && !piece.hasMoved() && piece instanceof Rook){
                moves.add(new Move(position, position + 2));
            }
            piece = boardState.getBoard()[position - 4];
            if (!hasMoved && !piece.hasMoved() && piece instanceof Rook){
                moves.add(new Move(position, position - 2));
            }
        }
        return moves;
    }
}
