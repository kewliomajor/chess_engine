package application;

import board.BoardState;

public class Application {
    public static void main(String[] args) {
        BoardState board = new BoardState();

        System.out.println(board.toString());
        GraphicalBoard.main(args);
    }
}
