package board;

public class PieceScoreBoard {

    enum PieceType {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    }

    private PieceScore[][] pawnBoard = {
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(-11, 7), p(  6,-4), p(  7, 8), p( 3,-2), p( 3,-2), p(  7, 8), p(  6,-4), p(-11, 7)},
            {p(-18,-4), p( -2,-5), p( 19, 5), p(24, 4),p(24, 4),p( 19, 5),p( -2,-5),p(-18,-4)},
            {p(-17, 3), p( -9, 3), p( 20,-8), p(35,-3),p(35,-3),p( 20,-8),p( -9, 3),p(-17, 3)},
            {p( -6, 8), p(  5, 9), p(  3, 7), p(21,-6),p(21,-6),p(  3, 7),p(  5, 9),p( -6, 8)},
            {p( -6, 8), p( -8,-5), p( -6, 2), p(-2, 4),p(-2, 4),p( -6, 2),p( -8,-5),p( -6, 8)},
            {p( -4, 3), p( 20,-9), p( -8, 1), p(-4,18),p(-4,18),p( -8, 1),p( 20,-9),p( -4, 3)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)}
    };

    private PieceScore[][] knightBoard = {
            {p(-161,-105), p(-96,-82), p(-80,-46), p(-73,-14),p(-73,-14),p(-80,-46),p(-96,-82),p(-161,-105)},
            {p( -83, -69), p(-43,-54), p(-21,-17), p(-10,  9),p(-10,  9),p(-21,-17),p(-43,-54),p( -83, -69)},
            {p( -71, -50), p(-22,-39), p(  0, -7), p(  9, 28),p(  9, 28),p(  0, -7),p(-22,-39),p( -71, -50)},
            {p( -25, -41), p( 18,-25), p( 43,  6), p( 47, 38),p( 47, 38),p( 43,  6),p( 18,-25),p( -25, -41)},
            {p( -26, -46), p( 16,-25), p( 38,  3), p( 50, 40),p( 50, 40),p( 38,  3),p( 16,-25),p( -26, -46)},
            {p( -11, -54), p( 37,-38), p( 56, -7), p( 65, 27),p( 65, 27),p( 56, -7),p( 37,-38),p( -11, -54)},
            {p( -63, -65), p(-19,-50), p(  5,-24), p( 14, 13),p( 14, 13),p(  5,-24),p(-19,-50),p( -63, -65)},
            {p(-195,-109), p(-67,-89), p(-42,-50), p(-29,-13),p(-29,-13),p(-42,-50),p(-67,-89),p(-195,-109)}
    };

    private PieceScore[][] bishopBoard = {
            {p(-44,-58), p(-13,-31), p(-25,-37), p(-34,-19),p(-34,-19),p(-25,-37),p(-13,-31),p(-44,-58)},
            {p(-20,-34), p( 20, -9), p( 12,-14), p(  1,  4),p(  1,  4),p( 12,-14),p( 20, -9),p(-20,-34)},
            {p( -9,-23), p( 27,  0), p( 21, -3), p( 11, 16),p( 11, 16),p( 21, -3),p( 27,  0),p( -9,-23)},
            {p(-11,-26), p( 28, -3), p( 21, -5), p( 10, 16),p( 10, 16),p( 21, -5),p( 28, -3),p(-11,-26)},
            {p(-11,-26), p( 27, -4), p( 16, -7), p(  9, 14),p(  9, 14),p( 16, -7),p( 27, -4),p(-11,-26)},
            {p(-17,-24), p( 16, -2), p( 12,  0), p(  2, 13),p(  2, 13),p( 12,  0),p( 16, -2),p(-17,-24)},
            {p(-23,-34), p( 17,-10), p(  6,-12), p( -2,  6),p( -2,  6),p(  6,-12),p( 17,-10),p(-23,-34)},
            {p(-35,-55), p(-11,-32), p(-19,-36), p(-29,-17),p(-29,-17),p(-19,-36),p(-11,-32),p(-35,-55)}
    };

    private PieceScore[][] rookBoard = {
            {p(-25, 0), p(-16, 0), p(-16, 0), p(-9, 0),p(-9, 0),p(-16, 0),p(-16, 0),p(-25, 0)},
            {p(-21, 0), p( -8, 0), p( -3, 0), p( 0, 0),p( 0, 0),p( -3, 0),p( -8, 0),p( -21, 0)},
            {p(-21, 0), p( -9, 0), p( -4, 0), p( 2, 0),p( 2, 0),p( -4, 0),p( -9, 0),p( -21, 0)},
            {p(-22, 0), p( -6, 0), p( -1, 0), p( 2, 0),p( 2, 0),p( -1, 0),p( -6, 0),p( -22, 0)},
            {p(-22, 0), p( -7, 0), p(  0, 0), p( 1, 0),p( 1, 0),p( 0, 0),p( -7, 0),p( -22, 0)},
            {p(-21, 0), p( -7, 0), p(  0, 0), p( 2, 0),p( 2, 0),p( 0, 0),p( -7, 0),p( -21, 0)},
            {p(-12, 0), p(  4, 0), p(  8, 0), p(12, 0),p( 12, 0),p( 8, 0),p( 4, 0),p( -12, 0)},
            {p(-23, 0), p(-15, 0), p(-11, 0), p(-5, 0),p( -5, 0),p( -11, 0),p( -15, 0),p( -23, 0)}
    };

    private PieceScore[][] queenBoard = {
            {p( 0,-71), p(-4,-56), p(-3,-42), p(-1,-29),p(-1,-29),p(-3,-42),p(-4,-56),p( 0,-71)},
            {p(-4,-56), p( 6,-30), p( 9,-21), p( 8, -5),p( 8, -5),p( 9,-21),p( 6,-30),p(-4,-56)},
            {p(-2,-39), p( 6,-17), p( 9, -8), p( 9,  5),p( 9,  5),p( 9, -8),p( 6,-17),p(-2,-39)},
            {p(-1,-29), p( 8, -5), p(10,  9), p( 7, 19),p( 7, 19),p(10,  9),p( 8, -5),p(-1,-29)},
            {p(-3,-27), p( 9, -5), p( 8, 10), p( 7, 21),p( 7, 21),p( 8, 10),p( 9, -5),p(-3,-27)},
            {p(-2,-40), p( 6,-16), p( 8,-10), p(10,  3),p(10,  3),p( 8,-10),p( 6,-16),p(-2,-40)},
            {p(-2,-55), p( 7,-30), p( 7,-21), p( 6, -6),p( 6, -6),p( 7,-21),p( 7,-30),p(-2,-55)},
            {p(-1,-74), p(-4,-55), p(-1,-43), p( 0,-30),p( 0,-30),p(-1,-43),p(-4,-55),p(-1,-74)}
    };

    private PieceScore[][] kingBoard = {
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)},
            {p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0),p(0,0)}
    };

    public double getScoreOfPiece(PieceType type, int position){
        position -= 20;
        int x = (position%10) -1;
        int y = position/10;

        double score = 0.0;

        switch (type){
            case PAWN:
                score = pawnBoard[x][y].getMiddleGameScore();
                break;
            case KNIGHT:
                score = knightBoard[x][y].getMiddleGameScore();
                break;
            case BISHOP:
                score = bishopBoard[x][y].getMiddleGameScore();
                break;
            case ROOK:
                score = rookBoard[x][y].getMiddleGameScore();
                break;
            case QUEEN:
                score = queenBoard[x][y].getMiddleGameScore();
                break;
            case KING:
                score = kingBoard[x][y].getMiddleGameScore();
                break;
        }

        return score;
    }

    private PieceScore p(int m, int e){
        return new PieceScore(m, e);
    }
}