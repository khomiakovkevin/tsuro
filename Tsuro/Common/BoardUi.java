import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BoardUi extends JComponent {
    private static BufferedImage background;

    static {
        try {
            background = ImageIO.read(new File("../Common/TsuroGameBoard.png"));
          // For testing, use "../Common/TsuroGameBoard.png" for execution in .../TikiTech/Tsuro/6/
          // For programming, use "Tsuro/Common/TsuroGameBoard.png" for execution in .../TikiTech/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Board board;


    public BoardUi(Board board) {
        this.board = board;
    }

    public Dimension getPreferredSize() {
        return new Dimension(1000, 1000);
    }

    /**
    * Paints the Board along with the background
    */
    public void paint(Graphics g) {
        int rad1 = Math.min(getWidth(), getHeight());
        g.drawImage(background, 0, 0, rad1, rad1, null);
        g.setColor(Color.BLACK);

        int rad = Math.min(getWidth(), getHeight());
        int margin = rad / 20;
        rad -= margin;

        for (int row = 9; row >= 0; row--) {
            for (int col = 0; col < 10; col++) {
                int w = rad / 10;
                int left = rad * col / 10 + margin / 2;
                int top = rad * row / 10 + margin / 2;

                Tiles tile = board.getGrid()[col][9 - row];
                if (tile != null)
                    tile.drawTile((Graphics2D) g.create(left, top, w, w), new Dimension(w, w));
            }
        }
    }
}
