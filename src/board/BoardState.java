package board;

import pieces.*;

import java.util.ArrayList;
import java.util.List;

public class BoardState extends AbstractBoard<BoardState>{

	private AbstractPiece[] board;
	private List<Pawn> doubleMovingPawns = new ArrayList<>();
	private AbstractPiece blackKing;
	private AbstractPiece whiteKing;

	@Override
	public BoardState getInstance(AbstractBoard board) {
		return new BoardState((BoardState) board);
	}

	@Override
	public BoardState getInstance(Color playerColor) {
		return new BoardState(playerColor);
	}
	
	/**
	 * Creates a board with all pieces in the starting formation
	 */
	public BoardState(Color playerColor){
		board = new AbstractPiece[BOARD_SIZE];
		moveHistory = new ArrayList<>();
		setupStartingPieces(playerColor);
		this.playerColor = playerColor;
	}


	public BoardState(BoardState boardState){
		currentMove = boardState.getCurrentMoveColor();
		board = new AbstractPiece[BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; i++){
			AbstractPiece existingPiece = boardState.getBoard()[i];
			if (existingPiece instanceof InvalidPiece){
				board[i] = InvalidPiece.getInstance();
			}
			else if (existingPiece instanceof EmptyPiece){
				board[i] = new EmptyPiece(i);
			}
			else if (existingPiece instanceof Pawn){
				board[i] = new Pawn((Pawn)existingPiece);
			}
			else if (existingPiece instanceof Rook){
				board[i] = new Rook((Rook)existingPiece);
			}
			else if (existingPiece instanceof Knight){
				board[i] = new Knight((Knight)existingPiece);
			}
			else if (existingPiece instanceof Bishop){
				board[i] = new Bishop((Bishop)existingPiece);
			}
			else if (existingPiece instanceof Queen){
				board[i] = new Queen((Queen)existingPiece);
			}
			else if (existingPiece instanceof King){
				King king = new King((King)existingPiece);
				if (existingPiece.getColor() == Color.BLACK){
					blackKing = king;
				}
				else if (existingPiece.getColor() == Color.WHITE){
					whiteKing = king;
				}
				else{
					throw new RuntimeException("King has an impossible color: " + existingPiece.getColor());
				}
				board[i] = king;
			}
		}
		doubleMovingPawns = new ArrayList<>(boardState.doubleMovingPawns);
		moveHistory = new ArrayList<>(boardState.moveHistory);
		playerColor = boardState.playerColor;
	}
	
	
	public AbstractPiece[] getBoard(){
		return board;
	}

	public boolean pieceIsInvalid(int position){
		return board[position] instanceof InvalidPiece;
	}

	public Color getPieceColor(int position){
		return board[position].getColor();
	}


	public List<Move> getValidPieceMoves(int position){
		List<Move> validMoves = new ArrayList<>();
		for (Move move : board[position].getAllValidMoves(this)){
			if (isMoveValid(move)){
				validMoves.add(move);
			}
		}
		return validMoves;
	}


	public Object getObject(int position){
		return board[position];
	}


	public AbstractPiece getPiece(int i, int j){
		int row = (10 * i) + 20;
		j+= 1;
		return board[row + j];
	}


	public Color getCurrentMoveColor(){
		return currentMove;
	}

	public King getWhiteKing(){
		return (King)whiteKing;
	}

	public King getBlackKing(){
		return (King)blackKing;
	}


	public double getBoardScore(){
		boolean whiteHasMoves = false;
		boolean blackHasMoves = false;
		double boardScore = 0;
		for (int i = 0; i < BOARD_SIZE; i++){
			if (board[i] instanceof InvalidPiece || board[i] instanceof EmptyPiece){
				continue;
			}

			AbstractPiece piece = board[i];
			if (!whiteHasMoves && piece.getColor() == Color.WHITE){
				if (piece.getAllValidMoves(this).size() > 0){
					whiteHasMoves = true;
				}
			}
			else if (!blackHasMoves && piece.getColor() == Color.BLACK){
				if (piece.getAllValidMoves(this).size() > 0){
					blackHasMoves = true;
				}
			}

			int offset = 1;
			if (board[i].getColor() != currentMove){
				offset = -1;
			}
			double score = board[i].getBaseValue() * offset;
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

	@Override
	public int getBlackKingPosition() {
		return blackKing.getPosition();
	}

	@Override
	public int getWhiteKingPosition() {
		return whiteKing.getPosition();
	}

	public MoveEffect move(Move move){
		MoveEffect effect = MoveEffect.NONE;
		for (Pawn pawn : doubleMovingPawns){
			pawn.setDoubleMove(false);
		}
		doubleMovingPawns.clear();
		AbstractPiece fromPiece = board[move.getStartPosition()];
		//case for castling
		if (fromPiece instanceof King && (move.getStartPosition() + 2 == move.getEndPosition() || move.getStartPosition() -2 == move.getEndPosition())){
			EmptyPiece rookEmptyPiece;
			int rookPosition;
			int futureRookPosition;
			int offset = 0;
			if (playerColor == pieces.Color.BLACK){
				offset = 1;
			}
			if (move.getStartPosition() + 2 == move.getEndPosition()){
				effect = MoveEffect.CASTLE_KINGSIDE;
				rookPosition = move.getStartPosition()+3 + offset;
				futureRookPosition = move.getStartPosition()+1;
			}
			else{
				effect = MoveEffect.CASTLE_QUEENSIDE;
				rookPosition = move.getStartPosition()-4 + offset;
				futureRookPosition = move.getStartPosition()-1;
			}
			rookEmptyPiece = new EmptyPiece(rookPosition);
			board[futureRookPosition] = board[rookPosition];
			board[futureRookPosition].move(new Move(rookPosition, futureRookPosition));
			board[rookPosition] = rookEmptyPiece;
		}
		board[move.getEndPosition()] = board[move.getStartPosition()];
		board[move.getStartPosition()] = new EmptyPiece(move.getStartPosition());
		AbstractPiece piece = board[move.getEndPosition()];
		piece.move(move);
		if (piece instanceof Pawn){
			if (((Pawn)piece).isDoubleMoving()){
				doubleMovingPawns.add((Pawn)piece);
			}
			if (pawnQueening((Pawn)piece)){
				effect = MoveEffect.QUEENING_PAWN;
				board[piece.getPosition()] = new Queen(piece.getColor(), piece.getPosition());
			}
			//rest is en passant rules
			int offset = 1;
			if (fromPiece.getColor() == Color.WHITE){
				offset = -1;
			}
			if (playerColor == Color.BLACK){
				offset *= -1;
			}
			else if (fromPiece.getPosition() + 9 * offset == move.getEndPosition()){
				AbstractPiece potentialPawn = board[fromPiece.getPosition() - offset];
				if (potentialPawn instanceof Pawn){
					board[potentialPawn.getPosition()] = new EmptyPiece(potentialPawn.getPosition());
				}
			}
			else if (fromPiece.getPosition() + 11 * offset == move.getEndPosition()){
				AbstractPiece potentialPawn = board[fromPiece.getPosition()+ offset];
				if (potentialPawn instanceof Pawn){
					board[potentialPawn.getPosition()] = new EmptyPiece(potentialPawn.getPosition());
				}
			}
		}
		return effect;
	}


	private boolean pawnQueening(Pawn pawn){
		if (pawn.getColor() == Color.BLACK){
			switch(pawn.getPosition()){
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
		else if (pawn.getColor() == Color.WHITE){
			switch(pawn.getPosition()){
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
		else{
			throw new RuntimeException("Pawn has an invalid color " + pawn.getColor());
		}
		return false;
	}


	public List<Move> getAllValidMoves(Color color){
		List<Move> validMoves = new ArrayList<>();
		for (int i = 21; i < 99; i++){
			if (board[i].getColor() != color){
				continue;
			}
			List<Move> moves = board[i].getMoves(this);
			for (Move move : moves){
				if (isMoveValid(move)){
					validMoves.add(move);
				}
			}
		}
		return validMoves;
	}


	public boolean isMoveValid(Move move){
		AbstractPiece fromPiece = board[move.getStartPosition()];
		AbstractPiece toPiece = board[move.getEndPosition()];
		if (toPiece instanceof InvalidPiece){
			return false;
		}
		else if (fromPiece.getColor() == toPiece.getColor()){
			return false;
		}
		//now the actual logic for individual pieces
		if (!isSpecificPieceMoveValid(fromPiece, toPiece)){
			return false;
		}

		BoardState afterMoveBoard = new BoardState(this);
		afterMoveBoard.makeMove(move);
		return !afterMoveBoard.kingInCheck(fromPiece.getColor());
	}

	@Override
	public int getGraphicalXFromPosition(int position) {
		position -= 20;
		int x = (position%10) -1;

		return x;
	}

	@Override
	public int getGraphicalYFromPosition(int position) {
		position -= 20;
		int y = position/10;

		return y;
	}

	@Override
	public int getBoardStatePositionFromGraphicalPosition(int x, int y) {
		return 20 + ((y * 10) + x + 1);
	}

	private boolean isSpecificPieceMoveValid(AbstractPiece fromPiece, AbstractPiece toPiece){
		if (fromPiece instanceof Pawn){
			if (!isPawnMoveValid((Pawn)fromPiece, toPiece)){
				return false;
			}
		}
		else if (fromPiece instanceof King){
			if (!isKingMoveValid((King)fromPiece, toPiece)){
				return false;
			}
		}
		return true;
	}


	public boolean kingInCheck(Color color){
		AbstractPiece targetKing;
		if (color == Color.WHITE){
			targetKing = whiteKing;
		}
		else if (color == Color.BLACK){
			targetKing = blackKing;
		}
		else{
			throw new RuntimeException("Trying to determine if King is in check but given bad king color: " + color);
		}

		if (pawnChecks(targetKing)){
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


	private boolean pawnChecks(AbstractPiece targetKing){
		int offset = 1;
		if (targetKing.getColor() == Color.WHITE){
			offset = -1;
		}
		if (playerColor == Color.BLACK){
			offset *= -1;
		}
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		AbstractPiece potentialPawn = board[targetKing.getPosition()+11*offset];
		if (potentialPawn instanceof Pawn && potentialPawn.getColor() == oppositeColor){
			return true;
		}
		potentialPawn = board[targetKing.getPosition()+9*offset];
		if (potentialPawn instanceof Pawn && potentialPawn.getColor() == oppositeColor){
			return true;
		}
		return false;
	}

	private boolean knightChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		List<Integer> possiblePositions = new ArrayList<Integer>() {{
			add(21);
			add(19);
			add(12);
			add(8);
			add(-8);
			add(-12);
			add(-19);
			add(-21);
		}};
		for (Integer pos : possiblePositions){
			AbstractPiece potentialKnight = board[targetKing.getPosition()+pos];
			if (potentialKnight instanceof Knight && potentialKnight.getColor() == oppositeColor){
				return true;
			}
		}

		return false;
	}

	private boolean kingChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		List<Integer> possiblePositions = new ArrayList<Integer>() {{
			add(9);
			add(10);
			add(11);
			add(1);
			add(-1);
			add(-9);
			add(-10);
			add(-11);
		}};
		for (Integer pos : possiblePositions){
			AbstractPiece potentialKing = board[targetKing.getPosition()+pos];
			if (potentialKing instanceof King && potentialKing.getColor() == oppositeColor){
				return true;
			}
		}

		return false;
	}

	private boolean horizontalVerticalChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		for (int i = targetKing.getPosition()+10; i < BOARD_SIZE; i+= 10){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-10; i > 0; i-= 10){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()+1; i < targetKing.getPosition()+8; i++){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-1; i > targetKing.getPosition()-8; i--){
			AbstractPiece potentialRookQueen = board[i];
			if (potentialRookQueen instanceof InvalidPiece || potentialRookQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialRookQueen instanceof Rook || potentialRookQueen instanceof Queen) && potentialRookQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialRookQueen instanceof EmptyPiece)){
				break;
			}
		}
		return false;
	}

	private boolean diagonalChecks(AbstractPiece targetKing){
		Color oppositeColor = Color.getOpposite(targetKing.getColor());
		for (int i = targetKing.getPosition()+11; i < BOARD_SIZE; i+= 11){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()+9; i < BOARD_SIZE; i+= 9){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-11; i > 0; i-= 11){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		for (int i = targetKing.getPosition()-9; i > 0; i-= 9){
			AbstractPiece potentialBishopQueen = board[i];
			if (potentialBishopQueen instanceof InvalidPiece || potentialBishopQueen.getColor() == targetKing.getColor()){
				break;
			}
			if ((potentialBishopQueen instanceof Bishop || potentialBishopQueen instanceof Queen) && potentialBishopQueen.getColor() == oppositeColor){
				return true;
			}
			else if (!(potentialBishopQueen instanceof EmptyPiece)){
				break;
			}
		}
		return false;
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


	private boolean isBasicMoveValid(AbstractPiece fromPiece, AbstractPiece toPiece){
		if (toPiece instanceof EmptyPiece || fromPiece.getColor() == Color.getOpposite(toPiece.getColor())){
			return true;
		}
		return false;
	}


	private boolean isPawnMoveValid(Pawn fromPiece, AbstractPiece toPiece){
		int offset = 1;
		if (fromPiece.getColor() == Color.WHITE){
			offset = -1;
		}
		if (playerColor == Color.BLACK){
			offset *= -1;
		}
		//forward one
		if (fromPiece.getPosition() + 10 * offset == toPiece.getPosition()){
			return toPiece instanceof EmptyPiece;
		}
		//forward two
		else if (fromPiece.getPosition() + 20 * offset == toPiece.getPosition()){
			if (board[fromPiece.getPosition() + 10 * offset] instanceof EmptyPiece){
				return toPiece instanceof EmptyPiece && !fromPiece.hasMoved();
			}
			return false;
		}
		//capturing
		else{
			if (fromPiece.getColor() == Color.getOpposite(toPiece.getColor())){
				return true;
			}
			//rest is en passant rules
			else if (fromPiece.getPosition() + 9 * offset == toPiece.getPosition()){
				AbstractPiece potentialPawn = board[fromPiece.getPosition() - offset];
				if (potentialPawn instanceof Pawn){
					return (doubleMovingPawns.contains(potentialPawn) && fromPiece.getColor() == Color.getOpposite(potentialPawn.getColor()));
				}
			}
			else if (fromPiece.getPosition() + 11 * offset == toPiece.getPosition()){
				AbstractPiece potentialPawn = board[fromPiece.getPosition()+ offset];
				if (potentialPawn instanceof Pawn){
					return (doubleMovingPawns.contains(potentialPawn) && fromPiece.getColor() == Color.getOpposite(potentialPawn.getColor()));
				}
			}
		}
		return false;
	}


	private boolean isKingMoveValid(King fromPiece, AbstractPiece toPiece){
		//case for castling
		Move move;
		BoardState afterMoveBoard;
		if (fromPiece.getPosition()+2 == toPiece.getPosition()){
			afterMoveBoard = new BoardState(this);
			move = new Move(fromPiece.getPosition(), fromPiece.getPosition()+1);
			afterMoveBoard.makeMove(move);
			if (afterMoveBoard.kingInCheck(fromPiece.getColor())){
				return false;
			}
		}
		else if (fromPiece.getPosition()-2 == toPiece.getPosition()){
			afterMoveBoard = new BoardState(this);
			move = new Move(fromPiece.getPosition(), fromPiece.getPosition()-1);
			afterMoveBoard.makeMove(move);
			if (afterMoveBoard.kingInCheck(fromPiece.getColor())){
				return false;
			}
		}
		if (toPiece instanceof EmptyPiece || fromPiece.getColor() == Color.getOpposite(toPiece.getColor())){
			return true;
		}
		return false;
	}


	public String getPieceString(int position){
		return getPieceString(board[position]);
	}


	private String getPieceString(AbstractPiece piece){
		String pieceString = "";

		if (piece == null){
			pieceString = " N ";
		}
		else{
			if (piece instanceof EmptyPiece){
				pieceString = " E ";
			}
			else if (piece instanceof InvalidPiece){
				pieceString = " I ";
			}
			else if (piece instanceof Pawn){
				pieceString = " P ";
			}
			else if (piece instanceof Rook){
				pieceString = " R ";
			}
			else if (piece instanceof Knight){
				pieceString = " N ";
			}
			else if (piece instanceof Bishop){
				pieceString = " B ";
			}
			else if (piece instanceof Queen){
				pieceString = " Q ";
			}
			else if (piece instanceof King){
				pieceString = " K ";
			}
		}

		return pieceString;
	}


	private void setupDebugPosition(Color playerColor){
		for (int i = 0; i < BOARD_SIZE; i++){
			int digit = i % 10;
			if (i > 20 && i < 99 && digit != 0 && digit != 9){
				board[i] = getDebugPieceForPosition(i, playerColor);
			}
			else{
				board[i] = InvalidPiece.getInstance();
			}
		}
	}


	private AbstractPiece getDebugPieceForPosition(int position, Color playerColor){
		Color engineColor = Color.getOpposite(playerColor);
		switch (position){
			case 25:
				return new Pawn(engineColor, position);
			case 44:
				return new Pawn(playerColor, position);
			default:
				return new EmptyPiece(position);
		}
	}
	
	
	private void setupStartingPieces(Color playerColor){
		for (int i = 0; i < BOARD_SIZE; i++){
			int digit = i % 10;
			if (i > 20 && i < 99 && digit != 0 && digit != 9){
				board[i] = getPieceForPosition(i, playerColor);
			}
			else{
				board[i] = InvalidPiece.getInstance();				
			}
		}
	}

	private AbstractPiece getPieceForPosition(int position, Color playerColor){
		Color engineColor = Color.getOpposite(playerColor);
		//TODO swap king and queen positions when colors changed
		switch (position){
			case 21:
				return new Rook(engineColor, position);
			case 22:
				return new Knight(engineColor, position);
			case 23:
				return new Bishop(engineColor, position);
			case 24:
				if (engineColor == Color.BLACK){
					return new Queen(engineColor, position);
				}
				else{
					return createKing(engineColor, position);
				}
			case 25:
				if (engineColor == Color.BLACK){
					return createKing(engineColor, position);
				}
				else{
					return new Queen(engineColor, position);
				}
			case 26:
				return new Bishop(engineColor, position);
			case 27:
				return new Knight(engineColor, position);
			case 28:
				return new Rook(engineColor, position);
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
				return new Pawn(engineColor, position);
			case 81:
			case 82:
			case 83:
			case 84:
			case 85:
			case 86:
			case 87:
			case 88:
				return new Pawn(playerColor, position);
			case 91:
				return new Rook(playerColor, position);
			case 92:
				return new Knight(playerColor, position);
			case 93:
				return new Bishop(playerColor, position);
			case 94:
				if (playerColor == Color.WHITE){
					return new Queen(playerColor, position);
				}
				else{
					return createKing(playerColor, position);
				}
			case 95:
				if (playerColor == Color.WHITE){
					return createKing(playerColor, position);
				}
				else{
					return new Queen(playerColor, position);
				}
			case 96:
				return new Bishop(playerColor, position);
			case 97:
				return new Knight(playerColor, position);
			case 98:
				return new Rook(playerColor, position);
			default:
				return new EmptyPiece(position);
		}
	}

	private King createKing(Color color, int position){
		King king = new King(color, position);
		if (color == Color.BLACK){
			blackKing = king;
		}
		else{
			whiteKing = king;
		}
		return king;
	}
}
