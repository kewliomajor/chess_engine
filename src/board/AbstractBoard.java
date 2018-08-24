package board;

import pieces.Color;
import pieces.Move;

import java.util.List;

public abstract class AbstractBoard<T> {

    public static final int BOARD_SIZE = 120;
    protected static final int CHECKMATE_SCORE = 1000;

    protected Color currentMove = Color.WHITE;
    protected Color playerColor;

    protected List<Move> moveHistory;

    public abstract boolean pieceIsInvalid(int position);

    public abstract boolean pieceIsKing(int position);

    public abstract boolean pieceIsQueen(int position);

    public abstract T getInstance(Color playerColor);

    public abstract T getInstance(AbstractBoard board);

    public List<Move> getMoveHistory(){
        return moveHistory;
    }

    public Color getCurrentMoveColor(){
        return currentMove;
    }

    public Color getPlayerColor(){
        return playerColor;
    }

    public abstract double getBoardScore();

    public abstract List<Move> getValidPieceMoves(int position);

    public abstract Object getObject(int position);

    public abstract String getPieceString(int position);

    public abstract Color getPieceColor(int position);

    /**
     * Excepts that the move is valid, must check isMoveValid before calling
     *
     * @param move
     */
    public void makeMove(Move move){
        move(move);
        currentMove = Color.getOpposite(currentMove);
        moveHistory.add(move);
    }

    public abstract void move(Move move);

    public abstract int getBlackKingPosition();

    public abstract int getWhiteKingPosition();

    public abstract boolean kingInCheck(Color color);

    public abstract List<Move> getAllValidMoves(Color color);

    public abstract boolean isMoveValid(Move move);

    public abstract String toString();
}
