import javax.swing.*;
import java.awt.*;

public class DrawATile extends JComponent {
    Tiles aTile = new Tiles("S2", "E1",
            "S1", "N1",
            "E2", "W2",
            "N2", "W1", 2);

    public void paintComponent(Graphics g) {
        aTile.drawTile(g, new Dimension(300, 300));
    }

    public static void main(String[] args) throws Exception {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, 300, 300);
        window.getContentPane().add(new DrawATile());
        window.setVisible(true);

    }
}
