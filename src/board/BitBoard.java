package board;

import pieces.*;

import java.util.ArrayList;
import java.util.List;

import static pieces.BitPieces.*;

public class BitBoard extends AbstractBoard<BitBoard>{
    private static final int MAX_DOUBLE_MOVING_PAWNS = 2;

    private byte[] board = new byte[BOARD_SIZE];
    private int[] doubleMovingPawns = {0, 0};
    private byte blackKing;
    private byte whiteKing;
    private int whiteKingPosition;
    private int blackKingPosition;

    @Override
    public BitBoard getInstance(AbstractBoard board) {
        return new BitBoard((BitBoard) board);
    }

    /**
     * Creates a board with all pieces in the starting formation
     */
    public BitBoard(Color playerColor){
        moveHistory = new ArrayList<>();
        setupStartingPieces(playerColor);
        this.playerColor = playerColor;
    }


    public BitBoard(BitBoard bitBoard){
        currentMove = bitBoard.getCurrentMoveColor();
        for (int i = 0; i < BOARD_SIZE; i++){
            byte existingPiece = bitBoard.getPiece(i);
            board[i] = existingPiece;
        }
        blackKing = bitBoard.getBlackKing();
        whiteKing = bitBoard.getWhiteKing();
        blackKingPosition = bitBoard.getBlackKingPosition();
        whiteKingPosition = bitBoard.getWhiteKingPosition();
        System.arraycopy(bitBoard.doubleMovingPawns, 0, doubleMovingPawns, 0, bitBoard.doubleMovingPawns.length);
        moveHistory = new ArrayList<>(bitBoard.moveHistory);
        playerColor = bitBoard.playerColor;
    }


    public boolean pieceIsInvalid(int position){
        return board[position] == INVALID_PIECE;
    }


    public boolean pieceIsKing(int position){
        return BitPieces.isPieceKing(board[position]);
    }


    public boolean pieceIsQueen(int position){
        return BitPieces.isPieceQueen(board[position]);
    }


    public Color getPieceColor(int position){
        return BitPieces.isPieceWhite(board[position]) ? Color.WHITE : Color.BLACK;
    }


    public byte[] getBoard(){
        return board;
    }


    public byte getPiece(int i){
        return board[i];
    }


    public byte getPiece(int i, int j){
        int row = (10 * i) + 20;
        j+= 1;
        return board[row + j];
    }

    public byte getWhiteKing(){
        return whiteKing;
    }

    public byte getBlackKing(){
        return blackKing;
    }

    public int getWhiteKingPosition(){
        return whiteKingPosition;
    }

    public int getBlackKingPosition(){
        return blackKingPosition;
    }


    public double getBoardScore(){
        boolean whiteHasMoves = false;
        boolean blackHasMoves = false;
        double boardScore = 0;
        for (int i = 0; i < BOARD_SIZE; i++){
            if (board[i] == INVALID_PIECE || board[i] == EMPTY_PIECE){
                continue;
            }

            byte piece = board[i];
            if (!whiteHasMoves && BitPieces.isPieceWhite(piece) && getValidPieceMoves(i).size() > 0){
                whiteHasMoves = true;
            }
            if (!blackHasMoves && !BitPieces.isPieceWhite(piece) && getValidPieceMoves(i).size() > 0){
                blackHasMoves = true;
            }

            int offset = 1;
            if (BitPieces.isPieceWhite(piece) == (currentMove == Color.BLACK)){
                offset = -1;
            }
            double score = getPieceValue(piece) * offset;
            boardScore += score;
        }
        if (!whiteHasMoves){
            if (currentMove == Color.WHITE){
                return -CHECKMATE_SCORE;
            }
            return CHECKMATE_SCORE;
        }
        else if (!blackHasMoves){
            if (currentMove == Color.BLACK){
                return -CHECKMATE_SCORE;
            }
            return CHECKMATE_SCORE;
        }

        return boardScore;
    }

    private double getPieceValue(byte piece){
        double value;
        if (BitPieces.isPieceKing(piece)){
            value = 0;
        }
        else if (BitPieces.isPieceQueen(piece)){
            value = 9;
        }
        else if (BitPieces.isPieceRook(piece)){
            value = 5;
        }
        else if (BitPieces.isPieceKnight(piece)){
            value = 3;
        }
        else if (BitPieces.isPieceBishop(piece)){
            value = 3;
        }
        else if (BitPieces.isPiecePawn(piece)){
            value = 1;
        }
        else{
            throw new RuntimeException("Can't get the type of the piece: " + piece);
        }

        return value;
    }

    public void move(Move move){
        for (int i = 0; i < MAX_DOUBLE_MOVING_PAWNS; i++){
            int position = doubleMovingPawns[i];
            if (position != 0){
                board[position] = BitPieces.unDoubleMovePiece(board[position]);
            }
        }
        doubleMovingPawns = new int[]{0, 0};
        byte fromPiece = board[move.getStartPosition()];
        //case for castling
        if (BitPieces.isPieceKing(fromPiece) && (move.getStartPosition() + 2 == move.getEndPosition() || move.getStartPosition() -2 == move.getEndPosition())){
            int rookPosition;
            int futureRookPosition;
            int offset = 0;
            if (playerColor == pieces.Color.BLACK){
                offset = 1;
            }
            if (move.getStartPosition() + 2 == move.getEndPosition()){
                rookPosition = move.getStartPosition()+3 + offset;
                futureRookPosition = move.getStartPosition()+1;
            }
            else{
                rookPosition = move.getStartPosition()-4 + offset;
                futureRookPosition = move.getStartPosition()-1;
            }
            board[futureRookPosition] = BitPieces.movePiece(board[rookPosition]);
            board[rookPosition] = EMPTY_PIECE;
            board[move.getStartPosition()] = BitPieces.castlePiece(board[move.getStartPosition()]);
        }
        board[move.getEndPosition()] = BitPieces.movePiece(board[move.getStartPosition()]);
        board[move.getStartPosition()] = EMPTY_PIECE;
        byte piece = board[move.getEndPosition()];
        Color fromColor = BitPieces.isPieceWhite(board[move.getEndPosition()]) ? Color.WHITE : Color.BLACK;
        if (BitPieces.isPiecePawn(piece)){
            int offset = 1;
            if (fromColor == Color.WHITE){
                offset = -1;
            }
            if (playerColor == Color.BLACK){
                offset *= -1;
            }
            if (move.getStartPosition() + 20 * offset == move.getEndPosition()){
                piece = BitPieces.doubleMovePiece(piece);
                board[move.getEndPosition()] = piece;
                doubleMovingPawns[0] = move.getEndPosition();
            }
            if (pawnQueening(piece, move.getEndPosition())){
                byte newQueen = BitPieces.isPieceWhite(piece) ? WHITE_QUEEN : BLACK_QUEEN;
                board[move.getEndPosition()] = newQueen;
            }
        }
        else if (BitPieces.isPieceKing(piece)){
            if (fromColor == Color.WHITE){
                whiteKingPosition = move.getEndPosition();
            }
            else{
                blackKingPosition = move.getEndPosition();
            }
        }
    }


    private boolean pawnQueening(byte pawn, int position){
        if (BitPieces.isPieceWhite(pawn)){
            switch(position){
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                    return true;
            }
        }
        else{
            switch(position){
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                    return true;
            }
        }
        return false;
    }


    public boolean kingInCheck(Color color){
        int targetKing;
        int offset = 1;
        if (color == Color.WHITE){
            targetKing = whiteKingPosition;
            offset = -1;
        }
        else if (color == Color.BLACK){
            targetKing = blackKingPosition;
        }
        else{
            throw new RuntimeException("Trying to determine if King is in check but given bad king color: " + color);
        }
        if (playerColor == Color.BLACK){
            offset *= -1;
        }

        if (pawnChecks(targetKing, offset)){
            return true;
        }
        if (knightChecks(targetKing)){
            return true;
        }
        if (kingChecks(targetKing)){
            return true;
        }
        if (horizontalVerticalChecks(targetKing)){
            return true;
        }
        if (diagonalChecks(targetKing)){
            return true;
        }

        return false;
    }


    private boolean pawnChecks(int targetKing, int offset){
        byte potentialPawn = board[targetKing+11*offset];
        if (BitPieces.isPiecePawn(potentialPawn) && !BitPieces.colorsMatch(board[targetKing], potentialPawn)){
            return true;
        }
        potentialPawn = board[targetKing+9*offset];
        if (BitPieces.isPiecePawn(potentialPawn) && !BitPieces.colorsMatch(board[targetKing], potentialPawn)){
            return true;
        }
        return false;
    }

    private boolean knightChecks(int targetKing){
        int[] possiblePositions = new int[]{21, 19, 12, 8, -8, -12, -19, -21};
        for (int pos : possiblePositions){
            byte potentialKnight = board[targetKing+pos];
            if (BitPieces.isPieceKnight(potentialKnight) && !BitPieces.colorsMatch(board[targetKing], potentialKnight)){
                return true;
            }
        }

        return false;
    }

    private boolean kingChecks(int targetKing){
        int[] possiblePositions = new int[]{9, 10, 11, 1, -1, -9, -10, -11};
        for (int pos : possiblePositions){
            byte potentialKing = board[targetKing+pos];
            if (BitPieces.isPieceKing(potentialKing) && !BitPieces.colorsMatch(board[targetKing], potentialKing)){
                return true;
            }
        }

        return false;
    }

    private boolean horizontalVerticalChecks(int targetKing){
        for (int i = targetKing+10; i < BOARD_SIZE; i+= 10){
            byte potentialRookQueen = board[i];
            if (potentialRookQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialRookQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceRook(potentialRookQueen) || BitPieces.isPieceQueen(potentialRookQueen))){
                return true;
            }
            else if (potentialRookQueen != EMPTY_PIECE){
                break;
            }
        }
        for (int i = targetKing-10; i > 0; i-= 10){
            byte potentialRookQueen = board[i];
            if (potentialRookQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialRookQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceRook(potentialRookQueen) || BitPieces.isPieceQueen(potentialRookQueen))){
                return true;
            }
            else if (potentialRookQueen != EMPTY_PIECE){
                break;
            }
        }
        for (int i = targetKing+1; i < targetKing+8; i++){
            byte potentialRookQueen = board[i];
            if (potentialRookQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialRookQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceRook(potentialRookQueen) || BitPieces.isPieceQueen(potentialRookQueen))){
                return true;
            }
            else if (potentialRookQueen != EMPTY_PIECE){
                break;
            }
        }
        for (int i = targetKing-1; i > targetKing-8; i--){
            byte potentialRookQueen = board[i];
            if (potentialRookQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialRookQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceRook(potentialRookQueen) || BitPieces.isPieceQueen(potentialRookQueen))){
                return true;
            }
            else if (potentialRookQueen != EMPTY_PIECE){
                break;
            }
        }
        return false;
    }

    private boolean diagonalChecks(int targetKing){
        for (int i = targetKing+11; i < BOARD_SIZE; i+= 11){
            byte potentialBishopQueen = board[i];
            if (potentialBishopQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialBishopQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceBishop(potentialBishopQueen) || BitPieces.isPieceQueen(potentialBishopQueen))){
                return true;
            }
            else if (potentialBishopQueen != EMPTY_PIECE){
                break;
            }
        }
        for (int i = targetKing+9; i < BOARD_SIZE; i+= 9){
            byte potentialBishopQueen = board[i];
            if (potentialBishopQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialBishopQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceBishop(potentialBishopQueen) || BitPieces.isPieceQueen(potentialBishopQueen))){
                return true;
            }
            else if (potentialBishopQueen != EMPTY_PIECE){
                break;
            }
        }
        for (int i = targetKing-11; i > 0; i-= 11){
            byte potentialBishopQueen = board[i];
            if (potentialBishopQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialBishopQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceBishop(potentialBishopQueen) || BitPieces.isPieceQueen(potentialBishopQueen))){
                return true;
            }
            else if (potentialBishopQueen != EMPTY_PIECE){
                break;
            }
        }
        for (int i = targetKing-9; i > 0; i-= 9){
            byte potentialBishopQueen = board[i];
            if (potentialBishopQueen == INVALID_PIECE || BitPieces.colorsMatch(potentialBishopQueen, board[targetKing])){
                break;
            }
            if ((BitPieces.isPieceBishop(potentialBishopQueen) || BitPieces.isPieceQueen(potentialBishopQueen))){
                return true;
            }
            else if (potentialBishopQueen != EMPTY_PIECE){
                break;
            }
        }
        return false;
    }


    public List<Move> getAllValidMoves(Color color){
        List<Move> validMoves = new ArrayList<>();
        for (int i = 21; i < 99; i++){
            if (board[i] == EMPTY_PIECE || board[i] == INVALID_PIECE){
                continue;
            }
            if (!BitPieces.isPieceWhite(board[i]) && color == Color.WHITE ||
                    BitPieces.isPieceWhite(board[i]) && color == Color.BLACK){
                continue;
            }
            List<Move> moves = getPieceMoves(i);
            for (Move move : moves){
                if (isMoveValid(move)){
                    validMoves.add(move);
                }
            }
        }
        return validMoves;
    }


    public List<Move> getValidPieceMoves(int position){
        List<Move> validMoves = new ArrayList<>();
        for (Move move : getPieceMoves(position)){
            if (isMoveValid(move)){
                validMoves.add(move);
            }
        }
        if (BitPieces.isPieceKing(board[position]) && !BitPieces.isPieceWhite(board[position])){
            //System.out.println("black king has " + validMoves.size() + " valid moves out of " + getPieceMoves(position).size() + " total moves");
        }
        return validMoves;
    }


    public Object getObject(int position){
        return board[position];
    }


    private List<Move> getPieceMoves(int position){
        List<Move> moves;

        byte piece = board[position];
        if (BitPieces.isPieceKing(piece)){
            moves = getKingMoves(position);
        }
        else if (BitPieces.isPieceQueen(piece)){
            moves = getQueenMoves(position);
        }
        else if (BitPieces.isPieceRook(piece)){
            moves = getRookMoves(position);
        }
        else if (BitPieces.isPieceKnight(piece)){
            moves = getKnightMoves(position);
        }
        else if (BitPieces.isPieceBishop(piece)){
            moves = getBishopMoves(position);
        }
        else if (BitPieces.isPiecePawn(piece)){
            moves = getPawnMoves(position);
        }
        else{
            throw new RuntimeException("Can't get the type of the piece: " + piece);
        }

        return moves;
    }

    private List<Move> getKingMoves(int position){
        List<Move> moves = new ArrayList<>();
        int[] possiblePositions = new int[]{9, 10, 11, 1, -1, -9, -10, -11};
        for (int pos : possiblePositions){
            moves.add(new Move(position, position+pos));
        }
        Color color = BitPieces.isPieceWhite(board[position]) ? Color.WHITE : Color.BLACK;
        if (!kingInCheck(color)){
            int offset = 1;
            if (playerColor == Color.BLACK){
                offset = -1;
            }
            byte piece = board[position + 3 * offset];
            if (!BitPieces.pieceHasMoved(board[position]) && !BitPieces.pieceHasMoved(piece) && BitPieces.isPieceRook(piece)){
                if (board[position + 2 * offset] == EMPTY_PIECE &&
                        board[position + offset] == EMPTY_PIECE){
                    moves.add(new Move(position, position + 2 * offset));
                }
            }
            piece = board[position - 4 * offset];
            if (!BitPieces.pieceHasMoved(board[position]) && !BitPieces.pieceHasMoved(piece) && BitPieces.isPieceRook(piece)){
                if (board[position - 3 * offset] == EMPTY_PIECE &&
                        board[position -2 * offset] == EMPTY_PIECE &&
                        board[position - offset] == EMPTY_PIECE){
                    moves.add(new Move(position, position - 2 * offset));
                }
            }
        }
        return moves;
    }

    private List<Move> getQueenMoves(int position){
        List<Move> moves = new ArrayList<>();
        moves.addAll(getHorizontalVerticalMoves(position));
        moves.addAll(getDiagonalMoves(position));
        return moves;
    }

    private List<Move> getRookMoves(int position){
        return getHorizontalVerticalMoves(position);
    }

    private List<Move> getKnightMoves(int position){
        List<Move> moves = new ArrayList<>();
        int[] possiblePositions = new int[]{21, 19, 12, 8, -8, -12, -19, -21};
        for (int pos : possiblePositions){
            moves.add(new Move(position, position+pos));
        }
        return moves;
    }

    private List<Move> getBishopMoves(int position){
        return getDiagonalMoves(position);
    }

    private List<Move> getHorizontalVerticalMoves(int position){
        List<Move> moves = new ArrayList<>();
        moves.addAll(addMovesGreaterThan(position, position+10, BoardState.BOARD_SIZE, 10));
        moves.addAll(addMovesLessThan(position,position-10, 0, 10));
        moves.addAll(addMovesGreaterThan(position, position+1, position+8, 1));
        moves.addAll(addMovesLessThan(position, position-1, position-8, 1));
        return moves;
    }

    private List<Move> getDiagonalMoves(int position){
        List<Move> moves = new ArrayList<>();
        moves.addAll(addMovesGreaterThan(position, position+11, BoardState.BOARD_SIZE, 11));
        moves.addAll(addMovesGreaterThan(position, position+9, BoardState.BOARD_SIZE, 9));
        moves.addAll(addMovesLessThan(position, position-11, 0, 11));
        moves.addAll(addMovesLessThan(position, position-9, 0, 9));
        return moves;
    }

    private List<Move> addMovesGreaterThan(int position, int start, int limit, int increment){
        List<Move> moves = new ArrayList<>();
        for (int i = start; i < limit; i+=increment){
            byte piece = board[i];
            if (piece == INVALID_PIECE){
                break;
            }
            else if (piece == EMPTY_PIECE){
                moves.add(new Move(position, i));
            }
            else if (BitPieces.colorsMatch(piece, board[position])){
                break;
            }
            else {
                moves.add(new Move(position, i));
                break;
            }
        }
        return moves;
    }

    private List<Move> addMovesLessThan(int position, int start, int limit, int increment){
        List<Move> moves = new ArrayList<>();
        for (int i = start; i > limit; i-=increment){
            byte piece = board[i];
            if (piece == INVALID_PIECE){
                break;
            }
            else if (piece == EMPTY_PIECE){
                moves.add(new Move(position, i));
            }
            else if (BitPieces.colorsMatch(piece, board[position])){
                break;
            }
            else {
                moves.add(new Move(position, i));
                break;
            }
        }
        return moves;
    }

    private List<Move> getPawnMoves(int position){
        Color color = BitPieces.isPieceWhite(board[position]) ? Color.WHITE : Color.BLACK;
        int offset = 1;
        if (color == Color.WHITE){
            offset = -1;
        }
        if (playerColor == Color.BLACK){
            offset *= -1;
        }
        List<Move> moves = new ArrayList<>();
        moves.add(new Move(position, position + 10 * offset));
        if (!BitPieces.pieceHasMoved(board[position])){
            moves.add(new Move(position, position + 20 * offset));
        }
        moves.add(new Move(position, position + 11 * offset));
        moves.add(new Move(position, position + 9 * offset));
        return moves;
    }


    public boolean isMoveValid(Move move){
        byte fromPiece = board[move.getStartPosition()];
        byte toPiece = board[move.getEndPosition()];
        if (toPiece == INVALID_PIECE){
            return false;
        }
        else if (toPiece != EMPTY_PIECE && BitPieces.colorsMatch(fromPiece, toPiece)){
            return false;
        }
        else if (!isSpecificPieceMoveValid(move.getStartPosition(), move.getEndPosition())){
            return false;
        }

        BitBoard afterMoveBoard = new BitBoard(this);
        afterMoveBoard.makeMove(move);
        return !afterMoveBoard.kingInCheck(currentMove);
    }

    private boolean isSpecificPieceMoveValid(int from, int to){
        if (BitPieces.isPiecePawn(board[from])){
            if (!isPawnMoveValid(from, to)){
                return false;
            }
        }
        else if (BitPieces.isPieceKing(board[from])){
            if (!isKingMoveValid(from, to)){
                return false;
            }
        }
        else{
            if (!isGenericMoveValid(from, to)){
                return false;
            }
        }
        return true;
    }


    private boolean isPawnMoveValid(int fromPiece, int toPiece){
        Color fromColor = BitPieces.isPieceWhite(board[fromPiece]) ? Color.WHITE : Color.BLACK;
        int offset = 1;
        if (fromColor == Color.WHITE){
            offset = -1;
        }
        if (playerColor == Color.BLACK){
            offset *= -1;
        }
        //forward one
        if (fromPiece + 10 * offset == toPiece){
            return board[toPiece] == EMPTY_PIECE;
        }
        //forward two
        else if (fromPiece + 20 * offset == toPiece){
            if (board[fromPiece + 10 * offset] == EMPTY_PIECE){
                return board[toPiece] == EMPTY_PIECE && !BitPieces.pieceHasMoved(board[fromPiece]);
            }
            return false;
        }
        //capturing
        else{
            if (board[toPiece] != EMPTY_PIECE && board[toPiece] != INVALID_PIECE && !BitPieces.colorsMatch(board[fromPiece], board[toPiece])){
                return true;
            }
            //rest is en passant rules
            else if (fromPiece + 9 * offset == toPiece){
                byte potentialPawn = board[fromPiece - offset];
                if (BitPieces.isPiecePawn(potentialPawn) && !BitPieces.colorsMatch(potentialPawn, board[fromPiece])){
                    return (doubleMovingPawns[0] == (fromPiece - offset));
                }
            }
            else if (fromPiece + 11 * offset == toPiece){
                byte potentialPawn = board[fromPiece+ offset];
                if (BitPieces.isPiecePawn(potentialPawn) && !BitPieces.colorsMatch(potentialPawn, board[fromPiece])){
                    return (doubleMovingPawns[0] == (fromPiece + offset));
                }
            }
        }
        return false;
    }


    private boolean isKingMoveValid(int fromPiece, int toPiece){
        //case for castling
        Move move;
        BitBoard afterMoveBoard;
        if (fromPiece+2 == toPiece){
            afterMoveBoard = new BitBoard(this);
            move = new Move(fromPiece, fromPiece+1);
            afterMoveBoard.makeMove(move);
            if (afterMoveBoard.kingInCheck(currentMove)){
                return false;
            }
        }
        else if (fromPiece-2 == toPiece){
            afterMoveBoard = new BitBoard(this);
            move = new Move(fromPiece, fromPiece-1);
            afterMoveBoard.makeMove(move);
            if (afterMoveBoard.kingInCheck(currentMove)){
                return false;
            }
        }
        if (board[toPiece] == EMPTY_PIECE || !BitPieces.colorsMatch(board[fromPiece], board[toPiece])){
            return true;
        }
        return false;
    }


    private boolean isGenericMoveValid(int fromPiece, int toPiece){
        if (board[toPiece] == INVALID_PIECE){
            return false;
        }
        else if (board[toPiece] == EMPTY_PIECE){
            return true;
        }
        return !BitPieces.colorsMatch(board[fromPiece], board[toPiece]);
    }


    public String toString(){
        String boardString = "|";

        for (int i = 0; i < BOARD_SIZE; i++){
            boardString += getPieceString(board[i]) + "|";
            int digit = i % 10;
            if (digit == 9 && i != BOARD_SIZE-1){
                boardString += "\n|";
            }
        }

        return boardString;
    }


    public String getPieceString(int position){
        return getPieceString(board[position]);
    }


    private String getPieceString(byte piece){
        String pieceString = "";

        if (piece == EMPTY_PIECE){
            pieceString = " E ";
        }
        else if (piece == INVALID_PIECE){
            pieceString = " I ";
        }
        else if (BitPieces.isPiecePawn(piece)){
            pieceString = " P ";
        }
        else if (BitPieces.isPieceRook(piece)){
            pieceString = " R ";
        }
        else if (BitPieces.isPieceKnight(piece)){
            pieceString = " N ";
        }
        else if (BitPieces.isPieceBishop(piece)){
            pieceString = " B ";
        }
        else if (BitPieces.isPieceQueen(piece)){
            pieceString = " Q ";
        }
        else if (BitPieces.isPieceKing(piece)){
            pieceString = " K ";
        }


        return pieceString;
    }


    private void setupStartingPieces(Color playerColor){
        for (int i = 0; i < BOARD_SIZE; i++){
            int digit = i % 10;
            if (i > 20 && i < 99 && digit != 0 && digit != 9){
                board[i] = getPieceForPosition(i, playerColor);
            }
            else{
                board[i] = INVALID_PIECE;
            }
        }
    }

    private byte getPieceForPosition(int position, Color playerColor){
        Color engineColor = Color.getOpposite(playerColor);
        switch (position){
            case 21:
                return BitPieces.colorRook(engineColor);
            case 22:
                return BitPieces.colorKnight(engineColor);
            case 23:
                return BitPieces.colorBishop(engineColor);
            case 24:
                if (engineColor == Color.BLACK){
                    return BitPieces.colorQueen(engineColor);
                }
                else{
                    return createKing(engineColor, position);
                }
            case 25:
                if (engineColor == Color.BLACK){
                    return createKing(engineColor, position);
                }
                else{
                    return BitPieces.colorQueen(engineColor);
                }
            case 26:
                return BitPieces.colorBishop(engineColor);
            case 27:
                return BitPieces.colorKnight(engineColor);
            case 28:
                return BitPieces.colorRook(engineColor);
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
                return BitPieces.colorPawn(engineColor);
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
                return BitPieces.colorPawn(playerColor);
            case 91:
                return BitPieces.colorRook(playerColor);
            case 92:
                return BitPieces.colorKnight(playerColor);
            case 93:
                return BitPieces.colorBishop(playerColor);
            case 94:
                if (playerColor == Color.WHITE){
                    return BitPieces.colorQueen(playerColor);
                }
                else{
                    return createKing(playerColor, position);
                }
            case 95:
                if (playerColor == Color.WHITE){
                    return createKing(playerColor, position);
                }
                else{
                    return BitPieces.colorQueen(playerColor);
                }
            case 96:
                return BitPieces.colorBishop(playerColor);
            case 97:
                return BitPieces.colorKnight(playerColor);
            case 98:
                return BitPieces.colorRook(playerColor);
            default:
                return EMPTY_PIECE;
        }
    }

    private byte createKing(Color color, int position){
        byte king;
        if (color == Color.BLACK){
            king = BLACK_KING;
            blackKing = king;
            blackKingPosition = position;
        }
        else{
            king = WHITE_KING;
            whiteKing = king;
            whiteKingPosition = position;
        }
        return king;
    }
}
