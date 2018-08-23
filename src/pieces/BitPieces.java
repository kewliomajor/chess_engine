package pieces;

public class BitPieces {
    public static byte EMPTY_PIECE = 0x0;
    public static byte INVALID_PIECE = (byte)255;
    private static byte COLOR_MASK = (byte)128;
    private static byte HAS_MOVED_MASK = 0x8;
    private static byte HAS_CASTLED_MASK = (byte)16;
    private static byte DOUBLE_MOVING_MASK = (byte)32;
    private static byte PIECE_MASK = 0x7;

    public static byte WHITE_PAWN = 0x1;
    public static byte WHITE_ROOK = 0x4;
    public static byte WHITE_KNIGHT = 0x2;
    public static byte WHITE_BISHOP = 0x3;
    public static byte WHITE_QUEEN = 0x5;
    public static byte WHITE_KING = 0x6;

    public static byte BLACK_PAWN = (byte)129;
    public static byte BLACK_ROOK = (byte)132;
    public static byte BLACK_KNIGHT = (byte)130;
    public static byte BLACK_BISHOP = (byte)131;
    public static byte BLACK_QUEEN = (byte)133;
    public static byte BLACK_KING = (byte)134;

    public static boolean pieceHasMoved(byte piece){
        return (piece & HAS_MOVED_MASK) == 0;
    }

    public static byte movePiece(byte piece){
        return (byte)(piece | HAS_MOVED_MASK);
    }

    public static boolean kingHasCastled(byte piece){
        return (piece & HAS_CASTLED_MASK) == 0;
    }

    public static byte castlePiece(byte piece){
        return (byte)(piece | HAS_CASTLED_MASK);
    }

    public static boolean doubleMoving(byte piece){
        return (piece & DOUBLE_MOVING_MASK) == 0;
    }

    public static byte doubleMovePiece(byte piece){
        return (byte)(piece | DOUBLE_MOVING_MASK);
    }

    public static byte unDoubleMovePiece(byte piece){
        return (byte)(piece & (~DOUBLE_MOVING_MASK));
    }

    public static boolean isPieceWhite(byte piece){
        return (piece & COLOR_MASK) == 0;
    }

    public static boolean colorsMatch(byte piece, byte otherPiece){
        return (piece ^ otherPiece ^ COLOR_MASK) != 0;
    }

    public static boolean isPieceKing(byte piece){
        return (piece & PIECE_MASK) == WHITE_KING;
    }

    public static boolean isPieceQueen(byte piece){
        return (piece & PIECE_MASK) == WHITE_QUEEN;
    }

    public static boolean isPieceRook(byte piece){
        return (piece & PIECE_MASK) == WHITE_ROOK;
    }

    public static boolean isPieceKnight(byte piece){
        return (piece & PIECE_MASK) == WHITE_KNIGHT;
    }

    public static boolean isPieceBishop(byte piece){
        return (piece & PIECE_MASK) == WHITE_BISHOP;
    }

    public static boolean isPiecePawn(byte piece){
        return (piece & PIECE_MASK) == WHITE_PAWN;
    }

    private static int getStrippedPiece(byte piece){
        return piece & PIECE_MASK;
    }

    public static byte colorKing(Color color){
        if (color == Color.WHITE){
            return WHITE_KING;
        }
        else if (color == Color.BLACK){
            return BLACK_KING;
        }
        throw new RuntimeException("Color" + color + " is invalid for pieces");
    }

    public static byte colorQueen(Color color){
        if (color == Color.WHITE){
            return WHITE_QUEEN;
        }
        else if (color == Color.BLACK){
            return BLACK_QUEEN;
        }
        throw new RuntimeException("Color" + color + " is invalid for pieces");
    }

    public static byte colorRook(Color color){
        if (color == Color.WHITE){
            return WHITE_ROOK;
        }
        else if (color == Color.BLACK){
            return BLACK_ROOK;
        }
        throw new RuntimeException("Color" + color + " is invalid for pieces");
    }

    public static byte colorBishop(Color color){
        if (color == Color.WHITE){
            return WHITE_BISHOP;
        }
        else if (color == Color.BLACK){
            return BLACK_BISHOP;
        }
        throw new RuntimeException("Color" + color + " is invalid for pieces");
    }

    public static byte colorKnight(Color color){
        if (color == Color.WHITE){
            return WHITE_KNIGHT;
        }
        else if (color == Color.BLACK){
            return BLACK_KNIGHT;
        }
        throw new RuntimeException("Color" + color + " is invalid for pieces");
    }

    public static byte colorPawn(Color color){
        if (color == Color.WHITE){
            return WHITE_PAWN;
        }
        else if (color == Color.BLACK){
            return BLACK_PAWN;
        }
        throw new RuntimeException("Color" + color + " is invalid for pieces");
    }
}
