package engine;
import board.BoardState;
import pieces.Move;

import java.util.List;

public class ComputeThread extends Thread {

    private BoardState boardState;
    private Move move;
    private double score;
    private int maxDepth;
    private boolean die = false;

    public ComputeThread(BoardState boardState, Move move, int maxDepth){
        this.boardState = boardState;
        this.move = move;
        this.maxDepth = maxDepth;
    }

    public void run()
    {
        score = -alphaBetaMax(boardState, -10000, +10000, maxDepth);
    }

    public Move getMove(){
        return move;
    }

    public double getScore(){
        return score;
    }

    public void killThread(){
        die = true;
    }

    double alphaBetaMax(BoardState boardState, double alpha, double beta, int depthLeft) {
        if ( depthLeft == 0 ) {
            return boardState.getBoardScore();
        }
        List<Move> validMoves = boardState.getAllValidMoves(boardState.getCurrentMoveColor());
        for (Move move : validMoves) {
            if (die){
                return 0;
            }
            BoardState afterMoveBoard = new BoardState(boardState);
            afterMoveBoard.makeMove(move);
            double score = alphaBetaMin(afterMoveBoard, alpha, beta, depthLeft - 1 );
            if( score >= beta )
                return beta;   // fail hard beta-cutoff
            if( score > alpha )
                alpha = score; // alpha acts like max in MiniMax
        }
        return alpha;
    }

    double alphaBetaMin(BoardState boardState, double alpha, double beta, int depthLeft ) {
        if ( depthLeft == 0 ){
            return -boardState.getBoardScore();
        }
        List<Move> validMoves = boardState.getAllValidMoves(boardState.getCurrentMoveColor());
        for (Move move : validMoves) {
            if (die){
                return 0;
            }
            BoardState afterMoveBoard = new BoardState(boardState);
            afterMoveBoard.makeMove(move);
            double score = alphaBetaMax(afterMoveBoard, alpha, beta, depthLeft - 1 );
            if( score <= alpha )
                return alpha; // fail hard alpha-cutoff
            if( score < beta )
                beta = score; // beta acts like min in MiniMax
        }
        return beta;
    }
}