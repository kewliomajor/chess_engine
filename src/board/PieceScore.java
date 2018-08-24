package board;

public class PieceScore {
    private double middleGameScore = 0;
    private double endGameScore = 0;

    public PieceScore(int middleGameScore, int endGameScore){
        this.middleGameScore = middleGameScore * 0.01;
        this.endGameScore = endGameScore * 0.01;
    }

    public double getMiddleGameScore(){
        return middleGameScore;
    }

    public double getEndGameScore(){
        return endGameScore;
    }
}
