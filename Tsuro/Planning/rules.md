# Rules - the Rule Checker component

The rule checker component will decide the legality of player actions and provide an implementation of the rules of the game.
The referee component will serve as a supervisor of Tsuro games, implementing the mechanics of running a game.
- Design an interface for a rule checker for Tsuro
- provide operations for checking legality of tile placements in all phases of the game
- include what information a rule checker will need to perform these operations

The referee must use a rule checker. 
A player may use it to check if its move is allowed, or it may decide to take its chances and play any which way it wants.
_____

We will implement the rule checker component with a class RuleChecker. The RuleChecker is accessible to both the Referee and the Players for checking the basic rules of the game. Both the Referee and the Players can use the RuleChecker to check for the validity of an Action at any time without mutating the state of the Board.

An Action is one of:
- Place Tile on Board
- Rotate Tile (before placing on Board)
- Place Avatar on a specific Port on a specific Tile
- Move

#### Tsuro is governed by the following basic rules:
On a Player's turn:
- During the first round, the Player must place their Tile on the Board's edge.
- During the first round, the Player must place their Avatar on a Port located on the initial Tile's edge(s).
- Throughout the game, the Player must place Tiles and take into account the progression of their Avatar throughout the Board

A Rule Checker can:
- Check if a Place action for a Tile is legal
- Check if a Place action for a Port is legal
- Check if the game is over


The following is a method checking if placing a Tile is legal.

    /**
    * Checks if a new Tile can be placed at (x,y). This requires that:
    *   - the (x,y) position is on the Board
    *   - the (x,y) position is not already occupied
    *
    * @param b Board that the new Tile will be placed on
    * @param x x Position the new Tile will be placed on
    * @param y y Position the new Tile will be placed on
    * @return True if the place is legal, False if it is not
    */
    boolean isPlaceTileLegal(Board b, int x, int y);
    
The following is a method checking if placing an Avatar is legal.


    /**
    * Checks if an Avatar can be placed at (x,y) at port z. This requires that:
    *   - the (x,y) position is on the Board
    *   - the (x,y) position is occupied by a Tile
    *   - the Tile has been placed by the current Player
    *   - the p position is along the edge of the Board
    *
    * @param b Board that the new Tile will be placed on
    * @param x x Position the new Tile will be placed on
    * @param y y Position the new Tile will be placed on
    * @param p p Port.portname used for initial placement
    * @param a a Avatar to be placed
    * @return True if the place is legal, False if it is not
    */
    boolean isPlaceAvatarLegal(Board b, int row, int col, Port p, Avatar a);
    
The following is a method checking if the game is over.

    /**
    * Check if the game is over
    *    The game is over when:
    *      - a Player’s Avatar is the last one standing on the Board and all other Avatars have reached edges
    *
    * @param b         Board of the game that might be over
    * @param t         Turn status indicating who's turn it is
    * @param s         Status of Turn (i.e. Place, Build, Move, Game Over)
    * @return          True if the game is over, False if it is not
    */
    boolean isGameOver(Board b, Turn t, Status s);
