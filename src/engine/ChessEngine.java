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
            System.out.println("starting thread for move from " + move.getStartPosition() + " to " + move.getEndPosition());
            ComputeThread thread = new ComputeThread(afterMoveBoard, move);
            runningThreads.add(thread);
            thread.run();
        }
        for (ComputeThread thread : runningThreads){
            try{
                thread.join();
                System.out.println("thread ended for move " + thread.getMove().getStartPosition() + " to " + thread.getMove().getEndPosition());
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
}
