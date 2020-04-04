import java.util.ArrayList;

/**
 * A Referee oversees and performs actions for a game of Tsuro. It accepts
 * players until the maximum number of players is reached (in this case, the
 * maximum number of players is 5). The Referee then waits until the runGame()
 * method is called. From there, the placePhase() begins until all primary Tiles
 * have been placed on the Board and all Avatars have been placed on the
 * player's respective port and all of the Players have an Avatar and Tile on
 * the Board. Then, the Referee moves to the turnPhases() that alternates
 * between Players. If a Player performs an illegal action, an error is returned
 * and the referee lets the Player make an action that is legal. At the end of
 * the turnPhase() method, after all Players have placed their Tiles, the
 * Avatars on the Board progress through the specific Port's path until either
 * the end of a Tile is reached or the edge of the Board is reached.
 *
 * If the Referee encounters a Player that has lost (the Player's Avatar has
 * reached the edge of the Board), the Player is no longer able to participate
 * in any more turnPhases() rounds. The Referee also keeps track of what Players
 * are out of the game and what Players are still playing. At the end of the
 * game, th Referee states the winner of the game (if there is any) and asks if
 * the Players of that game want to play another one.
 */
public interface IReferee {

    /**
     * Starts a game. The placePhase is executed until one Tile and one Avatar are
     * placed on the Board, then the game moves to the alternating turnPhase().
     * When the game is over, the method returns nothing. A winner can be found
     * for an individual game using getWinner().
     * If a Player ever provides an invalid move, they must provide a legal move
     * in order to continue with the game. No other Player makes a move until a
     * valid move is made.
     * @throws IllegalStateException If the number of players are less than 3, or
     *                               more than 5.
     */
    public void runGame() throws IllegalStateException;

    /**
     * Moves the Avatar to the edging port of the selected Tile.
     * @param a The avatar
     * @param firstMove Is this the Avatar's first move of the game?
     */
    public void moveAvatar (Avatar a, boolean firstMove);

    /**
     * Returns the current selected Tile from the given tile options
     */
    int getCurSelected();

    /**
     * Returns an ArrayList of Tiles for thePlayer to choose from
     */
    ArrayList<Tiles> getCurOptions();

    /**
     * Returns the Player whose turn it currently is
     */
    IPlayer getCurTurn ();

    /**
     * Returns an ArrayList of active players currently in the game
     */
    public ArrayList<IPlayer> getActive();

    /**
     * Returns Players who got elimintated from the game
     */
    public ArrayList<ArrayList<IPlayer>> getEliminated();

    /**
     * Creates a new Player instance using the given parameters.
     * @param playerName The name of the Player, which must be unique.
     * @param age The age of the player
     * @param avatarColor The color of the Avatar that the Player chooses, which
     *                    must be unique.
     * @throws IllegalArgumentException If playerName or avatarColor is already in
     *                                  use.
     */
    public void addPlayer(String playerName, int age, String avatarColor);

    /**
     * Creates a new Player instance using the given parameters.
     * @param p The Player, which mucts be unique
     */
    void addPlayer (IPlayer p);

    /**
     * Removes a Player from the game because they have lost.
     * @param playerColor The color of the Player to remove.
     */
    public void removePlayer(String playerColor);

    /**
     * Adds a new Observer to this Referee.
     * @param name The String name of the new Observer.
     */
    public void addObserver(String name);

    /**
     * Updates all Observers with either:
     * 1. New updates to the Board
     * 2. New Board requests
     * 3. End of Game messages
     * @param jsonString The updates to be sent to the observers.
     */
    public void updateObserver(String jsonString);

}


