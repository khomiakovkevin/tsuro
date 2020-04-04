import com.google.gson.JsonArray;

import java.awt.*;
import java.util.ArrayList;

public class Player implements IPlayer{
    String name;
    int age;
    Strategy strategy;
    Point curPos;
    Avatar avatar;
    private ArrayList<Tiles> tileHand;
    
    public Player(String name, int age, Strategy strategy) {
        this.name = name;
        this.age = age;
        this.strategy = strategy;
        Point point = null;
        Avatar avatar = null;
        this.tileHand = new ArrayList<>(3);
    }

    public Player(String name, int age, String stratName){
        this.name = name;
        this.age = age;
        this.strategy = strategyFactory(stratName);
        Point point = null;
        Avatar avatar = null;
        this.tileHand = new ArrayList<>(3);

    }

    public void updateBoard (Board b){
        this.strategy.updateBoard(b);
    }
    
    /**
    * Runs the given strategy and plays Tsuro accordingly
    * @param stratName The name of the strategy the Player wants to play
    */
    private Strategy strategyFactory(String stratName) {
        switch (stratName) {
            case "dumb":
            case "Dumb":
                return new DumbStrategy();
            case "second":
            case "Second":
                return new Second();
            default:
                throw new IllegalArgumentException("Strategy: " + stratName + " is not supported");
        }
        
    }
    
    public int compareTo (Player p){
        return this.age - p.getAge();
    }

    @Override
    public int getAge(){
        return this.age;
    }


    @Override
    public String getPlayerName() {
        return this.name;
    }

    @Override
    public void setEliminated() {
        this.avatar.setExited();
    }

    @Override
    public void setKicked() {
        this.avatar.setExited();
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Gets the color of this Player's Avatar.
     * @return The color of this Player's Avatar.
     */
    @Override
    public String getAvatarName() {
        return this.avatar.getColor().toString().toLowerCase();
    }

    /**
     * Gets the Avatar belonging to this Player
     * @return The Avatar.
     */
    public Avatar getAvatar() {
        return this.avatar;
    }
    
    
    /**
     * @return the JSON array representing the initial placement [23, 0, "blue", "E", 0, 0]
     */
    @Override
    public JsonArray provideInitPlacement() {
        JsonArray response = new JsonArray();
        int optionIdx = this.strategy.chooseInitialTile();
        Tiles chosenTile = this.tileHand.remove(optionIdx);
        Point chosenPos = this.strategy.chooseInitPosition();
        Port.PortName chosenPort = this.strategy.choosePort(chosenTile, chosenPos);
        int chosenRotation = this.strategy.chooseRotation();

        response.add(chosenTile.getIdxInAll35());
        response.add(chosenRotation);
        response.add(this.avatar.getColor().toString().toLowerCase());
        response.add(chosenPort.toString());
        response.add(chosenPos.x);
        response.add(chosenPos.y);

        return response;
    }

    
    /**
     * Sets the new Position of the Player
     * @param newPos The new position that the Player is located at
     */
    public void setPosition (Point newPos) {
        this.curPos = newPos;
    }

    
    /**
     * @return the point that the intermediate position leads to according the the Player's strategy
     */
    private Point chooseInterPosition() {
        Point currentPosition = this.avatar.getPos();
        int xx = currentPosition.x;
        int yy = currentPosition.y;
        Port.PortName currentPort = this.avatar.getCurrentPort();

        switch (currentPort) {
            default:
                return null;
            case N1:
            case N2:
                return new Point(xx, yy + 1);
            case E1:
            case E2:
                return new Point(xx + 1, yy);
            case S1:
            case S2:
                return new Point(xx, yy - 1);
            case W1:
            case W2:
                return new Point(xx - 1, yy);
        }
    }

    
     /**
     * @return the JSON array representing the intermediate placement ["blue", 0, 0, 0, 1]
     */
    @Override
    public JsonArray provideInterPlacement() {

        JsonArray response = new JsonArray();
        int optionIdx = this.strategy.chooseIntermediateTile();
        Tiles chosenTile = this.tileHand.remove(optionIdx);
        Point chosenPos = this.chooseInterPosition();
        int chosenRotation = this.strategy.chooseRotation();

        response.add(this.avatar.getColor().toString().toLowerCase());
        response.add(chosenTile.getIdxInAll35());
        response.add(chosenRotation);
        response.add(chosenPos.x);
        response.add(chosenPos.y);

        return response;
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public boolean hasExited() {
        return this.avatar.hasExited();
    }
    
    public void setTileHand(ArrayList<Tiles> tileHand) {
        if (tileHand.size() > 3) {
            throw new IllegalArgumentException("Cannot give a hand of more than three tiles.");
        }
        this.tileHand = tileHand;
    }
    
    public ArrayList<Tiles> getTileHand() {
        return this.tileHand;
    }
    
    public void resetTileHand() {
        this.tileHand = new ArrayList<>();
    }

    /**
     * Tells if this Avatar currently has no moves that will not result in its
     * death.
     *
     * @param board The Board that this Avatar is playing on.
     * @return True if this Avatar has no valid moves left.
     */
    public boolean hasNoMoves(Board board, Tiles placingTile, boolean firstMove) {

        Location here = new Location(this.avatar);
        Location toPlace = here.advance();
        
        // Options for rotations:
        int[] degrees = {0, 90, 180, 270};
    
    /*
       For each tile in the hand and every way it can be rotated, check if that
       placement would end in death.
     */
        ArrayList<Tiles> checkingTiles = new ArrayList<>(this.tileHand);
        checkingTiles.add(placingTile);
        for (Tiles tile : checkingTiles) {
            if (tile == null) {
                continue;
            }
            for (int numDegrees : degrees) {
                boolean isMoveSuicidal = Rules.willEndInDeath(board, toPlace.getCol(),
                        toPlace.getRow(), toPlace.getPortName(), tile.rotate(numDegrees),
                        true);
                if (!isMoveSuicidal) {
                    /// If any moves will not end in death, then return false.
                    return false;
                }
            }
        }
        
        // If all moves will end in death, then return true.
        return true;
    }
    
    public boolean hasFullHand() {
        return this.tileHand.size() == 3;
    }

    public ArrayList<Tiles> confiscateTiles() {
        ArrayList<Tiles> confiscatedTiles = this.tileHand;
        this.tileHand = new ArrayList<>();
        return confiscatedTiles;
    }

    @Override
    public int compareTo(IPlayer o) {
        return 0;
    }
}
