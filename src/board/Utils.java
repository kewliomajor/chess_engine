package board;

public class Utils {
	public static boolean isPositionLegal(int position){
		if (position % 10 >= 1 && position % 10 <= 8){
			if (position >= 21 && position <= 98){
				return true;
			}
		}
		return false;
	}
}
