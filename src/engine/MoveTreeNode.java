package engine;

import pieces.Move;

import javax.swing.tree.DefaultMutableTreeNode;

public class MoveTreeNode extends DefaultMutableTreeNode {

    private Move move;

    public MoveTreeNode(Move move){
        this.move = move;
    }

    public Move getMove(){
        return move;
    }
}
