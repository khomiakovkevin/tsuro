import com.google.gson.JsonArray;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a board for a game of Tsuro.
 */
public class Board {
    private Tiles[][] grid;
    private transient Referee ref;

    /**
     * Constructs a Tsuro game board.
     */
    public Board() {
        this.grid = new Tiles[10][10];
    }
    
    /**
     * Constructs a Tsuro game board with the given dimensions.
     *
     * @param height The desired height of the Tsuro board.
     * @param width The desired width of the Tsuro board.
     */
    public Board(int height, int width) {
        this.grid = new Tiles[height][width];
    }
    
    /**
     * Sets the Referee of this board as the given value.
     *
     * @param ref The desired new referee.
     */
    public void setReferee(Referee ref) {
        this.ref = ref;
    }
    
    /**
     * Gets the grid of Tiles of this Board.
     *
     * @return This Board's grid of Tiles.
     */
    public Tiles[][] getGrid() {
        return grid;
    }

    /**
     * Gives the selected tile slot in the board.
     * Assumes that x and y coordinates are within the bounds of this board.
     *
     * @param xx The x coordinate of the slot being selected.
     * @param yy The y coordinate of the slot being selected.
     * @return The Tiles in this slot, or null if it is empty.
     */
    public Tiles getTileAt(int xx, int yy) {
        return this.grid[xx][yy];
    }
    
    /**
     * Gets the height of this Board.
     *
     * @return The height of this Board.
     */
    public int getHeight() {
        return this.grid.length;
    }
    
    /**
     * Gets the width of this Board.
     *
     * @return The width of this Board.
     */
    public int getWidth() {
        return this.grid[0].length;
    }

    /**
    * Get an Array of positions for all players on the board.
    * @param portMap The list of all portnames and player names
    */
    public JsonArray getAllPlayersPosition(HashMap<Port.PortName, String> portMap) {
        JsonArray result = new JsonArray();

        for (Avatar.AvatarColor color : Avatar.AvatarColor.values()) {
            JsonArray colorPos = new JsonArray();
            if (this.ref.getPlayer(color.toString().toLowerCase()) == null) {
                colorPos.add(color.toString().toLowerCase());
                colorPos.add("never played");
                result.add(colorPos);
            } else {
                result.add(getPlayerPosition(this.ref.getPlayer(color.toString().toLowerCase()).getAvatar(), portMap));
            }
        }
        checkForCollisions(result);
        return result;
    }

    /**
     * Places initial tile, and then places player avatar on selected port.
     *
     * @param row           The row of the location where a placement is to be made.
     * @param column        The column of the location where a placement is to be made.
     * @param tile          The tile to be placed.
     * @param portName      The port of the tile to place the avatar on.
     * @param currentPlayer The player who is making this placement.
     */
    public void initialPlacement(int row, int column, Tiles tile, Port.PortName portName, Avatar currentPlayer) {
        this.placeTile(row, column, tile, currentPlayer, true);
        this.placeAvatar(row, column, portName, currentPlayer);
    }

    /**
     * Ensures with rule checker that placement is valid, and simply places tile.
     * Player advancement inside of board is handled elsewhere.
     *
     * @param row           The row of the location where a placement is to be made.
     * @param column        The column of the location where a placement is to be made.
     * @param tile          The tile to be placed.
     * @param currentPlayer The player who is making this placement.
     * @param firstMove     Is this the first move a player has made or not?
     */
    public void placeTile(int row, int column, Tiles tile, Avatar currentPlayer, boolean firstMove) {
        Tiles tileCopy = tile.clone();
        this.grid[row][column] = tileCopy;

        // After referee calls this method it will advance the players according to
        // logic we will write in future assignment

        if (!firstMove) {
            for (IPlayer player : this.ref.getActive()) {
                Avatar avatar = player.getAvatar();
                this.ref.moveAvatar(avatar, false);
            }
        }
    }

    /**
     * Checks to see if there are any avatars whose positions on the board are the same
     *
     * @param positions An Array of all positions of avatars on the board
     */
    private void checkForCollisions(JsonArray positions) {
        for (int ii = 0; ii < positions.size(); ii++) {
            JsonArray pos1 = (JsonArray) positions.get(ii);
            if (pos1.size() > 2) {
                for (int jj = ii + 1; jj < positions.size(); jj++) {
                    JsonArray pos2 = (JsonArray) positions.get(jj);
                    if (pos2.size() > 2 && pos1.get(1).getAsInt() == pos2.get(1).getAsInt()) {

                        JsonArray newPos1 = new JsonArray();
                        newPos1.add(pos1.get(0));
                        newPos1.add("collided");
                        positions.set(ii, newPos1);

                        JsonArray newPos2 = new JsonArray();
                        newPos2.add(pos2.get(0));
                        newPos2.add("collided");
                        positions.set(jj, newPos2);

                    }
                }
            }

        }
    }

    /**
     * Get the end port given the starting port
     * @param p The name of the initial/starting port
     */
    private Port.PortName getEdgingPort(Port.PortName p) {
        switch (p) {
            case E2:
                return Port.PortName.W1;
            case E1:
                return Port.PortName.W2;
            case N2:
                return Port.PortName.S1;
            case N1:
                return Port.PortName.S2;
            case W2:
                return Port.PortName.E1;
            case W1:
                return Port.PortName.E2;
            case S2:
                return Port.PortName.N1;
            case S1:
                return Port.PortName.N2;
            default:
                return null;
        }
    }

    /**
     * From initial given starting position, trace the path as far as possible and
     * return the new position.
     *
     * @param row        The row of the current position.
     * @param col        The col coordinate of the current position.
     * @param tile      The tile either to be placed at this location, or the tile
     *                  already there.
     * @param portName  The port of the current position.
     * @param firstMove If true, the current position should be treated as an
     *                  entry point to the current tile rather than an exit.
     * @return A new Location, with x and y coordinates and a port name.
     */
    public Location tracePath(int row, int col, Tiles tile, Port.PortName portName,
                              boolean firstMove, boolean physicalMove) {

        // Setup of temporary variables.
        int currentRow = row;
        int currentCol = col;
        Port.PortName currentPortName = portName;
        Tiles currentTile = tile;

        // If the current port is an entry point rather than the exit point,
        // trace to the end of the current tile's path.
        if (firstMove) {
            currentPortName = currentTile.trace(portName, physicalMove);
        }

        // If the current position borders onto another placed tile, get its
        // location.
        Pair<Integer, Integer> nextCoordinates = this.advancedCoordinates(currentRow,
                currentCol,
                currentPortName, row, col);

        // As long as the next tile location is placed, trace to the end of the
        // given path, update temporary variables, and then check if the new
        // location borders onto another placed tile.
        while (nextCoordinates != null) {
            Avatar curAvatar = null;
            Port curPort = null;
            if (physicalMove) {
                for (IPlayer player : this.ref.getActive()) {
                    Avatar avatar = player.getAvatar();
                    if (avatar.getCurrentPort().toString().equals(currentPortName.toString())
                            && avatar.getPos().x == currentRow && avatar.getPos().y == currentCol) {
                        curAvatar = avatar;
                    }
                }
                
                curPort = currentTile.getPort(curAvatar.getCurrentPort());
            }

            currentPortName = currentPortName.advance();
            currentRow = nextCoordinates.fst;
            currentCol = nextCoordinates.snd;
            if (this.grid[currentRow][currentCol] != null) {
                currentTile = this.grid[currentRow][currentCol];
            } else if (currentRow == row && currentCol == col) {
              currentTile = tile;
            }

            if (physicalMove) {
                curPort.removeAvatar();
                Port nextPort = currentTile.getPort(curPort.getName().advance());
                nextPort.addAvatar(curAvatar);
            }
            currentPortName = currentTile.trace(currentPortName, physicalMove);
            if (physicalMove) {
                curAvatar.updatePosition(currentRow, currentCol, currentPortName);
            }

            nextCoordinates = this.advancedCoordinates(currentRow, currentCol,
                    currentPortName, row, col);
        }

        // Construct coordinates and return with final Port.PortName.
        return new Location(currentRow, currentCol, currentPortName);
    }
    
    /**
     * From the starting position of the given Avatar, trace its path as far as
     * possible and return the new position.
     *
     * @param avatar    The Avatar to be queried.
     * @param firstMove If true, the current position should be treated as an
     *                  entry point to the current tile rather than an exit.
     * @return A new Location, with x and y coordinates and a port name.
     */
    public Location tracePath(Avatar avatar, boolean firstMove) {
        int xx = avatar.getPos().x;
        int yy = avatar.getPos().y;
        Tiles tileAtLocation = this.grid[xx][yy];
        Port.PortName currentPort = avatar.getCurrentPort();
        return this.tracePath(xx, yy, tileAtLocation, currentPort, firstMove, true);
    }

    /**
     * Gives pair of coordinates referring to where a player at the given location
     * would advance to.
     * Assumes that the location is the exit point of the tile for the
     * hypothetical player.
     *
     * @param row          The row of the current location.
     * @param col          The column current location.
     * @param currentPort The current Port acting as an exit from this location.
     * @return A pair containing the coordinates of where the advancing would
     * lead, or null if the current Port points towards an empty spot.
     * @throws IllegalArgumentException If currentPort is not a valid
     *                                  Port.PortName.
     */
    private Pair<Integer, Integer> advancedCoordinates(int row, int col,
                                                       Port.PortName currentPort,
                                                       int placingRow,
                                                       int placingColumn)
            throws IllegalArgumentException {
        Point advancedPosition = this.advancePosition(new Point(col, row), currentPort);

        /*
            If a tile exists at the new position, return its position.
            Otherwise, return null.
         */
        if (advancedPosition == null) {
            return null;
        } else {
            if (this.getTileAt(advancedPosition.y, advancedPosition.x) == null) {
                if (advancedPosition.y != placingRow || advancedPosition.x != placingColumn) {
                    return null;
                }
            }
            return new Pair<>(advancedPosition.y, advancedPosition.x);
        }
    }
    
    /**
     * Gets the coordinates of the position adjacent to the given location.
     *
     * @param currentPosition The position to be queried.
     * @param currentPort     The port to be queried.
     * @return The coordinates of the position adjacent to the given location.
     * @throws IllegalArgumentException if currentPort is null.
     */
    public Point advancePosition(Point currentPosition, Port.PortName currentPort)
            throws IllegalArgumentException {
        // Initializes temporary variables:
        int currentRow = currentPosition.x;
        int currentCol = currentPosition.y;

        // Determines the coordinates that the current location points towards.
        switch (currentPort.direction()) {
            case "N":
                currentRow += 1;
                break;
            case "E":
                currentCol += 1;
                break;
            case "S":
                currentRow -= 1;
                break;
            case "W":
                currentCol -= 1;
                break;
            default:
                throw new IllegalArgumentException("Cannot advance coordinate of null " +
                        "port name.");
        }
        /*
           If the coordinates lie out of bounds of the game, no advancement can
           happen.
         */
        if (currentCol < 0 || currentCol >= this.getWidth() ||
                currentRow < 0 || currentRow >= this.getHeight()) {
            return null;
        }
        return new Point(currentRow, currentCol);
    }

    /**
    * Return information about the player, including position and Tile chosen
    * @param avatar The player's corresponding avatar
    * @param portMap A list of all ports and avatars on the board
    */
    private JsonArray getPlayerPosition(Avatar avatar, HashMap<Port.PortName, String> portMap) {
        JsonArray playerInfo = new JsonArray();
        String color = avatar.getColor().toString().toLowerCase();
        if (!avatar.hasExited()) {
            Point avatarPos = avatar.getPos();
            Tiles curTile = grid[avatarPos.x][avatarPos.y];

            int idxOfTile = curTile.getIdxInAll35();

            playerInfo.add(color);
            playerInfo.add(idxOfTile);
            playerInfo.add(curTile.getRotation());
            playerInfo.add(portMap.get(curTile.getAvatarPort(avatar)));
            playerInfo.add(avatarPos.x);
            playerInfo.add(avatarPos.y);

            return playerInfo;
        } else {
            playerInfo.add(color);
            playerInfo.add("exited");
            return playerInfo;
        }
    }

    /**
     * Places given avatar at the selected location after checking to make sure
     * the request is valid.
     *
     * @param row           The row of the location where a placement is to be made.
     * @param column        The column of the location where a placement is to be made.
     * @param portName      The port of the tile to place the avatar on.
     * @param currentPlayer The player who is making this placement.
     */
    private void placeAvatar(int row, int column, Port.PortName portName, Avatar currentPlayer) {
        this.grid[row][column].placeAvatar(portName, currentPlayer);
        currentPlayer.updatePosition(row, column, portName);
    }
    
    /**
     * Gets the Referee of this Board.
     *
     * @return The Referee of this Board.
     */
    public Referee getRef() {
        return this.ref;
    }
}
