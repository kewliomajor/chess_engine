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
                return recursiveGetMove(moves.subList(1, moves.size()), childNode);
            }
        }
        //there were no matches
        return null;
    }

    private void buildBook(){
        root = new  MoveTreeNode(new Move(0, 0));
        insertNodeInto(getKingPawnBookTree(), root, 0);
        insertNodeInto(getQueenPawnBookTree(), root, 0);
        insertNodeInto(getRetiTree(), root, 0);
        insertNodeInto(getEnglishTree(), root, 0);
    }

    private MoveTreeNode getKingPawnBookTree(){
        int kingPawnStartPos = whiteKingStartPos+10*offset;
        MoveTreeNode kingPawnMove = new MoveTreeNode(new Move(kingPawnStartPos, kingPawnStartPos+20*offset));

        insertNodeInto(getSicilianBookTree(), kingPawnMove, 0);

        return kingPawnMove;
    }

    private MoveTreeNode getSicilianBookTree(){
        MoveTreeNode sicilian = new MoveTreeNode(new Move(whiteKingStartPos+62*offset, whiteKingStartPos+42*offset));
        MoveTreeNode sicilianNf3 = new MoveTreeNode(new Move(whiteKingStartPos-2*offset, whiteKingStartPos+19*offset));
        MoveTreeNode sicilianNf3d6 = new MoveTreeNode(new Move(whiteKingStartPos+61*offset, whiteKingStartPos+51*offset));
        insertNodeInto(sicilianNf3, sicilian, 0);
        insertNodeInto(sicilianNf3d6, sicilianNf3, 0);

        return sicilian;
    }

    private MoveTreeNode getQueenPawnBookTree(){
        int queenPawnStartPos = whiteKingStartPos+11*offset;
        MoveTreeNode queenPawnMove = new MoveTreeNode(new Move(queenPawnStartPos, queenPawnStartPos+20*offset));

        insertNodeInto(getQGDBookTree(queenPawnStartPos), queenPawnMove, 0);

        return queenPawnMove;
    }

    private MoveTreeNode getQGDBookTree(int queenPawnStartPos){
        MoveTreeNode symmetrical = new MoveTreeNode(new Move(queenPawnStartPos+50*offset, queenPawnStartPos+30*offset));
        MoveTreeNode qgd = new MoveTreeNode(new Move(queenPawnStartPos+offset, queenPawnStartPos+21*offset));
        MoveTreeNode qgdc6 = new MoveTreeNode(new Move(queenPawnStartPos+51*offset, queenPawnStartPos+41*offset));
        insertNodeInto(qgd, symmetrical, 0);
        insertNodeInto(qgdc6, qgd, 0);

        return symmetrical;
    }

    private MoveTreeNode getRetiTree(){
        MoveTreeNode retiKnightMove = new MoveTreeNode(new Move(whiteKingStartPos-2*offset, whiteKingStartPos+19*offset));

        //queen pawn
        MoveTreeNode queenPawn = new MoveTreeNode(new Move(whiteKingStartPos+61*offset, whiteKingStartPos+41*offset));
        MoveTreeNode queenPawnd4 = new MoveTreeNode(new Move(whiteKingStartPos+11*offset, whiteKingStartPos+31*offset));
        MoveTreeNode queenPawnd4c6 = new MoveTreeNode(new Move(whiteKingStartPos+62*offset, whiteKingStartPos+52*offset));
        insertNodeInto(queenPawn, retiKnightMove, 0);
        insertNodeInto(queenPawnd4, queenPawn, 0);
        insertNodeInto(queenPawnd4c6, queenPawnd4, 0);

        return retiKnightMove;
    }

    private MoveTreeNode getEnglishTree(){
        int cPawnStartPos = whiteKingStartPos+12*offset;
        MoveTreeNode englishMove = new MoveTreeNode(new Move(cPawnStartPos, cPawnStartPos+20*offset));

        //king pawn
        MoveTreeNode kingPawn = new MoveTreeNode(new Move(cPawnStartPos+48*offset, cPawnStartPos+28*offset));
        MoveTreeNode kingPawnNc3 = new MoveTreeNode(new Move(cPawnStartPos-9*offset, cPawnStartPos+10*offset));
        MoveTreeNode kingPawnNc3Nf6 = new MoveTreeNode(new Move(cPawnStartPos+56*offset, cPawnStartPos+37*offset));
        insertNodeInto(kingPawn, englishMove, 0);
        insertNodeInto(kingPawnNc3, kingPawn, 0);
        insertNodeInto(kingPawnNc3Nf6, kingPawnNc3, 0);

        return englishMove;
    }
}
