import java.awt.*;

/**
 * The Rules class is accessible to both the Referee and the Players for
 * checking the basic rules of the game. Both the Referee and the Players can
 * use the RuleChecker to check for the validity of an Action at any time
 * without mutating the state of the Board.
 * <p>
 * An Action is one of:
 * - Place Tile on Board
 * - Rotate Tile (before placing on Board)
 * - Place Avatar on a specific Port on a specific Tile
 * - Move
 * <p>
 * #### Tsuro is governed by the following basic rules:
 * On a Player's turn:
 * - During the first round, the Player must place their Tile on the Board's
 * edge.
 * - During the first round, the Player must place their Avatar on a Port
 * located on the initial Tile's edge(s).
 * - Throughout the game, the Player must place Tiles and take into account the
 * progression of their Avatar throughout the Board
 * <p>
 * A Rule Checker can:
 * - Check if a Place action for a Tile is legal
 * - Check if an initial Tiles and Avatar placement is legal
 * - Check if the game is over
 */
public class Rules {
    /**
     * Checks if an Avatar can be initially placed at the supplied location on the
     * given tile. This requires that:
     * - The given location is on the Board.
     * - The given location is not occupied by a Tile.
     * - All Tiles adjacent to the given location are empty.
     * - The given location is along the edge of the board
     * - The given placement would not end in the death of the player.
     *
     * @param board    The Board that the new Avatar and Tiles will be placed on.
     * @param pos      The coordinates that the new Tiles should be placed at.
     * @param portName The Port.PortName that the new Avatar should be placed at,
     *                 at the given pos.
     * @param tile     The Tiles being attempted to be placed.
     * @return True if the placement is legal, or false if it is not.
     */
    public static boolean isInitialPlacementLegal(Board board,
                                                  Point pos,
                                                  Port.PortName portName,
                                                  Tiles tile) {
        // Sets up temporary xx and yy variables for when it is more convenient
        int row = pos.y;
        int col = pos.x;

        // location must be on board
        boolean coordinatesOnBoard = doesBoardContainCoordinates(board, col, row);
        // location must be empty
        boolean locationEmpty = isBoardEmptyAt(board, col, row);
        // All tiles adjacent to location must be empty
        boolean adjacentsEmpty = areAdjacentsEmptyAt(board, col, row);
        // location must be along the edge of the board
        boolean connectsToEdge = connectsToEdge(board, pos, portName);
        // The placement must not result in the death of the player
        boolean willNotResultInDeath = !willEndInDeath(board, col, row, portName, tile,
                true);

        // If all these conditions are true, then the placement is legal.
        return coordinatesOnBoard && locationEmpty && adjacentsEmpty &&
                connectsToEdge && willNotResultInDeath;
    }

    /**
     * Checks if a specific Tiles can be placed at the given location, as an
     * intermediate turn of the given avatar.
     *
     * @param board  The Board that the Tiles will be placed on.
     * @param xx     The x coordinate the new Tile will be placed at.
     * @param yy     The y coordinate the new Tile will be placed at.
     * @param tile   The Tiles being attempted to be placed.
     * @param player The Player representing the player making this move.
     * @return True if the given move is either will not lead to the suicide of
     * the given player, or if all alternative moves to a suicidal request
     * that the player can make are also suicidal.
     */
    public static boolean isIntermediatePlacementLegal(Board board, int xx,
                                                       int yy, Tiles tile,
                                                       IPlayer player) {
        Avatar avatar = player.getAvatar();
        if (isPlaceTileLegal(board, xx, yy, tile, avatar)) {
            board.getGrid()[xx][yy] = tile;
            Port.PortName initPort = avatar.getCurrentPort();
            Point initPos = new Point(avatar.getPos().x, avatar.getPos().y);
            if (willEndInDeath(board, avatar.getPos().x, avatar.getPos().y, avatar.getCurrentPort(), tile, false)) {
                board.getGrid()[xx][yy] = null;
                avatar.updatePosition(initPos.x, initPos.y, initPort);
                return player.hasNoMoves(board, tile,false);
            }
            else {
                avatar.updatePosition(initPos.x, initPos.y, initPort);
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if a new Tile can be placed at (xx, yy). This requires that:
     * - The (xx, yy) position is on the Board.
     * - The (xx, yy) position is not already occupied.
     * - The (xx, yy) position is adjacent to the current location of the given
     * player.
     *
     * @param board  The Board that the new Tile will be placed on.
     * @param xx     The x coordinate the new Tile will be placed at.
     * @param yy     The y coordinate the new Tile will be placed at.
     * @param tile   The tile to be placed.
     * @param avatar The Avatar of the player attempting to make this placement.
     * @return True if the placement is legal, false if it is not.
     */
    public static boolean isPlaceTileLegal(Board board, int xx, int yy,
                                           Tiles tile, Avatar avatar) {
        // (xx, yy) must be on the Board.
        boolean validCoordinates = doesBoardContainCoordinates(board, xx, yy);
        if (!validCoordinates) {
            return false;
            // Case 1: coordinates lie outside board
        }

        // (xx, yy) must be empty.
        boolean locationEmpty = isBoardEmptyAt(board, xx, yy);
        if (!locationEmpty) {
            return false;
            // Case 2: coordinates refer to an already filled slot
        }
        // (xx, yy) must be adjacent to the current location of the player.
        boolean locationAdjacent = isAdjacentTo(xx, yy, avatar);

        // If any of these three conditions are false, the move is invalid.
        if (!locationAdjacent) {
            return false;
            // Case 3: coordinates not next to avatar
        }

        return true;
    }

    /**
     * Checks if given initial Avatar placement will not end in the death of the
     * player.
     * Assumes that coordinates are contained within the board.
     *
     * @param board       The Board being queried.
     * @param xx          The x coordinate that is attempting to be placed at.
     * @param yy          The y coordinate that is attempting to be placed at.
     * @param currentPort The Port that is attempting to be placed at at the given
     *                    position.
     * @param tile        The Tiles attempting to be placed.
     * @param firstMove   If this is an initial placement or not.
     * @return True if this attempted placement will result in the death of the
     * player.
     */
    public static boolean willEndInDeath(Board board, int xx, int yy,
                                         Port.PortName currentPort,
                                         Tiles tile, boolean firstMove) {
    /*
      If we placed tile at the given location, with an avatar potentially being
      moved initially to the portName of the location before tracing out its
      path to the end, what will the final location of the avatar be?
     */
        Location updatedLocation = board.tracePath(xx, yy, tile, currentPort,
                firstMove, false);

        // Unpack updated Location
        int updatedRow = updatedLocation.getCol();
        int updatedCol = updatedLocation.getRow();
        Port.PortName updatedPortName = updatedLocation.getPortName();

    /*
      If the updated location connects to the edge, then the proposed move will
      result in the player's death.
     */
        return connectsToEdge(board, updatedRow, updatedCol, updatedPortName);
    }

    /**
     * Checks if given position is only adjacent to empty slots on the given
     * Board.
     * Assumes that coordinates are contained within board.
     *
     * @param board The Board being queried.
     * @param xx    The x coordinate that is being queried.
     * @param yy    The y coordinate that is being queried.
     * @return True if (xx, yy) is only adjacent to empty spots, or false if not.
     */
    private static boolean areAdjacentsEmptyAt(Board board, int xx, int yy) {
        // Initialize return variable.
        boolean adjacentsEmpty = true;

    /*
      If (xx, yy) is not on the left border, then we can check if the tile left
      of it is empty or not.
     */
        if (xx != 0) {
            adjacentsEmpty = isBoardEmptyAt(board, xx - 1, yy);
        }
    /*
      If (xx, yy) is not on the right border, then we can check if the tile
      right of it is empty or not.
     */
        if (xx != board.getWidth() - 1) {
            adjacentsEmpty &= isBoardEmptyAt(board, xx + 1, yy);
        }
    /*
      If (xx, yy) is not on the top border, then we can check if the tile above
      it is empty or not.
     */
        if (yy != 0) {
            adjacentsEmpty &= isBoardEmptyAt(board, xx, yy - 1);
        }
    /*
      If (xx, yy) is not on the bottom border, then we can check if the tile
      below it is empty or not.
     */
        if (yy != board.getHeight() - 1) {
            adjacentsEmpty &= isBoardEmptyAt(board, xx, yy + 1);
        }

        // If all of the adjacent tiles are empty, return true.
        return adjacentsEmpty;
    }

    /**
     * Checks if the given location connects to the edge of the board.
     * Assumes that coordinates are contained within board.
     *
     * @param board    The Board being queried.
     * @param pos      The Point that is being queried.
     * @param portName The Port.PortName that is being queried.
     * @return True if location is adjacent to the edge of the board, or false if
     * not.
     */
    private static boolean connectsToEdge(Board board, Point pos,
                                          Port.PortName portName) {
        // Unpacks pos and sends it along to overloaded alias.
        return connectsToEdge(board, pos.y, pos.x, portName);
    }

    /**
     * Checks if the given coordinates and Port.PortName connects to the edge of
     * the board.
     * Assumes that coordinates are contained within board.
     *
     * @param board    The Board being queried.
     * @param col      The x coordinate being queried.
     * @param row      The y coordinate being queried.
     * @param portName The Port.PortName being queried.
     * @return True if the given location borders the edge of board.
     */
    public static boolean connectsToEdge(Board board, int row, int col,
                                         Port.PortName portName) {
        // If the location is at the North border and the port is North facing:
        boolean bordersNorth = (row == board.getHeight() - 1) &&
                (portName == Port.PortName.N1 ||
                        portName == Port.PortName.N2);
        // If the location is at the South border and the port is South facing:
        boolean bordersSouth = (row == 0) &&
                (portName == Port.PortName.S1 ||
                        portName == Port.PortName.S2);
        // If the location is at the West border and the port is West facing:
        boolean bordersWest = (col == 0) &&
                (portName == Port.PortName.W1 ||
                        portName == Port.PortName.W2);
        // If the location is at the East border and the port is East facing:
        boolean bordersEast = (col == board.getWidth() - 1) &&
                (portName == Port.PortName.E1 ||
                        portName == Port.PortName.E2);

        // If any of these are true, then the location connects to an edge.
        return bordersNorth || bordersSouth || bordersWest || bordersEast;
    }

    /**
     * Checks if the given position is on the given Board.
     *
     * @param board the Board that is being queried.
     * @param xx    The x coordinate that is being queried.
     * @param yy    The y coordinate that is being queried.
     * @return True if (xx, yy) is contained within this board, or false if not.
     */
    private static boolean doesBoardContainCoordinates(Board board, int xx,
                                                       int yy) {
        boolean containsX = xx >= 0 && xx < board.getWidth();
        boolean containsY = yy >= 0 && yy < board.getHeight();
        // If both dimensions are large enough for these indices, return true.
        return containsX && containsY;
    }

    /**
     * Checks if the given coordinates are adjacent to the given avatar.
     *
     * @param row     The row of the tile being queried.
     * @param col     The col of the tile being queried.
     * @param avatar The Avatar being queried.
     * @return Whether or not the given coordinates are adjacent to the given
     * avatar.
     */
    private static boolean isAdjacentTo(int col, int row, Avatar avatar) {
        // Get location where avatar is attempting to immediately move to.
        Location currentLocation = new Location(avatar);
        Location nextLocation = currentLocation.advance();
        // If both indices match up, then return true.
        return (nextLocation.getRow() == row) &&
                (nextLocation.getCol() == col);
    }

    /**
     * Checks if given position is empty on the given Board.
     * Assumes that coordinates are contained within board.
     *
     * @param board The Board being queried.
     * @param xx    The x coordinate that is being queried.
     * @param yy    The y coordinate that is being queried.
     * @return True if (xx, yy) is empty on this board, or false if not.
     */
    private static boolean isBoardEmptyAt(Board board, int xx, int yy) {
        // If there is no Tiles object at given indices, then return true.
        return board.getTileAt(xx, yy) == null;
    }
    
    /**
     * Checks if the given coordinates are valid for an initial tile placement.
     *
     * @param board The board to be placed on.
     * @param xx    The desired x coordinate of the placement.
     * @param yy    The desired y coordinate of the placement.
     * @return True if the coordinates lie inside board, and refer to a spot
     *         that is empty and has empty neighbors.
     */
    public static boolean validCoordinates(Board board, int xx, int yy) {
        return doesBoardContainCoordinates(board, xx, yy) &&
                isBoardEmptyAt(board, xx, yy) &&
                areAdjacentsEmptyAt(board, xx, yy);
    }
}
