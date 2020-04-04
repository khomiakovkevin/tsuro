import java.awt.*;
import java.util.ArrayList;

public class MockReferee extends Referee {

    String curPlayer;
    ArrayList<Tiles> curOptions;
    int selected;

    /**
     * Returns the current Player in the Game.
     */
    public String getCurPlayer() {
        return curPlayer;
    }

    /**
     * Returns the array of Tiles available to the Player as options.
     */
    public ArrayList<Tiles> getCurOptions() {
        return curOptions;
    }
    
    /**
     * Returns the selected Tile from the options available.
     */
    public int getSelected() {
        return selected;
    }

    
    /**
     * Sets the current options of tiles available to the player.
     * @param newOptions The list of Tiles that are now available to choose from
     */
    public void setCurOptions(ArrayList<Tiles> newOptions) {
        ArrayList<Tiles> copyOfnewOptions = new ArrayList<>();
        for (Tiles t: newOptions) {
            Tiles copy = Tiles.all35Tiles().get(t.getIdxInAll35()).clone();
            copyOfnewOptions.add(copy);
        }
        this.curOptions = copyOfnewOptions;
    }

    public MockReferee () {
        super(10, 10);
        super.connectToBoard();
    }

    
    /**
     * Processes the selected Tile, location of the Tile, and the rotation for the initial placement
     * @param row The location (row) of the Tile on the Board
     * @param col The location (column) of the Tile on the Board
     * @param selected The Tile selected from the options
     * @param color The color of the Avatar that the Player chooses, which
     *                    must be unique.
     * @param options The list of Tiles given as options for the Player
     *                    to choose from
     * @param port The port that the Avatar will start at
     */
    public void processInitPlacement(int row, int col, Tiles selected, String color, ArrayList<Tiles> options, Port.PortName port) {
        super.addPlayer(color);
        Point pos = new Point(col, row);
        super.getPlayer(color).setTileHand(options);
        this.selected = selected.getIdxInAll35();
        setCurOptions(options);
        if (Rules.isInitialPlacementLegal(this.board, pos,
                port, selected)) {
            IPlayer player = super.getPlayer(color);
            this.board.initialPlacement(pos.x, pos.y, selected, port, player.getAvatar());
            player.getAvatar().updatePosition(pos.x, pos.y, port);
            this.moveAvatar(player.getAvatar(), true);
        }
    }

    /**
     * Processes the selected Tile, location of the Tile, and the rotation for an intermediate placement
     * @param row The location (row) of the Tile on the Board
     * @param col The location (column) of the Tile on the Board
     * @param selected The Tile selected from the options
     * @param player The Player who made the placement
     * @param options The list of Tiles given as options for the Player
     *                    to choose from
     */
    public void processIntermediatePlacement(int row, int col, Tiles selected, IPlayer player, ArrayList<Tiles> options){
        Point pos = new Point(col, row);
        setCurOptions(options);
        Avatar avatar = player.getAvatar();
        String curPlayer = super.getPlayer(avatar.getColor().toString().toLowerCase()).getName();
        this.curPlayer = curPlayer;
        if (Rules.isIntermediatePlacementLegal(super.board, col, row, selected, player)) {
            this.board.placeTile(pos.x, pos.y, selected, avatar, false);
            super.advancePlayers();
        }
    }
}
