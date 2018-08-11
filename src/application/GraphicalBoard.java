package application;

import board.BoardState;
import pieces.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GraphicalBoard {

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private JPanel chessBoard;
    private static final String COLS = "ABCDEFGH";


    public static final int SIZE = 64;
    public static final BufferedImage SHEET;
    static {
        try {
            SHEET = ImageIO.read(new File("src/resources/pieces.png"));
        } catch (IOException x) {
            throw new UncheckedIOException(x);
        }
    }
    public static final BufferedImage GOLD_QUEEN    = SHEET.getSubimage(0 * SIZE, 0,    SIZE, SIZE);
    public static final BufferedImage SILVER_QUEEN  = SHEET.getSubimage(0 * SIZE, SIZE, SIZE, SIZE);
    public static final BufferedImage GOLD_KING     = SHEET.getSubimage(1 * SIZE, 0,    SIZE, SIZE);
    public static final BufferedImage SILVER_KING   = SHEET.getSubimage(1 * SIZE, SIZE, SIZE, SIZE);
    public static final BufferedImage GOLD_ROOK     = SHEET.getSubimage(2 * SIZE, 0,    SIZE, SIZE);
    public static final BufferedImage SILVER_ROOK   = SHEET.getSubimage(2 * SIZE, SIZE, SIZE, SIZE);
    public static final BufferedImage GOLD_KNIGHT   = SHEET.getSubimage(3 * SIZE, 0,    SIZE, SIZE);
    public static final BufferedImage SILVER_KNIGHT = SHEET.getSubimage(3 * SIZE, SIZE, SIZE, SIZE);
    public static final BufferedImage GOLD_BISHOP   = SHEET.getSubimage(4 * SIZE, 0,    SIZE, SIZE);
    public static final BufferedImage SILVER_BISHOP = SHEET.getSubimage(4 * SIZE, SIZE, SIZE, SIZE);
    public static final BufferedImage GOLD_PAWN     = SHEET.getSubimage(5 * SIZE, 0,    SIZE, SIZE);
    public static final BufferedImage SILVER_PAWN   = SHEET.getSubimage(5 * SIZE, SIZE, SIZE, SIZE);
    public static final List<BufferedImage> SPRITES =
            Collections.unmodifiableList(Arrays.asList(GOLD_QUEEN,  SILVER_QUEEN,
                    GOLD_KING,   SILVER_KING,
                    GOLD_ROOK,   SILVER_ROOK,
                    GOLD_KNIGHT, SILVER_KNIGHT,
                    GOLD_BISHOP, SILVER_BISHOP,
                    GOLD_PAWN,   SILVER_PAWN));


    GraphicalBoard() {
        initializeGui();
    }

    public final void initializeGui() {
        // set up the main GUI
        initMetadata();
        refreshGui(null);
    }

    public void refreshGui(BoardState boardState){
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(Color.BLACK));
        try{
            gui.remove(2);
        }
        catch(ArrayIndexOutOfBoundsException e){
            //nothing to remove, just add
        }
        gui.add(chessBoard);

        // create the chess board squares
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = null;
                if (boardState == null){
                    icon = getPieceIcon(EmptyPiece.getInstance());
                }
                else{
                    AbstractPiece piece = boardState.getPiece(ii, jj);
                    icon = getPieceIcon(piece);
                }
                b.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
                chessBoardSquares[jj][ii] = b;
            }
        }

        fillChessBoard();
    }


    private void initMetadata(){
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        tools.add(new JButton("New")); // TODO - add functionality!
        tools.add(new JButton("Save")); // TODO - add functionality!
        tools.add(new JButton("Restore")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(new JButton("Resign")); // TODO - add functionality!

        gui.add(new JLabel("?"), BorderLayout.LINE_START);
    }


    private void fillChessBoard(){
        //fill the chess board
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                            SwingConstants.CENTER));
        }

        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (ii + 1),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                }
            }
        }
    }

    private ImageIcon getPieceIcon(AbstractPiece piece){
        BufferedImage image = null;
        int offset = 0;
        if (piece.getColor() == pieces.Color.WHITE){
            offset = 1;
        }

        if (piece instanceof EmptyPiece){
            image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        }
        else if (piece instanceof InvalidPiece){
            throw new RuntimeException("shouldn't have an invalid piece at a valid board position");
        }
        else if (piece instanceof Pawn){
            image = SPRITES.get(10 + offset);
        }
        else if (piece instanceof Rook){
            image = SPRITES.get(4 + offset);
        }
        else if (piece instanceof Knight){
            image = SPRITES.get(6 + offset);
        }
        else if (piece instanceof Bishop){
            image = SPRITES.get(8 + offset);
        }
        else if (piece instanceof Queen){
            image = SPRITES.get(0 + offset);
        }
        else if (piece instanceof King){
            image = SPRITES.get(2 + offset);
        }

        return new ImageIcon(image);
    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                BoardState board = new BoardState();
                GraphicalBoard graphicalBoard = new GraphicalBoard();
                graphicalBoard.refreshGui(board);

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
            }
        };
        SwingUtilities.invokeLater(r);
    }
}
