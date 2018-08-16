package application;

import board.BoardState;
import pieces.Color;

public class Application {
    public static void main(String[] args) {
        BoardState board = new BoardState(Color.WHITE);

        System.out.println(board.toString());
        GraphicalBoard.main(args);
    }
}
