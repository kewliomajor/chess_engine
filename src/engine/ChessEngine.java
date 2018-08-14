package engine;

import board.BoardState;
import pieces.Color;
import pieces.Move;

import java.util.List;
import java.util.Random;

public class ChessEngine {

    private Color engineColor;

    public ChessEngine(Color engineColor){
        this.engineColor = engineColor;
    }

    public Move getBestMove(BoardState boardState){
        System.out.println("selecting best move for " + boardState.getCurrentMoveColor());
        List<Move> validMoves = boardState.getAllValidMoves(engineColor);
        double bestScore = -1000;
        Move bestMove = null;

        if (validMoves.size() == 0){
            throw new RuntimeException("No valid moves, checkmate");
        }

        for (Move move : validMoves) {
            BoardState afterMoveBoard = new BoardState(boardState);
            afterMoveBoard.makeMove(move);
            double score = -alphaBetaMax(afterMoveBoard, -1000, +1000, 2);
            if (score > bestScore){
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }


    double alphaBetaMax(BoardState boardState, double alpha, double beta, int depthLeft) {
        if ( depthLeft == 0 ) {
            return boardState.getBoardScore();
        }
        List<Move> validMoves = boardState.getAllValidMoves(boardState.getCurrentMoveColor());
        for (Move move : validMoves) {
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
