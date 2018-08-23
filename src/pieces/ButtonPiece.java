package pieces;

import javax.swing.*;

public class ButtonPiece extends JButton {

    private int piecePosition;
    private int boardX;
    private int boardY;
    private java.awt.Color color;

    public ButtonPiece(int piecePosition, int boardX, int boardY){
        super();
        this.piecePosition = piecePosition;
        this.boardX = boardX;
        this.boardY = boardY;
    }

    public void setPiecePosition(int piecePosition){
        this.piecePosition = piecePosition;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public void setColor(java.awt.Color color){
        this.color = color;
    }

    public java.awt.Color getColor(){
        return color;
    }

    public int getBoardX() {
        return boardX;
    }

    public int getBoardY() {
        return boardY;
    }
}
