package application;

import board.BoardState;
import pieces.*;
import sun.awt.image.OffScreenImageSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GraphicalBoard {

    private static final pieces.Color PLAYER_COLOR = pieces.Color.WHITE;
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private ButtonPiece[][] chessBoardSquares = new ButtonPiece[8][8];
    private JPanel chessBoard;
    private static final String COLS = "ABCDEFGH";
    private ButtonPiece currentlySelected;
    private ArrayList<ButtonPiece> currentValidMoves = new ArrayList<>();
    private BoardState boardState = new BoardState();


    private static final int SIZE = 64;
    private static final BufferedImage SHEET;
    static {
        try {
            SHEET = ImageIO.read(new File("src/resources/pieces.png"));
        } catch (IOException x) {
            throw new UncheckedIOException(x);
        }
    }
    private static final BufferedImage GOLD_KING    = SHEET.getSubimage(0, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_KING  = SHEET.getSubimage(0, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_QUEEN     = SHEET.getSubimage(SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_QUEEN   = SHEET.getSubimage(SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_ROOK     = SHEET.getSubimage(2 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_ROOK   = SHEET.getSubimage(2 * SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_KNIGHT   = SHEET.getSubimage(3 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_KNIGHT = SHEET.getSubimage(3 * SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_BISHOP   = SHEET.getSubimage(4 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_BISHOP = SHEET.getSubimage(4 * SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_PAWN     = SHEET.getSubimage(5 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_PAWN   = SHEET.getSubimage(5 * SIZE, SIZE, SIZE, SIZE);
    private static final List<BufferedImage> SPRITES =
            Collections.unmodifiableList(Arrays.asList(GOLD_KING,  SILVER_KING,
                    GOLD_QUEEN,   SILVER_QUEEN,
                    GOLD_ROOK,   SILVER_ROOK,
                    GOLD_KNIGHT, SILVER_KNIGHT,
                    GOLD_BISHOP, SILVER_BISHOP,
                    GOLD_PAWN,   SILVER_PAWN));


    public GraphicalBoard() {
        initializeGui();
    }

    public final void initializeGui() {
        // set up the main GUI
        initMetadata();
        refreshGui();
    }

    public void refreshGui(){
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
                ImageIcon icon;
                AbstractPiece piece = boardState.getPiece(ii, jj);
                icon = getPieceIcon(piece);
                ButtonPiece b = new ButtonPiece(piece);
                b.setMargin(buttonMargin);
                b.addActionListener(new BoardButtonListener());
                b.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                    b.setColor(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                    b.setColor(Color.BLACK);
                }
                chessBoardSquares[jj][ii] = b;
            }
        }

        fillChessBoard();
    }


    public String guiToString(){
        String boardString = "|";
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                AbstractPiece piece = chessBoardSquares[ii][jj].getPiece();
                boardString += " " + piece.getPosition() + " |";
            }
            boardString += "\n|";
        }

        return boardString;
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
            image = SPRITES.get(2 + offset);
        }
        else if (piece instanceof King){
            image = SPRITES.get(offset);
        }

        if (image == null){
            throw new RuntimeException("No valid piece available");
        }

        return new ImageIcon(image);
    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }

    public ButtonPiece getButtonFromBoardStatePosition(int boardStatePos) {
        if (boardState.getBoard()[boardStatePos] instanceof InvalidPiece){
            throw new RuntimeException("Requesting position outside of GUI bounds: " + boardStatePos);
        }
        boardStatePos -= 20;
        int x = (boardStatePos%10) -1;
        int y = boardStatePos/10;

        return chessBoardSquares[x][y];
    }

    public static void main(String[] args) {
        Runnable r = () -> {
            GraphicalBoard graphicalBoard = new GraphicalBoard();
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


    private void movePiece(ButtonPiece button, ImageIcon icon, OffScreenImageSource source){
        int startPosition = currentlySelected.getPiece().getPosition();
        int endPosition = button.getPiece().getPosition();
        //case for castling
        if (currentlySelected.getPiece() instanceof King && (startPosition + 2 == endPosition || startPosition -2 == endPosition)){
            EmptyPiece rookEmptyPiece;
            int rookPosition;
            int futureRookPosition;
            if (startPosition + 2 == endPosition){
                rookPosition = startPosition+3;
                futureRookPosition = startPosition+1;
            }
            else{
                rookPosition = startPosition-4;
                futureRookPosition = startPosition-1;
            }
            ButtonPiece rook = getButtonFromBoardStatePosition(rookPosition);
            ButtonPiece futureRook = getButtonFromBoardStatePosition(futureRookPosition);
            rookEmptyPiece = new EmptyPiece(rookPosition);
            ImageIcon emptyIcon = new ImageIcon(new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB));
            boardState.makeMove(new Move(rookPosition, futureRookPosition));
            futureRook.setPiece(rook.getPiece());
            futureRook.setIcon(rook.getIcon());
            rook.setPiece(rookEmptyPiece);
            rook.setIcon(emptyIcon);


            moveNormally(button, icon, source);
        }
        else{
            moveNormally(button, icon, source);
        }
        currentlySelected = null;
    }

    private void moveNormally(ButtonPiece button, ImageIcon icon, OffScreenImageSource source){
        int startPosition = currentlySelected.getPiece().getPosition();
        int endPosition = button.getPiece().getPosition();
        EmptyPiece emptyPiece = new EmptyPiece(currentlySelected.getPiece().getPosition());
        System.out.println("making move from " + startPosition + " to " + endPosition);
        boardState.makeMove(new Move(startPosition, endPosition));
        ImageIcon currentIcon = (ImageIcon)currentlySelected.getIcon();
        if (isColorPiece(source, pieces.Color.getOpposite(PLAYER_COLOR))){
            icon = new ImageIcon(new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB));
        }
        currentlySelected.setIcon(icon);
        currentlySelected.setBackground(currentlySelected.getColor());
        button.setPiece(currentlySelected.getPiece());
        button.setIcon(currentIcon);
        currentlySelected.setPiece(emptyPiece);
        System.out.println("old position set to empty piece at position " + emptyPiece.getPosition());
    }

    private void selectPiece(ButtonPiece button){
        if (currentlySelected != null){
            currentlySelected.setBackground(currentlySelected.getColor());
        }
        button.setBackground(Color.YELLOW);
        currentlySelected = button;
        clearMoves();
        displayMoves(button.getPiece());
    }

    private void clearMoves(){
        for (ButtonPiece button : currentValidMoves){
            button.setBackground(button.getColor());
        }
        currentValidMoves.clear();
    }

    private void displayMoves(AbstractPiece piece){
        List<Move> moves = piece.getMoves(boardState);
        System.out.println("number of possible moves: " + moves.size());
        for (Move move : moves){
            if (boardState.isMoveValid(move)){
                ButtonPiece button = getButtonFromBoardStatePosition(move.getEndPosition());
                button.setBackground(Color.GREEN);
                currentValidMoves.add(button);
            }
        }
    }

    private boolean isColorPiece(OffScreenImageSource source, pieces.Color color){
        int offset = 0;
        if (color == pieces.Color.WHITE){
            offset = 1;
        }
        if (source.equals(SPRITES.get(10 + offset).getSource())){
            return true;
        }
        else if (source.equals(SPRITES.get(8 + offset).getSource())){
            return true;
        }
        else if (source.equals(SPRITES.get(6 + offset).getSource())){
            return true;
        }
        else if (source.equals(SPRITES.get(4 + offset).getSource())){
            return true;
        }
        else if (source.equals(SPRITES.get(2 + offset).getSource())){
            return true;
        }
        else {
            return source.equals(SPRITES.get(offset).getSource());
        }
    }

    private class BoardButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ButtonPiece button = (ButtonPiece)e.getSource();
            ImageIcon icon = (ImageIcon)button.getIcon();
            OffScreenImageSource source = (OffScreenImageSource)icon.getImage().getSource();
            if (!isColorPiece(source, PLAYER_COLOR)){
                //checking for green color ensures its a valid move
                if (currentlySelected != null && button.getBackground() == Color.GREEN){
                    movePiece(button, icon, source);
                    clearMoves();
                    System.out.println(boardState.toString() + "\n\n");
                    //System.out.println(guiToString() + "\n\n");
                }
            }
            else{
                selectPiece(button);
            }
        }
    }
}