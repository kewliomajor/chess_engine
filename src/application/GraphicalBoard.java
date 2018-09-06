package application;

import board.AbstractBoard;
import board.ByteBoard;
import board.BoardState;
import engine.ChessEngine;
import pieces.*;
import sun.awt.image.OffScreenImageSource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GraphicalBoard<T> {

    private static pieces.Color PLAYER_COLOR = pieces.Color.WHITE;
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private ButtonPiece[][] chessBoardSquares = new ButtonPiece[8][8];
    private JPanel chessBoard;
    private static String COLS = "ABCDEFGH";
    private static String ROWS = "87654321";
    private static final String EVAL_STRING = "Current Evaluation: ";
    private ButtonPiece currentlySelected;
    private ArrayList<ButtonPiece> currentValidMoves = new ArrayList<>();
    private ArrayList<ButtonPiece> lastComputerMove = new ArrayList<>();
    private AbstractBoard<T> boardState;
    private ChessEngine<ByteBoard> engine;
    private ChessGraphics chessGraphics = new ChessGraphics();
    private boolean waitingForComputer = false;
    private JLabel currentEval = new JLabel(EVAL_STRING, SwingConstants.LEFT);



    public GraphicalBoard(AbstractBoard<T> boardState) {
        this.boardState = boardState;
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
        engine.reBuildOpeningBook(boardState);
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
                int position = getBoardStatePositionFromButtonPosition(jj, ii);
                String piece = boardState.getPieceString(position).trim();
                icon = chessGraphics.getPieceIcon(piece, boardState.getPieceColor(position));
                ButtonPiece b = new ButtonPiece(position, jj, ii);
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
                int position = chessBoardSquares[ii][jj].getPiecePosition();
                boardString += " " + position + " |";
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
                        chessBoard.add(new JLabel(ROWS.substring(ii, ii + 1), SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                }
            }
        }
    }

    public final JComponent getGui() {
        return gui;
    }

    public int getBoardStatePositionFromButtonPosition(int x, int y){
        return boardState.getBoardStatePositionFromGraphicalPosition(x, y);
    }

    public ButtonPiece getButtonFromBoardStatePosition(int boardStatePos) {
        if (boardState.pieceIsInvalid(boardStatePos)){
            throw new RuntimeException("Requesting position outside of GUI bounds: " + boardStatePos);
        }
        int x = boardState.getGraphicalXFromPosition(boardStatePos);
        int y = boardState.getGraphicalYFromPosition(boardStatePos);

        return chessBoardSquares[x][y];
    }


    private void movePiece(ButtonPiece toButton, ButtonPiece fromButton){
        int startPosition = getBoardStatePositionFromButtonPosition(fromButton.getBoardX(), fromButton.getBoardY());
        int endPosition = getBoardStatePositionFromButtonPosition(toButton.getBoardX(), toButton.getBoardY());
        System.out.println("making move from " + startPosition + " to " + endPosition);
        AbstractBoard.MoveEffect effect = boardState.makeMove(new Move(startPosition, endPosition));
        switch (effect){
            case CASTLE_KINGSIDE:
                castleKingside(startPosition);
                moveNormally(toButton, fromButton);
                break;
            case CASTLE_QUEENSIDE:
                castleQueenside(startPosition);
                moveNormally(toButton, fromButton);
                break;
            case QUEENING_PAWN:
                queeningPawn(toButton, fromButton, endPosition);
                break;
            case NONE:
                moveNormally(toButton, fromButton);
                break;
            default:
                throw new RuntimeException("Post move effect not supported: " + effect);
        }
    }

    private void castleKingside(int startPosition){
        int rookPosition;
        int futureRookPosition;
        int offset = 0;
        if (PLAYER_COLOR == pieces.Color.BLACK){
            offset = 1;
        }
        rookPosition = startPosition+3 + offset;
        futureRookPosition = startPosition+1;
        ButtonPiece rook = getButtonFromBoardStatePosition(rookPosition);
        ButtonPiece futureRook = getButtonFromBoardStatePosition(futureRookPosition);
        ImageIcon emptyIcon = chessGraphics.getEmptyIcon();
        futureRook.setIcon(rook.getIcon());
        rook.setIcon(emptyIcon);
    }

    private void castleQueenside(int startPosition){
        int rookPosition;
        int futureRookPosition;
        int offset = 0;
        if (PLAYER_COLOR == pieces.Color.BLACK){
            offset = 1;
        }
        rookPosition = startPosition-4 + offset;
        futureRookPosition = startPosition-1;
        ButtonPiece rook = getButtonFromBoardStatePosition(rookPosition);
        ButtonPiece futureRook = getButtonFromBoardStatePosition(futureRookPosition);
        ImageIcon emptyIcon = chessGraphics.getEmptyIcon();
        futureRook.setIcon(rook.getIcon());
        rook.setIcon(emptyIcon);
    }

    private void queeningPawn(ButtonPiece button, ButtonPiece fromButton, int endPosition){
        ImageIcon currentIcon = chessGraphics.getPieceIcon("Q", boardState.getPieceColor(endPosition));
        fromButton.setIcon(chessGraphics.getEmptyIcon());
        fromButton.setBackground(fromButton.getColor());
        button.setIcon(currentIcon);
    }

    private void moveNormally(ButtonPiece button, ButtonPiece fromButton){
        ImageIcon currentIcon = (ImageIcon)fromButton.getIcon();
        fromButton.setIcon(chessGraphics.getEmptyIcon());
        fromButton.setBackground(fromButton.getColor());
        button.setIcon(currentIcon);
    }

    private void selectPiece(ButtonPiece button){
        if (currentlySelected != null){
            currentlySelected.setBackground(currentlySelected.getColor());
        }
        button.setBackground(Color.YELLOW);
        currentlySelected = button;
        clearMoves();
        displayMoves(button.getPiecePosition());
    }

    private void clearMoves(){
        for (ButtonPiece button : currentValidMoves){
            button.setBackground(button.getColor());
        }
        currentValidMoves.clear();
    }

    private void displayMoves(int piecePosition){
        List<Move> moves = boardState.getValidPieceMoves(piecePosition);
        //System.out.println("number of possible moves: " + moves.size());
        for (Move move : moves){
            if (boardState.isMoveValid(move)){
                ButtonPiece button = getButtonFromBoardStatePosition(move.getEndPosition());
                button.setBackground(Color.GREEN);
                currentValidMoves.add(button);
            }
        }
    }

    private void makeEngineMove(){
        updateEvaluation();
        for (ButtonPiece button : lastComputerMove){
            button.setBackground(button.getColor());
        }
        lastComputerMove.clear();
        try{
            Move move = engine.getBestMove(boardState);
            System.out.println(move.getStartPosition() + " " + move.getEndPosition());
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
        System.out.println("history size: " + boardState.getMoveHistory().size());
        List<Move> moveHistory = boardState.getMoveHistory();
        for (Move move : moveHistory){
            System.out.println("move " + move.getStartPosition() + " to " + move.getEndPosition());
        }
        waitingForComputer = false;
        updateEvaluation();
        MakeEngineThinkInBackground();
    }

    private void MakeEngineThinkInBackground(){
        engine.searchInBackground(boardState);
    }

    private void showWinner(pieces.Color color){
        JOptionPane.showMessageDialog(null, color + " wins!");
        boardState = (AbstractBoard<T>) new BoardState(PLAYER_COLOR);
        initializeGui();
    }

    private void showDraw(){
        JOptionPane.showMessageDialog(null, "Draw!");
        boardState = (AbstractBoard<T>) new BoardState(PLAYER_COLOR);
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
            if (!chessGraphics.isColorPiece(source, PLAYER_COLOR)){
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
            boardState = (AbstractBoard<T>) boardState.getInstance(PLAYER_COLOR);
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
            COLS = new StringBuilder(COLS).reverse().toString();
            ROWS = new StringBuilder(ROWS).reverse().toString();
            engine.setEngineColor(PLAYER_COLOR);
            PLAYER_COLOR = pieces.Color.getOpposite(PLAYER_COLOR);
            boardState = (AbstractBoard<T>) boardState.getInstance(PLAYER_COLOR);
            initializeGui();
        }
    }
}
