# Plan

## Part 1: Describing the identifiable components of the software system

Actors in the board game: 
- The game board 
- the tiles (that contain 8 ports)
- the players
- the player avatars (representing the players' moves on the board).

The game board is able to know what tiles are placed where. This will allow for the game to look for illegal tile placing (i.e. not placing a tile next to an existing tile or not placing an initial tile at an edge of the game board).
The tiles know how the 8 ports are positioned and how they are connected. They will be able to know where the player avatars are located on their 8 ports. The tiles also know what tiles are next to them and can connect ports of the neighbor tiles accordingly. 
The players know what the tiles look like and can place these tiles where they wish and can additionally place player avatars at their initial position during the start of the game.

The software system requires that we have a GameBoard interface that is a 2D array of 10 x 10 spaces that allow tiles to be placed on them.

We then need a Tile class to represent the tiles to be placed on the GameBoard. A tile will be a square representation with 2 ports on each of its sides. These ports will be connected to one other port on the tile via a simple graph structure. Ports will be classified via "North", "East", "South", and "West" (in reference to the tile) and further classified into "1" or "2", depeding on how east or north the port is.

A Tile will also have a Point object to reference another Tile that is placed next to it in order to connect ports from one tile to the other. The x-value of the Point object will represent the row of the game board and the y-value of the Point object will represent the column of the game board. 

An Avatar will be a class to represent the player avatars that will traverse through the Tiles on the GameBoard via the connected ports. Avatars will need to ask whether it has reached the end of a Tile (cannot go any further) or has reached the end of the GameBoard (tell the Player that they have lost).

The Player will need to receive Tiles when appropriate, choose a Tile, rotate and place the Tile on the GameBoard according to how they want, and place their Avatar on the initial Tile. They will need to keep making decisions related to the game until they have learned they lost the game.

## Part 2: How we plan to proceed about implementing these pieces

We plan to implement these pieces one by one. We will start out by implementing the GameBoard and Tiles. After doing this, we can then allow for Tile to be placed on GameBoard 

* **Step 1** 
  * Implement a Tile Object
    * **Testing**
      * Make sure there are 8 ports that are connected to one another
      * Make sure there are no more than 4 distinct connections allowed
      * Make sure there are 2 ports on each side of the tile
      * Make sure rotation behaviour of the tile works fine

* **Step 2** 
  * Implement a Board Model
    * **Testing**
      * Make sure the board consists of a 2D array of 10 by 10 tiles
      * Make sure the initial tile is along at least one edge of the board
      * Make sure that every tile has a connection to another tile
      
* **Step 3** 
  * Implement an Avatar Object
    * **Testing**
      * No Testing
      
* **Step 4** 
  * Implement a Tsuro Game Model
    * **Testing**
      * Test appropriate move behaviour
      * Test appropriate initial Avatar placement
      * Test win/loss conditions
      * Test appropriate restarting of the game
      
* **Step 5** 
  * Implement a Tsuro Game Controller
    * **Testing**
      * Handling and parsing user commands
      * Test routing of user commands to model interface methods
      * Test appropriate behaviour for invalid commands
      * Test win/loss conditions

* **Step 6** 
  * Implement a Game View
    * **Testing**
      * TBD upon selection of the visual library