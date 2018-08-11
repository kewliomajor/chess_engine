package pieces;

public enum Color {
	BLACK, WHITE;

	public static Color getOpposite(Color color){
		if (color == Color.BLACK){
			return Color.WHITE;
		}
		return Color.BLACK;
	}
}
