package engine;

import application.CheckmateException;
import application.StalemateException;
import board.BoardState;
import pieces.Color;
import pieces.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessEngine {

    private static final int SEARCH_DEPTH = 4;
    private Color engineColor;
    private OpeningBook openingBook;
    private List<ComputeThread> runningThreads = new ArrayList<>();
    private Map<Move, ResponseComputeThread> responses = new HashMap<>();

    public ChessEngine(Color engineColor){
        this.engineColor = engineColor;
    }

    public void reBuildOpeningBook(BoardState boardState){
        this.openingBook = new OpeningBook(boardState);
    }

    public void setEngineColor(Color color){
        this.engineColor = color;
    }

    public void searchInBackground(BoardState boardState){
        clearRunningResponses();
        List<Move> validMoves = boardState.getAllValidMoves(boardState.getCurrentMoveColor());
        for (Move validMove : validMoves){
            BoardState afterMoveBoard = new BoardState(boardState);
            afterMoveBoard.makeMove(validMove);
            //System.out.println("starting response thread for move from " + validMove.getStartPosition() + " to " + validMove.getEndPosition());
            ResponseComputeThread thread = new ResponseComputeThread(afterMoveBoard, SEARCH_DEPTH);
            responses.put(validMove, thread);
            thread.setPriority(5);
            thread.start();
        }
    }

    private void clearRunningResponses(){
        for (Move move : responses.keySet()){
            ResponseComputeThread thread = responses.get(move);
            thread.killThread();
        }
        responses.clear();
    }

    public Move getBestMove(BoardState boardState){
        System.out.println("selecting best move for " + boardState.getCurrentMoveColor());

        Move response = getResponseFromLastMove(boardState);
        if (response != null){
            return response;
        }
        clearRunningThreads();

        return computeBestMove(boardState, runningThreads, SEARCH_DEPTH);
    }

    private void clearRunningThreads(){
        for (ComputeThread thread : runningThreads){
            thread.killThread();
        }
        runningThreads.clear();
    }

    private Move getResponseFromLastMove(BoardState boardState){
        Move response = null;
        List<Move> history = boardState.getMoveHistory();
        if (history.size() == 0){
            return response;
        }
        Move lastMove = history.get(history.size()-1);
        ResponseComputeThread correctThread = null;

        for (Move move : responses.keySet()){
            ResponseComputeThread thread = responses.get(move);
            if (move.equals(lastMove)){
                correctThread = thread;
            }
            else{
                thread.killThread();
            }
        }

        if (correctThread == null){
            System.out.println("no thread for that move");
            return response;
        }

        try {
            correctThread.join();
            System.out.println("getting move from background thread for response " + lastMove.getStartPosition() + " to " + lastMove.getEndPosition());
            response = correctThread.getBestResponse();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }

    private Move computeBestMove(BoardState boardState, List<ComputeThread> runningThreads, int searchDepth){
        Move openingMove = getOpeningMove(boardState);

        if (openingMove != null){
            return openingMove;
        }

        List<Move> validMoves = boardState.getAllValidMoves(engineColor);

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

        Move bestMove = computeFromScore(validMoves, boardState, runningThreads, searchDepth);

        runningThreads.clear();

        //when it's going to be checkmated no matter what it gets mad and doesn't want to make a move lol
        if (bestMove == null){
            return validMoves.get(0);
        }

        return bestMove;
    }

    private Move getOpeningMove(BoardState boardState){
        //check opening book
        if (openingBook == null){
            throw new RuntimeException("No opening book has been created");
        }
        return openingBook.getNextMove(boardState.getMoveHistory());
    }

    private Move computeFromScore(List<Move> validMoves, BoardState boardState, List<ComputeThread> runningThreads, int searchDepth){
        double bestScore = -10000;
        Move bestMove = null;
        for (Move move : validMoves) {
            BoardState afterMoveBoard = new BoardState(boardState);
            afterMoveBoard.makeMove(move);
            //System.out.println("starting thread for move from " + move.getStartPosition() + " to " + move.getEndPosition());
            ComputeThread thread = new ComputeThread(afterMoveBoard, move, searchDepth);
            runningThreads.add(thread);
            thread.setPriority(5);
            thread.start();
        }
        for (ComputeThread thread : runningThreads){
            try{
                thread.join();
                //System.out.println("thread ended for move " + thread.getMove().getStartPosition() + " to " + thread.getMove().getEndPosition());
                if (thread.getScore() > bestScore){
                    bestScore = thread.getScore();
                    bestMove = thread.getMove();
                }
            }
            catch(InterruptedException e){
                //we're the ones interrupting so we dont care
            }
        }
        return bestMove;
    }

    private class ResponseComputeThread extends Thread {
        private BoardState boardState;
        private int maxDepth;
        private Move bestResponse;
        private List<ComputeThread> runningThreads = new ArrayList<>();
        private boolean die = false;


        public ResponseComputeThread(BoardState boardState, int maxDepth){
            this.boardState = boardState;
            this.maxDepth = maxDepth;
        }

        public Move getBestResponse(){
            return bestResponse;
        }

        public void killThread(){
            for (ComputeThread thread : runningThreads){
                thread.killThread();
            }
            die = true;
        }

        public void run(){
            this.bestResponse = computeBestMove(boardState, runningThreads, maxDepth);
        }
    }
}
