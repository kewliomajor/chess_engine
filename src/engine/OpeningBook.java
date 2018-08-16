package engine;

import board.BoardState;
import pieces.Color;
import pieces.Move;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.Random;

public class OpeningBook extends DefaultTreeModel {

    private int blackKingStartPos;
    private int whiteKingStartPos;
    private int offset = -1;
    protected MoveTreeNode root;

    public OpeningBook(BoardState boardState){
        super(new MoveTreeNode(new Move(0, 0)));
        this.blackKingStartPos = boardState.getBlackKing().getPosition();
        this.whiteKingStartPos = boardState.getWhiteKing().getPosition();
        System.out.println("white king starting at position " + whiteKingStartPos);
        if (boardState.getPlayerColor() == Color.BLACK){
            offset = 1;
        }
        buildBook();
    }

    public Move getNextMove(List<Move> moves){
        return recursiveGetMove(moves, root);
    }

    private Move recursiveGetMove(List<Move> moves, MoveTreeNode node){
        if (node.isLeaf()){
            return null;
        }
        if (moves.size() == 0){
            //get random child
            Random rand = new Random();
            int  i = rand.nextInt(node.getChildCount());
            MoveTreeNode childNode = (MoveTreeNode)node.getChildAt(i);
            return childNode.getMove();
        }
        Move nextMove = moves.get(0);
        for (int i = 0; i < node.getChildCount(); i++){
            MoveTreeNode childNode = (MoveTreeNode)node.getChildAt(i);
            if (childNode.getMove().equals(nextMove)){
                moves.remove(0);
                return recursiveGetMove(moves, childNode);
            }
        }
        //there were no matches
        return null;
    }

    private void buildBook(){
        root = new  MoveTreeNode(new Move(0, 0));
        insertNodeInto(getKingPawnBookTree(), root, 0);
    }

    private MoveTreeNode getKingPawnBookTree(){
        int kingPawnStartPos = whiteKingStartPos+10*offset;
        MoveTreeNode kingPawnMove = new MoveTreeNode(new Move(kingPawnStartPos, kingPawnStartPos+20*offset));

        //sicilian
        MoveTreeNode sicilian = new MoveTreeNode(new Move(kingPawnStartPos+52*offset, kingPawnStartPos+32*offset));
        MoveTreeNode sicilian2 = new MoveTreeNode(new Move(whiteKingStartPos-2*offset, kingPawnStartPos+9*offset));
        insertNodeInto(sicilian, kingPawnMove, 0);
        insertNodeInto(sicilian2, sicilian, 0);
        
        return kingPawnMove;
    }
}
