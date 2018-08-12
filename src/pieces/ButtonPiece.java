package pieces;

import javax.swing.*;

public class ButtonPiece extends JButton {

    private AbstractPiece piece;

    public ButtonPiece(AbstractPiece piece){
        super();
        this.piece = piece;
    }

    public AbstractPiece getPiece() {
        return piece;
    }
}
