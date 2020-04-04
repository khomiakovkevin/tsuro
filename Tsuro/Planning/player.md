# Player

A Player has:
* A name (String)
* An Avatar color (String)

A Player can:
* Choose their name (as long as it is not already chosen)
* Choose their Avatar color (as long as it is not already chosen)
* Get a copy of the Board
* Check if it is their turn to play
* Check if the Game is over
* Check if a Place action for a Tile is legal
* Check if a Place action for an Avatar is legal (for their initial move)
* Choose between Tiles provided to them to rotate and place on the Board
* Rotate a Tile
* Place a Tile on the Board (as long as it is legal) and receive confirmation that the Place Tile happened
* Place an Avatar on the Board on a Tile on a Port (as long as it is legal) and receive confirmation that the Place Avatar happened

```
public interface Player {
```

```
  /**
   * Get the name of this Player
   * @return The name of this Player.
   */
  String getPlayerName();
```

```
  /**
   * Get the color of this Player's Avatar
   * @return The color of this Player's Avatar
   */
  String getAvatarName();
```

```
  /**
   * Place the Tile on the Board after rotating it
   * @param degrees The degrees that the Tile will be rotated: 90, 180, 270
   * @param position The position on the Board that the Tile will be placed on
   * @return A JSON message stating confirmation that the Tile has been placed
   * returns an error if the initial Tile is not at an edge of the Board, or if
   * the posiion is located outside of the Board
   */
  JSON placeTile(String degrees, ArrayList<Position> position);
```

```
  /**
   * Place the Avatar on the Board, on a Tile, and on a specific Port
   * @param position The position on the Board where there is a Tile
   * @param port The Port of the Tile that the Avatar will be placed on
   * @return A JSON message stating confirmation that the Avatar has been placed
   * returns an Error if there is no Tile at the given position on the Board, or
   * if the given Tile is not what the Player placed, or if the given Port is not
   * at the edge of the Board
   */
  JSON placeAvatar(ArrayList<Position> position, String port);
```

```
  /**
   * Produce the name of the Players in the game and still not disqualified, or the 
   * winner, or nothing
   * @return A JSON message stating the current players in the Game, the winner of
   * the game, or nothing if there are no winners
   */
  JSON receiveGameResults();
```