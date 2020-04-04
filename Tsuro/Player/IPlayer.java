import com.google.gson.JsonArray;

import java.awt.*;
import java.util.ArrayList;

/**
 * A Player has:
 * * A name (String)
 * * An Avatar color (String)
 *
 * A Player can:
 * * Choose their name (as long as it is not already chosen)
 * * Choose their Avatar color (as long as it is not already chosen)
 * * Get a copy of the Board
 * * Check if it is their turn to play
 * * Check if the Game is over
 * * Check if a Place action for a Tile is legal
 * * Check if a Place action for an Avatar is legal (for their initial move)
 * * Choose between Tiles provided to them to rotate and place on the Board
 * * Rotate a Tile
 * * Place a Tile on the Board (as long as it is legal) and receive confirmation
 *   that the Place Tile happened
 * * Place an Avatar on the Board on a Tile on a Port (as long as it is legal)
 *   and receive confirmation that the Place Avatar happened
 */
public interface IPlayer extends Comparable<IPlayer> {
    /**
     * Gets the name of this Player.
     * @return The name of this Player.
     */
    String getPlayerName();

    void setEliminated();

    void setKicked();
    
    String getName();

    Avatar getAvatar();

    boolean hasExited();

    boolean hasNoMoves(Board board,  Tiles tiles, boolean firstMove);

    boolean hasFullHand();

    ArrayList<Tiles> confiscateTiles();

    void updateBoard(Board b);

    /**
     * Gets the color of this Player's Avatar.
     * @return The color of this Player's Avatar.
     */
    public String getAvatarName();

    /**
     * @return the JSON array representing the initial placement [23, 0, "blue", "E", 0, 0]
     */
    public JsonArray provideInitPlacement();

    /**
     * @return the JSON array representing the intermediate placement ["blue", 0, 0, 0, 1]
     */
    public JsonArray provideInterPlacement();


    public void setAvatar(Avatar avatar);

    public void setPosition(Point newPos);

    public int getAge();

    void setTileHand(ArrayList<Tiles> tileHand);

}
