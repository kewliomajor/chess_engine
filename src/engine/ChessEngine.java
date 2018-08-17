package engine;

import application.CheckmateException;
import application.StalemateException;
import board.BoardState;
import pieces.Color;
import pieces.Move;

import java.util.ArrayList;
import java.util.List;

public class ChessEngine {

    private Color engineColor;
    private OpeningBook openingBook;
    private List<ComputeThread> runningThreads = new ArrayList<>();

    public ChessEngine(Color engineColor){
        this.engineColor = engineColor;
    }

    public void reBuildOpeningBook(BoardState boardState){
        this.openingBook = new OpeningBook(boardState);
    }

    public void setEngineColor(Color color){
        this.engineColor = color;
    }

    public Move getBestMove(BoardState boardState){
        System.out.println("selecting best move for " + boardState.getCurrentMoveColor());

        //check opening book
        if (openingBook == null){
            throw new RuntimeException("No opening book has been created");
        }
        Move bookMove = openingBook.getNextMove(boardState.getMoveHistory());
        if (bookMove != null){
            return bookMove;
        }

        List<Move> validMoves = boardState.getAllValidMoves(engineColor);
        double bestScore = -10000;
        Move bestMove = null;

        if (validMoves.size() == 0){
            if (boardState.kingInCheck(engineColor)){
                throw new CheckmateException("No valid moves, King in check: checkmate");
            }
            else{
                throw new StalemateException("No valid moves, King not in check: stalemate");
            }
        }

        if (validMoves.size() == 1){
            return validMoves.get(0);
        }

        for (Move move : validMoves) {
            BoardState afterMoveBoard = new BoardState(boardState);
            afterMoveBoard.makeMove(move);
            ComputeThread thread = new ComputeThread(afterMoveBoard, move);
            runningThreads.add(thread);
            thread.run();
        }
        for (ComputeThread thread : runningThreads){
            try{
                thread.join();
                if (thread.getScore() > bestScore){
                    bestScore = thread.getScore();
                    bestMove = thread.getMove();
                }
            }
            catch(InterruptedException e){
                throw new RuntimeException("Thread interrupted: " + e.getMessage());
            }
        }

        runningThreads.clear();

        //when it's going to be checkmated no matter what it gets mad and doesn't want to make a move lol
        if (bestMove == null){
            return validMoves.get(0);
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

    public class ComputeThread extends Thread {

        private BoardState boardState;
        private Move move;
        private double score;

        public ComputeThread(BoardState boardState, Move move){
            this.boardState = boardState;
            this.move = move;
        }

        public void run()
        {
            score = -alphaBetaMax(boardState, -10000, +10000, 4);
        }

        public Move getMove(){
            return move;
        }

        public double getScore(){
            return score;
        }
    }
}
