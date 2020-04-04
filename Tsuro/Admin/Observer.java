import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Observer
 * <p>
 * Description/Purpose:
 * Observers should be able to "plug" into a Referee Component and "observe"
 * certain aspects of the game. Observers should not be able to modify the
 * Referee or any of the Game Components but should be able to see how the game
 * is progressing.
 * <p>
 * Outline of Functionalities:
 * - View Board State
 * - View Information (Status/Phase/Turns) about the Game.
 * - View Information about the Players (Wins, Name, Color of Avatar)
 * - View which Avatars belong to which Players
 * - If it's a best of x games, view how many games have been played so far
 * <p>
 * Specifications:
 * The Observer class will not have direct access to the Referee Model, rather
 * it will have limited access to the Referee and the components it contains so
 * that there is no modification functionality.
 */

public class Observer extends JFrame {

    Board board;

    public Observer(Board board) {
        this.board = board;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle("Tsuro");


        BoardUi boardUi = new BoardUi(board);

        add(boardUi, BorderLayout.CENTER);
        
        HandPanel tileHand = new HandPanel (board.getRef().getCurOptions(), board.getRef().getCurSelected());
        tileHand.setLayout(new FlowLayout());
        JLabel currentPlayerLabel = new JLabel("Player Turn: " + board.getRef().getCurTurn().getPlayerName());
  
        currentPlayerLabel.setFont(currentPlayerLabel.getFont().deriveFont(24.0f));
  
        add(tileHand, BorderLayout.EAST);
        add(currentPlayerLabel, BorderLayout.PAGE_START);

        pack();
        setVisible(true);
    }


    public static void main(String args[]) {
        Observer o = new Observer(new Board());

        // add the HandPanel
        ArrayList<Tiles> tiles = Tiles.all35Tiles();
        int selected = 0;
        HandPanel handPanel = new HandPanel(tiles, selected);

        o.add(handPanel, BorderLayout.EAST);
    }

}


