package application;

import board.ByteBoard;
import pieces.Color;

import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        Runnable r = () -> {
            Thread.currentThread().setPriority(10);
            GraphicalBoard<ByteBoard> graphicalBoard = new GraphicalBoard<>(new ByteBoard(Color.WHITE));
            graphicalBoard.refreshGui();

            JFrame frame = new JFrame("Cora's Chess Engine");
            frame.add(graphicalBoard.getGui());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationByPlatform(true);

            // ensures the frame is the minimum size it needs to be
            // in order display the components within it
            frame.pack();
            // ensures the minimum size is enforced.
            frame.setMinimumSize(frame.getSize());
            frame.setVisible(true);
        };
        SwingUtilities.invokeLater(r);
    }
}
