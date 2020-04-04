# Data representation of the game board in Tsuro

### Descriptions of all actors
The game board is able to know what tiles are placed where. This will allow for the game to look for illegal tile placing (i.e. not placing a tile next to an existing tile or not placing an initial tile at an edge of the game board). The tiles know how the 8 ports are positioned and how they are connected. They will be able to know where the player avatars are located on their 8 ports. The tiles also know what tiles are next to them and can connect ports of the neighbor tiles accordingly. The players know what the tiles look like and can place these tiles where they wish and can additionally place player avatars at their initial position during the start of the game. The referee takes note of all illegal moves and directs them to the appropriate action. In addition, the referee keeps track of how many players are still in the game and who have lost. It will also decide whether or not the game has ended with a winner or has ended in a tie and will prompt the start of a new game.

The software system requires that we have a GameBoard interface that is a 2D array of 10 x 10 spaces that allow tiles to be placed on them.

We then need a Tile class to represent the tiles to be placed on the GameBoard. A tile will be a square representation with 2 ports on each of its sides. These ports will be connected to one other port on the tile via a simple graph structure. Ports will be classified via "North", "East", "South", and "West" (in reference to the tile) and further classified into "1" or "2", depending on how east or north the port is. A Tile will also have a Point object to reference another Tile that is placed next to it in order to connect ports from one tile to the other. The x-value of the Point object will represent the row of the game board and the y-value of the Point object will represent the column of the game board. 

An Avatar will be a class to represent the player avatars that will traverse through the Tiles on the GameBoard via the connected ports. Avatars will need to ask whether it has reached the end of a Tile (cannot go any further) or has reached the end of the GameBoard (tell the Player that they have lost).

The Player will need to receive Tiles when appropriate, choose a Tile, rotate and place the Tile on the GameBoard according to how they want, and place their Avatar on the initial Tile. They will need to keep making decisions related to the game until they have learned they lost the game.

### Data Definitions

An ITile is an interface with the following receivers:
 - getPosition() -> Position: Returns a Position struct representing the coordinate pair position of this ITile
 - createTile() -> void: Creates a new Tile

An IAvatar is an interface with the following receivers:
 - getPosition() -> Position: Returns a Position struct representing the coordinate pair position of this IAvatar
 - move() -> void: Moves the IAvatar's position by changing the ITile the IAvatar is located on by following the current tile's path of the IAvatar. If there is no ITile in front of the current ITile that the IAvatar is on, then the position of the IAvatar doesn't change. 
 - createAvatar(player int) -> void: Creates a new Avatar given the player's unique code (given as an integer)

A Board is an interface with the following receivers:
 - getTileAt(Position) -> ITile: Returns the ITile on the Board at the given coordinate pair position. If there is no ITile at the position or the coordinate pair is outside of the Board, it will return an error.
 - getAvatarAt(Position) -> list[IAvatar]: Returns the list of IAvatar on the Board at the given coordinate pair position. This is because there may be more than one IAvatar on an ITile. If there is no IAvatar at the position it will return an empty list. If the coordinate pair is outside the board, it will return an error.
 - addTile(ITile, Position) -> void: Attempts to place an ITile in the given coordinate pair position. If this move is not possible given the circumstances (it is a player's first move and they do not place the tile at an edge, there is already an ITile in that position, the coordinate pair is not on the game board), then it will return an error
 - moveAvatar(IAvatar, Position) -> void: Attempts to place an IAvatar in the given coordinate pair position and at a certain end of the ITile that corresponds to the path the IAvatar is following. If this move is not possible, then it will return an error. If the IAvatar has reached the edge of the Board, then the Referee lets the Player know that they have lost the game and are unable to make any more moves.
 - placeAvatar(Position, player int) -> void: Attempts to assign a position to the given player's IAvatar. If this is an illegal positions, it will return an error.
 - getDimensions() -> int, int: Returns the height and width (as a number of ITiles) for the Board.