package pieces;

public enum Color {
	BLACK, WHITE, NONE;

	public static Color getOpposite(Color color){
		if (color == Color.NONE){
			return Color.NONE;
		}
		else if (color == Color.BLACK){
			return Color.WHITE;
		}
		return Color.BLACK;
	}
}
