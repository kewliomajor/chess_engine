package application;

import board.BoardState;
import engine.ChessEngine;
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

    private static pieces.Color PLAYER_COLOR = pieces.Color.WHITE;
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private ButtonPiece[][] chessBoardSquares = new ButtonPiece[8][8];
    private JPanel chessBoard;
    private static final String COLS = "ABCDEFGH";
    private static final String EVAL_STRING = "Current Evaluation: ";
    private ButtonPiece currentlySelected;
    private ArrayList<ButtonPiece> currentValidMoves = new ArrayList<>();
    private ArrayList<ButtonPiece> lastComputerMove = new ArrayList<>();
    private BoardState boardState = new BoardState(PLAYER_COLOR);
    private ChessEngine engine;
    private boolean waitingForComputer = false;
    private JLabel currentEval = new JLabel(EVAL_STRING, SwingConstants.LEFT);


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
        engine = new ChessEngine(pieces.Color.getOpposite(PLAYER_COLOR));
        initializeGui();
    }

    public final void initializeGui() {
        // set up the main GUI
        gui.removeAll();
        initMetadata();
        refreshGui();
        gui.revalidate();
        gui.repaint();
        if (PLAYER_COLOR == pieces.Color.BLACK){
            makeEngineMove();
        }
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
        setSquares();

        fillChessBoard();
    }

    private void initMetadata(){
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        JButton newButton = new JButton("New");
        JButton resignButton = new JButton("Resign");
        JButton swapColorButton = new JButton("Swap Color");
        newButton.addActionListener(new NewGameListener());
        resignButton.addActionListener(new ResignListener());
        swapColorButton.addActionListener(new SwapColorListener());
        tools.add(newButton);
        tools.add(new JButton("Save")); // TODO - add functionality!
        tools.add(new JButton("Restore")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(resignButton);
        tools.add(swapColorButton);
        tools.addSeparator();
        tools.add(currentEval);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);
    }


    private void setSquares(){
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


    private void fillChessBoard(){
        //fill the chess board
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(new JLabel(COLS.substring(ii, ii + 1), SwingConstants.CENTER));
        }

        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (ii + 1), SwingConstants.CENTER));
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
            image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
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


    private void movePiece(ButtonPiece toButton, ButtonPiece fromButton){
        int startPosition = fromButton.getPiece().getPosition();
        int endPosition = toButton.getPiece().getPosition();
        //case for castling
        if (fromButton.getPiece() instanceof King && (startPosition + 2 == endPosition || startPosition -2 == endPosition)){
            EmptyPiece rookEmptyPiece;
            int rookPosition;
            int futureRookPosition;
            int offset = 0;
            if (PLAYER_COLOR == pieces.Color.BLACK){
                offset = 1;
            }
            if (startPosition + 2 == endPosition){
                rookPosition = startPosition+3 + offset;
                futureRookPosition = startPosition+1;
            }
            else{
                rookPosition = startPosition-4 + offset;
                futureRookPosition = startPosition-1;
            }
            ButtonPiece rook = getButtonFromBoardStatePosition(rookPosition);
            ButtonPiece futureRook = getButtonFromBoardStatePosition(futureRookPosition);
            rookEmptyPiece = new EmptyPiece(rookPosition);
            ImageIcon emptyIcon = new ImageIcon(new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB));
            boardState.makeMove(new Move(rookPosition, futureRookPosition), false);
            futureRook.setPiece(rook.getPiece());
            futureRook.setIcon(rook.getIcon());
            rook.setPiece(rookEmptyPiece);
            rook.setIcon(emptyIcon);


            moveNormally(toButton, fromButton);
        }
        else{
            moveNormally(toButton, fromButton);
        }
    }

    private void moveNormally(ButtonPiece button, ButtonPiece fromButton){
        ImageIcon icon = (ImageIcon)button.getIcon();
        OffScreenImageSource source = (OffScreenImageSource)icon.getImage().getSource();
        int startPosition = fromButton.getPiece().getPosition();
        int endPosition = button.getPiece().getPosition();
        EmptyPiece emptyPiece = new EmptyPiece(fromButton.getPiece().getPosition());
        System.out.println("making move from " + startPosition + " to " + endPosition);
        boardState.makeMove(new Move(startPosition, endPosition));
        ImageIcon currentIcon = (ImageIcon)fromButton.getIcon();
        AbstractPiece currentPiece = fromButton.getPiece();
        if (boardState.getBoard()[endPosition] instanceof Queen){
            currentIcon = getPieceIcon(boardState.getBoard()[endPosition]);
            currentPiece = boardState.getBoard()[endPosition];
        }
        if (isColorPiece(source, pieces.Color.getOpposite(fromButton.getPiece().getColor()))){
            icon = new ImageIcon(new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB));
        }
        fromButton.setIcon(icon);
        fromButton.setBackground(fromButton.getColor());
        button.setPiece(currentPiece);
        button.setIcon(currentIcon);
        fromButton.setPiece(emptyPiece);
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
            //System.out.println("checking move validity " + move.getStartPosition() + " to " +move.getEndPosition());
            if (boardState.isMoveValid(move)){
                ButtonPiece button = getButtonFromBoardStatePosition(move.getEndPosition());
                //System.out.println("setting button to green at " + move.getEndPosition());
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
        List<Integer> possiblePositions = new ArrayList<Integer>() {{
            add(10);
            add(8);
            add(6);
            add(4);
            add(2);
            add(0);
        }};
        for (Integer pos : possiblePositions){
            if (source.equals(SPRITES.get(pos + offset).getSource())){
                return true;
            }
        }
        return false;
    }

    private void makeEngineMove(){
        updateEvaluation();
        for (ButtonPiece button : lastComputerMove){
            button.setBackground(button.getColor());
        }
        lastComputerMove.clear();
        try{
            Move move = engine.getBestMove(boardState);
            ButtonPiece fromPiece = getButtonFromBoardStatePosition(move.getStartPosition());
            ButtonPiece toPiece = getButtonFromBoardStatePosition(move.getEndPosition());
            movePiece(toPiece, fromPiece);
            toPiece.setBackground(Color.YELLOW);
            fromPiece.setBackground(Color.YELLOW);
            lastComputerMove.add(toPiece);
            lastComputerMove.add(fromPiece);
        }
        catch(CheckmateException e){
            showWinner(PLAYER_COLOR);
        }
        catch(StalemateException e){
            showDraw();
        }
        if (boardState.getAllValidMoves(PLAYER_COLOR).size() == 0){
            if (boardState.kingInCheck(PLAYER_COLOR)){
                showWinner(pieces.Color.getOpposite(PLAYER_COLOR));
            }
            else{
                showDraw();
            }
        }
        waitingForComputer = false;
        updateEvaluation();
    }

    private void showWinner(pieces.Color color){
        JOptionPane.showMessageDialog(null, color + " wins!");
        boardState = new BoardState(PLAYER_COLOR);
        initializeGui();
    }

    private void showDraw(){
        JOptionPane.showMessageDialog(null, "Draw!");
        boardState = new BoardState(PLAYER_COLOR);
        initializeGui();
    }

    private void updateEvaluation(){
        double score = boardState.getBoardScore();
        JToolBar toolBar = (JToolBar) gui.getComponent(0);
        toolBar.remove(7);
        currentEval = new JLabel(EVAL_STRING + score, SwingConstants.LEFT);
        toolBar.add(currentEval);
        gui.revalidate();
        gui.repaint();
    }

    private class BoardButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ButtonPiece button = (ButtonPiece)e.getSource();
            ImageIcon icon = (ImageIcon)button.getIcon();
            OffScreenImageSource source = (OffScreenImageSource)icon.getImage().getSource();
            if (!isColorPiece(source, PLAYER_COLOR)){
                //checking for green color ensures its a valid move
                if (currentlySelected != null && button.getBackground() == Color.GREEN && !waitingForComputer){
                    movePiece(button, currentlySelected);
                    currentlySelected = null;
                    clearMoves();
                    System.out.println(boardState.toString() + "\n\n");
                    waitingForComputer = true;
                    makeEngineMove();
                }
            }
            else{
                selectPiece(button);
            }
        }
    }

    private class NewGameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            boardState = new BoardState(PLAYER_COLOR);
            initializeGui();
        }
    }

    private class ResignListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            showWinner(pieces.Color.getOpposite(PLAYER_COLOR));
        }
    }

    private class SwapColorListener implements  ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            engine.setEngineColor(PLAYER_COLOR);
            PLAYER_COLOR = pieces.Color.getOpposite(PLAYER_COLOR);
            boardState = new BoardState(PLAYER_COLOR);
            initializeGui();
        }
    }
}
