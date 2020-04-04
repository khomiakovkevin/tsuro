# Referee

A Referee oversees and performs actions for a game of Tsuro. It accepts players until the maximum number of players is reached (in this case, the maximum number of players is 5). The Referee then waits until the runGame() method is called. From there, the placePhase() begins until all primary Tiles have been placed on the Board and all Avatars have been placed on the player's respectie port and all of the Players have an Avatar and Tile on the Board. Then, the Referee moves to the turnPhases() that alternates between Players. If a Player performs an illegal action, an error is returned and the referee lets the Player make an action that is legal. At the end of the turnPhase() method, after all Players have placed their Tiles, the Avatars on the Board progress through the specific Port's path until either the end of a Tile is reached or the edge of the Board is reached.

If the Referee encounters a Player that has lost (the Player's Avatar has reached the edge of the Board), the Player is no longer able to participate in any more turnPhases() rounds. The Referee also keeps track of what Players are out of the game and what Players are still playing. At the end of the game, th Referee states the winner of the game (if there is any) and asks if the Players of that game want to play another one.

```
public interface Referee
```

```
   /**
   * Runs a game n amount of times and determines the winners based on the Player who won each game (if any)
   * @param numGames number of games to play
   * @return ArrayList<Player> that won each game
   * @throws IllegalArgumentException when the given n is 0
   */
  ArrayList<Player> runGame(int numGames) throws IllegalArgumentException;
```

```
   /**
   * Starts a game. The placePhase is executed until one Tile and one Avatar are placed on the Board,
   * then the game moves to the alternating turnPhase(). When the game is over, the method returns nothing. 
   * A winner can be found for an individual game using getWinner(). 
   * If a Player ever provides an invalid move, they must provide a legal move in order to continue with the 
   * game. No other Player makes a move until a valid move is made.
   * @throws IllegalStateException if the number of players are less than 3 or more than 5
   */
  Player runGame() throws IllegalStateException;
```

```
   /**
   * Creates a new Player instance using the given parameters.
   * @param playerName Name of the Player. Must be unique.
   * @param avatarColor Color of the Avatar that the Player chooses. Must be unique.
   * @throws IllegalArgumentException if playerName or avatarColor is already in use
   */
  void addPlayer(String playerName, String avatarColor) throws IllegalArgumentException;
```

```
   /**
   * Removes a Player from the game because they have lost.
   * @param playerName Name of the Player to remove.
   */
  void removePlayer(String playerName);
```

```
   /**
   * Adds a new Observer to this Referee
   * @param name String name of the new Observer
   */
  void addObserver(String name);
```

```
  /**
   * Updates all Observers with either:
   * 1. new updates to the Board
   * 2. new Board requests
   * 3. End of Game messages
   * @param jsonString Updates to be sent to observers
   */
  void updateObserver(String jsonString);
```