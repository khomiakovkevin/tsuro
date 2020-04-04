import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;

public class HandPanel extends JComponent {
    
    ArrayList<Tiles> hand;
    int selected;

    public HandPanel(ArrayList<Tiles> hand, int selected){
        this.hand = hand;
        this.selected = selected;
    }

    public Dimension getPreferredSize(){
        return new Dimension(100, 100);
    }

    public Dimension getMinimumSize(){
        return new Dimension(100, 100);
    }


    /**
    * Paints the HandPanel with the Tile options
    */
    public void paint(Graphics g) {
        g.setColor(Color.GRAY);

        for (int col = 0; col < 2; col++) {

            int yPosition = (col + 1)*100;

            Tiles tile = this.hand.get(col);

            tile.drawTile((Graphics2D) g.create(0, yPosition, 100, 100), getPreferredSize());

            // Creates the borders around the tiles
            Graphics2D g2 = (Graphics2D) g;
            float width = 3;
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(width));
            g2.drawRect(0, yPosition, 100, 100);
            g2.setStroke(oldStroke);

            setVisible(true);
        }
        highlight(g);

        setVisible(true);

    }

    /**
    * Highlights the selected Tile from the options provided via the HandPanel
    */
    public void highlight(Graphics g){

        int selectedIdx = this.selected;
        ArrayList<Tiles> tiles = this.hand;
        g.setColor(Color.RED);
        int y = 0;

        for (int ii=0; ii< tiles.size(); ii++){
            if (tiles.get(ii).getIdxInAll35() == selectedIdx){
                y = ii;
            }
        }

        // Creates the red border around the selected tile
        int yPosition = (y + 1)*100;
        Graphics2D g2 = (Graphics2D) g;
        float width = 3;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(width));
        g2.drawRect(0, yPosition, 100, 100);
        g2.setStroke(oldStroke);
    }

}
