package pieces;

import javax.swing.*;

public class ButtonPiece extends JButton {

    private AbstractPiece piece;
    private java.awt.Color color;

    public ButtonPiece(AbstractPiece piece){
        super();
        this.piece = piece;
    }

    public void setPiece(AbstractPiece piece){
        this.piece = piece;
    }

    public AbstractPiece getPiece() {
        return piece;
    }

    public void setColor(java.awt.Color color){
        this.color = color;
    }

    public java.awt.Color getColor(){
        return color;
    }
}
