package engine;
import board.AbstractBoard;
import pieces.Move;

import java.util.List;

public class ComputeThread<T> extends Thread {

    private AbstractBoard boardState;
    private Move move;
    private double score;
    private int maxDepth;
    private boolean die = false;

    public ComputeThread(AbstractBoard boardState, Move move, int maxDepth){
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

    double alphaBetaMax(AbstractBoard boardState, double alpha, double beta, int depthLeft) {
        if ( depthLeft == 0 ) {
            return boardState.getBoardScore();
        }
        List<Move> validMoves = boardState.getAllValidMoves(boardState.getCurrentMoveColor());
        for (Move move : validMoves) {
            if (die){
                return 0;
            }
            AbstractBoard<T> afterMoveBoard = (AbstractBoard<T>) boardState.getInstance(boardState);
            afterMoveBoard.makeMove(move);
            double score = alphaBetaMin(afterMoveBoard, alpha, beta, depthLeft - 1 );
            if( score >= beta )
                return beta;   // fail hard beta-cutoff
            if( score > alpha )
                alpha = score; // alpha acts like max in MiniMax
        }
        return alpha;
    }

    double alphaBetaMin(AbstractBoard boardState, double alpha, double beta, int depthLeft ) {
        if ( depthLeft == 0 ){
            return -boardState.getBoardScore();
        }
        List<Move> validMoves = boardState.getAllValidMoves(boardState.getCurrentMoveColor());
        for (Move move : validMoves) {
            if (die){
                return 0;
            }
            AbstractBoard<T> afterMoveBoard = (AbstractBoard<T>) boardState.getInstance(boardState);
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