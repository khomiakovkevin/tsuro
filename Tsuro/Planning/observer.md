# Observer

Description/Purpose:
Observers should be able to "plug" into a Referee Component and "observe" certain aspects of the game. Observers should not be able to modify the Referee or any of the Game Components but should be able to see how the game is progressing.

Outline of Functionalities:
- View Board State
- View Information (Status/Phase/Turns) about the Game.
- View Information about the Players (Wins, Name, Color of Avatar)
- View which Avatars belong to which Players
- If it's a best of x games, view how many games have been played so far

Specifications:
The Observer class will not have direct access to the Referee Model, rather it will have limited access to the Referee and the components it contains so that there is no modification functionality.

```
public interface IObserver {
```

The append() function allows for the Observer to add updates about the current game. This adds information to the History Builder, which includes all updates from the beginning of the game, and to the newUpdates Builder, which only includes the current updates since the last time updates were asked.

```
  /**
   * Appends new updates to both the overall history builder, as well as the newUpdates builder.
   * @param updates new Updates to add.
   */
  void append(String updates)
```

From the append() function, outputs all new updates that haven't been read since the last time updates have been asked for.

```
/**
   * Returns all new updates since the last read, and clears the newUpdates Builder.
   * @return String representation of new updates since last read.
   */
  String readNewUpdates();
```

From the append() function, outputs all recorded updates from the beginning of the game.

```
  /**
   * Returns the overall history of the game.
   * @return String representation of all game updates.
   */
  String getHistory();
```

Returns the name of the observer.

```
  /**
   * Getter for this Observer's Name
   * @return This Observer's Name
   */
  String getName();
```

The function that returns a rendering of the current stage of the game, including the tiles and their placements, the players' avatar locations, and the board in general.

```
  /**
  * Returns the board state
  * @return String representation of the current board state
  */
  String getBoardState();
```