package application;

import sun.awt.image.OffScreenImageSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChessGraphics {

    private static final int SIZE = 64;
    private static final BufferedImage SHEET;
    static {
        try {
            SHEET = ImageIO.read(new File("src/resources/pieces.png"));
        } catch (IOException x) {
            throw new UncheckedIOException(x);
        }
    }
    private static final BufferedImage GOLD_KING    = SHEET.getSubimage(0, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_KING  = SHEET.getSubimage(0, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_QUEEN     = SHEET.getSubimage(SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_QUEEN   = SHEET.getSubimage(SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_ROOK     = SHEET.getSubimage(2 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_ROOK   = SHEET.getSubimage(2 * SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_KNIGHT   = SHEET.getSubimage(3 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_KNIGHT = SHEET.getSubimage(3 * SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_BISHOP   = SHEET.getSubimage(4 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_BISHOP = SHEET.getSubimage(4 * SIZE, SIZE, SIZE, SIZE);
    private static final BufferedImage GOLD_PAWN     = SHEET.getSubimage(5 * SIZE, 0,    SIZE, SIZE);
    private static final BufferedImage SILVER_PAWN   = SHEET.getSubimage(5 * SIZE, SIZE, SIZE, SIZE);
    private static final List<BufferedImage> SPRITES =
            Collections.unmodifiableList(Arrays.asList(GOLD_KING,  SILVER_KING,
                    GOLD_QUEEN,   SILVER_QUEEN,
                    GOLD_ROOK,   SILVER_ROOK,
                    GOLD_KNIGHT, SILVER_KNIGHT,
                    GOLD_BISHOP, SILVER_BISHOP,
                    GOLD_PAWN,   SILVER_PAWN));

    public ImageIcon getPieceIcon(String piece, pieces.Color color){
        BufferedImage image = null;
        int offset = 0;
        if (color == pieces.Color.WHITE){
            offset = 1;
        }

        switch (piece) {
            case "E":
                image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
                break;
            case "I":
                throw new RuntimeException("shouldn't have an invalid piece at a valid board position");
            case "P":
                image = SPRITES.get(10 + offset);
                break;
            case "R":
                image = SPRITES.get(4 + offset);
                break;
            case "N":
                image = SPRITES.get(6 + offset);
                break;
            case "B":
                image = SPRITES.get(8 + offset);
                break;
            case "Q":
                image = SPRITES.get(2 + offset);
                break;
            case "K":
                image = SPRITES.get(offset);
                break;
        }

        if (image == null){
            throw new RuntimeException("No valid piece available");
        }

        return new ImageIcon(image);
    }

    public boolean isColorPiece(OffScreenImageSource source, pieces.Color color){
        int offset = 0;
        if (color == pieces.Color.WHITE){
            offset = 1;
        }
        List<Integer> possiblePositions = new ArrayList<Integer>() {{
            add(10);
            add(8);
            add(6);
            add(4);
            add(2);
            add(0);
        }};
        for (Integer pos : possiblePositions){
            if (source.equals(SPRITES.get(pos + offset).getSource())){
                return true;
            }
        }
        return false;
    }

    public ImageIcon getEmptyIcon(){
        return new ImageIcon(new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB));
    }
}
