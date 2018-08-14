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
        List<Move> validMoves = boardState.getAllValidMoves(engineColor);

        if (validMoves.size() == 0){
            throw new RuntimeException("No valid moves, checkmate");
        }

        Random rand = new Random();

        int i = rand.nextInt(validMoves.size());

        return validMoves.get(i);
    }
}
