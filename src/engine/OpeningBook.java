package engine;

import board.AbstractBoard;
import pieces.Color;
import pieces.Move;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.Random;

public class OpeningBook extends DefaultTreeModel {

    private int blackKingStartPos;
    private int whiteKingStartPos;
    private int offset = -1;
    private MoveTreeNode root;

    //known moves
    //white
    private Move c4;
    private Move d4;
    private Move e4;
    private Move we5;
    private Move Nc3;
    private Move Nf3;

    //black
    private Move b6;
    private Move c5;
    private Move c6;
    private Move d5;
    private Move d6;
    private Move e5;
    private Move e6;
    private Move Nc6;
    private Move Nf6;
    private Move Bb7;
    private Move Bf5;

    public OpeningBook(AbstractBoard boardState){
        super(new MoveTreeNode(new Move(0, 0)));
        this.blackKingStartPos = boardState.getBlackKingPosition();
        this.whiteKingStartPos = boardState.getWhiteKingPosition();
        System.out.println("white king starting at position " + whiteKingStartPos);
        if (boardState.getPlayerColor() == Color.BLACK){
            offset = 1;
        }
        createKnownMoves();
        buildBook();
    }

    private void createKnownMoves(){
        //white
        c4 = new Move(whiteKingStartPos+12*offset, whiteKingStartPos+32*offset);
        d4 = new Move(whiteKingStartPos+11*offset, whiteKingStartPos+31*offset);
        e4 = new Move(whiteKingStartPos+10*offset, whiteKingStartPos+30*offset);
        we5 = new Move(whiteKingStartPos+30*offset, whiteKingStartPos+40*offset);
        Nc3 = new Move(whiteKingStartPos+3*offset, whiteKingStartPos+22*offset);
        Nf3 = new Move(whiteKingStartPos-2*offset, whiteKingStartPos+19*offset);
        //black
        b6 = new Move(whiteKingStartPos+63*offset, whiteKingStartPos+53*offset);
        c5 = new Move(whiteKingStartPos+62*offset, whiteKingStartPos+42*offset);
        c6 = new Move(whiteKingStartPos+62*offset, whiteKingStartPos+52*offset);
        d5 = new Move(whiteKingStartPos+61*offset, whiteKingStartPos+41*offset);
        d6 = new Move(whiteKingStartPos+61*offset, whiteKingStartPos+51*offset);
        e5 = new Move(whiteKingStartPos+60*offset, whiteKingStartPos+40*offset);
        e6 = new Move(whiteKingStartPos+60*offset, whiteKingStartPos+50*offset);
        Nc6 = new Move(whiteKingStartPos+73*offset, whiteKingStartPos+52*offset);
        Nf6 = new Move(whiteKingStartPos+68*offset, whiteKingStartPos+49*offset);
        Bb7 = new Move(whiteKingStartPos+72*offset, whiteKingStartPos+63*offset);
        Bf5 = new Move(whiteKingStartPos+72*offset, whiteKingStartPos+39*offset);
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
        MoveTreeNode kingPawnMove = new MoveTreeNode(e4);

        insertNodeInto(getKingPawnSymmetricBookTree(), kingPawnMove, 0);
        insertNodeInto(getSicilianBookTree(), kingPawnMove, 0);
        insertNodeInto(getFrenchBookTree(), kingPawnMove, 0);
        insertNodeInto(getCaroKannBookTree(), kingPawnMove, 0);

        return kingPawnMove;
    }

    private MoveTreeNode getKingPawnSymmetricBookTree(){
        MoveTreeNode symmetric = new MoveTreeNode(e5);
        MoveTreeNode symmetricNf3 = new MoveTreeNode(Nf3);
        MoveTreeNode symmetricNf3Nc6 = new MoveTreeNode(Nc6);

        insertNodeInto(symmetricNf3, symmetric, 0);
        insertNodeInto(symmetricNf3Nc6, symmetricNf3, 0);

        return symmetric;
    }

    private MoveTreeNode getSicilianBookTree(){
        MoveTreeNode sicilian = new MoveTreeNode(c5);
        MoveTreeNode sicilianNf3 = new MoveTreeNode(Nf3);
        MoveTreeNode sicilianNf3d6 = new MoveTreeNode(d6);

        insertNodeInto(sicilianNf3, sicilian, 0);
        insertNodeInto(sicilianNf3d6, sicilianNf3, 0);

        return sicilian;
    }

    private MoveTreeNode getFrenchBookTree(){
        MoveTreeNode french = new MoveTreeNode(e6);
        MoveTreeNode frenchd4 = new MoveTreeNode(d4);
        MoveTreeNode frenchd4d5 = new MoveTreeNode(d5);

        insertNodeInto(frenchd4, french, 0);
        insertNodeInto(frenchd4d5, frenchd4, 0);

        return french;
    }

    private MoveTreeNode getCaroKannBookTree(){
        MoveTreeNode carokann = new MoveTreeNode(c6);
        MoveTreeNode carokannd4 = new MoveTreeNode(d4);
        MoveTreeNode carokannd4d5 = new MoveTreeNode(d5);
        MoveTreeNode carokannd4d5e5 = new MoveTreeNode(we5);
        MoveTreeNode carokannd4d5e5Bf5 = new MoveTreeNode(Bf5);

        insertNodeInto(carokannd4, carokann, 0);
        insertNodeInto(carokannd4d5, carokannd4, 0);
        insertNodeInto(carokannd4d5e5, carokannd4d5, 0);
        insertNodeInto(carokannd4d5e5Bf5, carokannd4d5e5, 0);

        return carokann;
    }

    private MoveTreeNode getQueenPawnBookTree(){
        MoveTreeNode queenPawnMove = new MoveTreeNode(d4);

        insertNodeInto(getQGDBookTree(), queenPawnMove, 0);
        insertNodeInto(getQEnglishDefenseBookTree(), queenPawnMove, 0);

        return queenPawnMove;
    }

    private MoveTreeNode getQGDBookTree(){
        MoveTreeNode symmetrical = new MoveTreeNode(d5);
        MoveTreeNode qgd = new MoveTreeNode(c4);
        MoveTreeNode qgdc6 = new MoveTreeNode(c6);
        MoveTreeNode qgde6 = new MoveTreeNode(e6);
        insertNodeInto(qgd, symmetrical, 0);
        insertNodeInto(qgdc6, qgd, 0);
        insertNodeInto(qgde6, qgd, 0);
        insertNodeInto(new MoveTreeNode(Nc3), qgdc6, 0);
        insertNodeInto(new MoveTreeNode(Nc3), qgde6, 0);

        return symmetrical;
    }

    private MoveTreeNode getQEnglishDefenseBookTree(){
        //b6
        MoveTreeNode englishDefense = new MoveTreeNode(b6);
        MoveTreeNode englishDefensec4 = new MoveTreeNode(c4);
        MoveTreeNode englishDefensec4Bb7 = new MoveTreeNode(Bb7);
        insertNodeInto(englishDefensec4, englishDefense, 0);
        insertNodeInto(englishDefensec4Bb7, englishDefensec4, 0);

        return englishDefense;
    }

    private MoveTreeNode getRetiTree(){
        MoveTreeNode retiKnightMove = new MoveTreeNode(Nf3);

        //queen pawn
        MoveTreeNode queenPawn = new MoveTreeNode(d5);
        MoveTreeNode queenPawnd4 = new MoveTreeNode(d4);
        MoveTreeNode queenPawnd4c6 = new MoveTreeNode(c6);
        insertNodeInto(queenPawn, retiKnightMove, 0);
        insertNodeInto(queenPawnd4, queenPawn, 0);
        insertNodeInto(queenPawnd4c6, queenPawnd4, 0);

        return retiKnightMove;
    }

    private MoveTreeNode getEnglishTree(){
        MoveTreeNode englishMove = new MoveTreeNode(c4);

        //king pawn
        MoveTreeNode kingPawn = new MoveTreeNode(e5);
        MoveTreeNode kingPawnNc3 = new MoveTreeNode(Nc3);
        MoveTreeNode kingPawnNc3Nf6 = new MoveTreeNode(Nf6);
        insertNodeInto(kingPawn, englishMove, 0);
        insertNodeInto(kingPawnNc3, kingPawn, 0);
        insertNodeInto(kingPawnNc3Nf6, kingPawnNc3, 0);

        //symmetrical
        MoveTreeNode symmetrical = new MoveTreeNode(c5);
        MoveTreeNode symmetricalNf3 = new MoveTreeNode(Nf3);
        MoveTreeNode symmetricalNf3Nc6 = new MoveTreeNode(Nc6);
        insertNodeInto(symmetrical, englishMove, 0);
        insertNodeInto(symmetricalNf3, symmetrical, 0);
        insertNodeInto(symmetricalNf3Nc6, symmetricalNf3, 0);

        return englishMove;
    }
}
