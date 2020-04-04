import com.google.gson.JsonArray;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


public class Referee implements IReferee {
    private PlayerManager playerManager;
    boolean gameOver;
    boolean initialPlacementDone;
    Board board;
    ArrayList<String> observers;
    int lastOption;
    private ArrayList<Tiles> drawingTileStack;
    private ArrayList<Tiles> all35tiles;
    private IPlayer curTurn;
    private int curSelected;
    private ArrayList<Tiles> curOptions;
    
    public Referee() {
        this.playerManager = new PlayerManager();
        this.gameOver = false;
        this.initialPlacementDone = false;
        this.board = new Board();
        this.observers = new ArrayList<>();
        this.lastOption = 0;
        this.initDrawingTileStack();
        this.all35tiles = Tiles.all35Tiles();
    }
    
    public Referee(int height, int width) {
        this.playerManager = new PlayerManager();
        this.gameOver = false;
        this.initialPlacementDone = false;
        this.board = new Board(height, width);
        this.observers = new ArrayList<>();
        this.lastOption = 0;
        this.initDrawingTileStack();
        this.all35tiles = Tiles.all35Tiles();
    }
    
    /**
    * Returns the current state of the Board.
    */
    public Board getBoard() {
        return board;
    }
    
    /**
    * Sets this Referee to connect to the Board.
    */
    public void connectToBoard(){
        this.board.setReferee(this);
    }

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
    @Override
    public void runGame() throws IllegalStateException {
        connectToBoard();
        this.playerManager.initGame();
        assignColors();
        promptAllInitialPlacements();
        while (!this.gameOver) {
            promptInterPlacement();
            checkGameOver();
        }
        this.playerManager.finalizeGame();
    }

    /**
    * Assigns colors (Avatars) to each of the Players in the Game.
    */
    private void assignColors() {
        this.playerManager.assignColors();
    }
    

    /**
    * Checks to see if the number of players is 1 or 0, prompting the end of the game.
    */
    private void checkGameOver() {
        if (this.getActive().size() < 2) {
            this.gameOver = true;
        }
    }

    /**
     * Prompt each active player to make the initial placement from the Tile options
     */
    private void promptAllInitialPlacements() {
        ArrayList<IPlayer> startingActive = new ArrayList<>(this.playerManager.getActive());
        for (IPlayer player : startingActive) {
            if (!player.hasExited()) {
                promptInitPlacement(player);
            }
        }
    }

    /**
     * Provide the player the options of Tiles, updates board, receives Tile options
     * @param player The current player getting prompted to choose and place intial Tile
     */
    public void promptInitPlacement(IPlayer player){
        ArrayList<Tiles> options = getOptions(3);
        this.curOptions = options;
        this.curTurn = player;
        player.setTileHand(options);
        player.updateBoard(this.board);
        JsonArray placementRequest = player.provideInitPlacement();
        Tiles tile = this.all35tiles.get(placementRequest.get(0).getAsInt());
        this.curSelected = placementRequest.get(0).getAsInt();
        tile.rotate(placementRequest.get(1).getAsInt());
        Port.PortName port = Port.PortName.valueOf(placementRequest.get(3).getAsString());
        Point pos = new Point(placementRequest.get(4).getAsInt(), placementRequest.get(5).getAsInt());
        if (Rules.isInitialPlacementLegal(this.board, pos,
                port, tile)) {
            this.board.initialPlacement(pos.x, pos.y, tile, port, player.getAvatar());
            player.getAvatar().updatePosition(pos.x, pos.y, port);
            this.moveAvatar(player.getAvatar(), true);
            player.setAvatar(player.getAvatar());
        } else {
            this.drawingTileStack.add(tile);
            this.playerManager.kickPlayer(player, this.drawingTileStack);
        }
    }

    /**
     * Provide the options of Tiles for teh Player to choose from
     * @param numOfOptions The number of options for the player to choose from
     *          (3 for initial, 2 for intermediate)
     */
    private ArrayList<Tiles> getOptions(int numOfOptions) {
        ArrayList<Tiles> options = new ArrayList<>();
        for (int i = 0; i < numOfOptions; i++) {

            options.add(this.all35tiles.get(this.lastOption));
            this.lastOption += 1;
            this.lastOption %= 34;
        }
        return options;
    }


    /**
     * Provide the player the options of Tiles, updates board, receives Tile options
     */
    private void promptInterPlacement() {
        for (IPlayer player : new ArrayList<>(this.playerManager.getActive())) {
            if (!player.hasExited()) {
                ArrayList<Tiles> options = this.getOptions(2);
                player.setTileHand(options);
                this.curTurn = player;
                this.curOptions = options;
                player.updateBoard(this.board);
                JsonArray placementRequest = player.provideInterPlacement();
                Tiles tile = this.all35tiles.get(placementRequest.get(1).getAsInt());
                this.curSelected = placementRequest.get(1).getAsInt();
                tile.rotate(placementRequest.get(2).getAsInt());
                Point pos = new Point(placementRequest.get(3).getAsInt(), placementRequest.get(4).getAsInt());
                boolean isPlaceTileLegal = Rules.isPlaceTileLegal(this.board, pos.x, pos.y, tile, player.getAvatar());
                boolean willEndInDeath = Rules.willEndInDeath(this.board, pos.x,
                        pos.y, player.getAvatar().getCurrentPort().advance(), tile,
                        true);
                boolean hasNoMoves = player.hasNoMoves(this.board, tile, false);
                
                
                if ((isPlaceTileLegal && !willEndInDeath) || hasNoMoves) {
                    board.placeTile(pos.x,pos.y,tile,player.getAvatar(),false);
                    this.advancePlayers();
                    player.setAvatar(player.getAvatar());
                } else {
                    this.drawingTileStack.add(tile);
                    this.playerManager.kickPlayer(player, this.drawingTileStack);
                }
            }
        }
    }
    
    /**
     * Advance all players' Avatars to their new locations (Ports). If they reached the edge
     * of the board, the Player is eliminated.
     */
    public void advancePlayers() {
        ArrayList<IPlayer> exitingPlayers = new ArrayList<>();
        for (IPlayer player : this.getActive()) {
            Avatar avatar = player.getAvatar();
            this.moveAvatar(avatar, false);
            if (Rules.connectsToEdge(this.board, avatar.getPos().y, avatar.getPos().x, avatar.getCurrentPort())) {
                avatar.setExited();
                exitingPlayers.add(player);
            }
        }
        this.playerManager.eliminatePlayers(exitingPlayers, this.drawingTileStack);
    }

    /**
     * Update the positions of the Avatar
     * @param avatar The avatar to be moved
     * @param firstMove Is this move the first move for the Avatar or not?
     */
    public void moveAvatar(Avatar avatar, boolean firstMove) {
        Location endOfPath = this.board.tracePath(avatar, firstMove);
        int finalRow = endOfPath.getRow();
        int finalCol = endOfPath.getCol();
        Port.PortName finalPort = endOfPath.getPortName();
        
        avatar.updatePosition(finalRow, finalCol, finalPort);
    }

    /**
     * Returns the current selected Tile from the given tile options
     */
    @Override
    public int getCurSelected() {
        return this.curSelected;
    }

    /**
     * Returns an ArrayList of Tiles for thePlayer to choose from
     */
    @Override
    public ArrayList<Tiles> getCurOptions() {
        return this.curOptions;
    }

    
    /**
     * Returns the Player whose turn it currently is
     */
    @Override
    public IPlayer getCurTurn() {
        return this.curTurn;
    }

    
    /**
     * Returns the Player whose Avatar he/she corresponds to
     * @param a The avatar whose Player we are looking for
     */
    private IPlayer getPlayer(Avatar a) {
        for (IPlayer p : this.getRegistered()) {
            if (p.getAvatarName().equals(a.getColor().toString().toLowerCase())) {
                return p;
            }
        }
        throw new IllegalArgumentException("Avatar: " + a.getColor() + " is not playing!");
    }
    
    /**
     * Returns the Player whose Avatar color he/she corresponds to
     * @param avatarColor The avatar color whose Player we are looking for
     */
    public IPlayer getPlayer (String avatarColor) {
        for (IPlayer p : this.getRegistered()) {
            if (p.getAvatarName().toLowerCase().equals(avatarColor)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Avatar: " + avatarColor + " is not playing!");
    }

    /**
     * Creates a new Player instance using the given parameters.
     * @param playerName The name of the Player, which must be unique.
     * @param age The age of the player
     * @param avatarColor The color of the Avatar that the Player chooses, which
     *                    must be unique.
     * @throws IllegalArgumentException If playerName or avatarColor is already in
     *                                  use.
     */
    @Override
    public void addPlayer(String playerName, int age, String avatarColor) {
        this.playerManager.registerPlayer(playerName, age, avatarColor, this.board);
    }

    /**
     * Creates a new Player instance using the given parameters.
     * @param p The Player, which mucts be unique
     */
    @Override
    public void addPlayer(IPlayer p) {
        this.playerManager.registerPlayer(p);
    }

    /**
     * Creates a new Player instance using the given parameters.
     * @param avatarColor The avatar color chosen to play with, which must be
     *                    unique.
     * @throws IllegalArgumentException If avatarColor is already in use.
     */
    public void addPlayer(String avatarColor) {
        int index = this.getRegistered().size();
        this.addPlayer("testing" + index, index, avatarColor);
        this.playerManager.forceInit();
    }

    /**
     * Removes a Player from the game because they have lost.
     * @param playerColor The color of the Player to remove.
     */
    @Override
    public void removePlayer(String playerColor) {
        for (int i = 0; i < this.getActive().size(); i++) {
            if (this.getActive().get(i).getAvatarName().equals(playerColor)) {
                this.getActive().remove(i);
            }
        }
    }

    /**
     * Adds a new Observer to this Referee.
     * @param name The String name of the new Observer.
     */
    @Override
    public void addObserver(String name) {
        observers.add(name);

    }

    /**
     * Updates all Observers with either:
     * 1. New updates to the Board
     * 2. New Board requests
     * 3. End of Game messages
     * @param jsonString The updates to be sent to the observers.
     */
    @Override
    public void updateObserver(String jsonString) {
        return;
    }
    
    /**
     * Registers the Player in the Player Manager class and returns an IPlayer
     */
    public ArrayList<IPlayer> getRegistered() {
        return this.playerManager.getRegistered();
    }
    
    /**
     * Changes the roster of active and eliminated players to show the change in
     * active Players
     */
    public ArrayList<ArrayList<IPlayer>> getEliminated() {
        return this.playerManager.getEliminated();
    }
    
    /**
     * Changes the roster of active and eliminated player to show the change in
     * active Players
     */
    public ArrayList<IPlayer> getKicked() {
        return this.playerManager.getKicked();
    }
    
    /**
     * Returns a list of active players in the game
     */
    public ArrayList<IPlayer> getActive() {
        return this.playerManager.getActive();
    }
    
    /**
     * Initializes the drawing of the 35-tile stack
     */
    private void initDrawingTileStack() {
        int numTiles = this.board.getHeight() * this.board.getWidth();
        this.drawingTileStack = new ArrayList(numTiles);
        while (numTiles >= 35) {
            this.drawingTileStack.addAll(Tiles.all35Tiles());
            numTiles -= 35;
        }
        ArrayList<Tiles> finalStack = Tiles.all35Tiles();
        Collections.shuffle(finalStack);
        while (numTiles > 0) {
            this.drawingTileStack.add(finalStack.remove(0));
            numTiles -= 1;
        }
    }
    
    /**
     * Manages all Players in the game. Keeps track of all active and 
     * eliminated players as well as the creation of new players in the game.
     */
    private class PlayerManager {
        private ArrayList<IPlayer> registered;
        private ArrayList<IPlayer> active;
        private ArrayList<ArrayList<IPlayer>> eliminated;
        private ArrayList<IPlayer> kicked;
        
        private PlayerManager() {
            this.registered = new ArrayList<>();
            this.active = new ArrayList<>();
            this.eliminated = new ArrayList<>();
            this.kicked = new ArrayList<>();
        }
    
        private ArrayList<IPlayer> getRegistered() {
            return this.registered;
        }
    
        private ArrayList<IPlayer> getActive() {
            return this.active;
        }
    
        private ArrayList<ArrayList<IPlayer>> getEliminated() {
            return this.eliminated;
        }
    
        private ArrayList<IPlayer> getKicked() {
            return this.kicked;
        }
    
        private void assignColors() {
            ArrayList<String> colors = Avatar.AvatarColor.getAllColors();
            for (int ii = 0; ii < this.registered.size(); ++ii) {
                this.registered.get(ii).setAvatar(new Avatar(colors.get(ii)));

            }
        }
    
        private void registerPlayer(String playerName, int age, String avatarColor, Board board) {
            if (this.registered.size() < 5) {
                for (IPlayer p : this.registered) {
                    if (p.getPlayerName().equals(playerName)) {
                        throw new IllegalArgumentException("Name: " + playerName + " is already taken, choose another name.");
                    }
                }
                Player player = new Player(playerName, age, new DumbStrategy());
                player.setAvatar(new Avatar(avatarColor));
                this.registered.add(player);
                Collections.sort(this.registered);
            } else {
                throw new IllegalStateException("There are already 5 players in the game!");
            }
        }

        private void registerPlayer(IPlayer pl) {
            if (this.registered.size() < 5) {
                for (IPlayer p : this.registered) {
                    if (p.getPlayerName().equals(pl.getName())) {
                        throw new IllegalArgumentException("Name: " + p.getName() + " is already taken, choose another name.");
                    }
                }
                this.registered.add(pl);
                Collections.sort(this.registered);
            } else {
                throw new IllegalStateException("There are already 5 players in the game!");
            }
        }
        
        private void initGame() {
            if (this.registered.size() < 3 || this.registered.size() > 5) {
                throw new IllegalStateException("Must have three to five players to begin a game.");
            }
            this.active = new ArrayList<>(this.registered);
        }
        
        private void forceInit() {
            this.active = new ArrayList<>(this.registered);
        }
        
        private void kickPlayer(IPlayer p, ArrayList<Tiles> drawingTileStack) {
            if (this.active.contains(p)) {
                this.active.remove(p);
                this.kicked.add(p);
                p.setKicked();
            }
        }
        
        private void eliminatePlayers(ArrayList<IPlayer> players, ArrayList<Tiles> drawingTileStack) {
            for (IPlayer player : players) {
                this.active.remove(player);
                player.setEliminated();
            }
            if (players.size() > 0) {
                this.eliminated.add(players);
            }
        }
        
        
        private void finalizeGame() {
            if (this.active.size() == 1) {
                IPlayer finalist = this.active.remove(0);
                ArrayList<IPlayer> finalistList = new ArrayList<>();
                finalistList.add(finalist);
                this.eliminated.add(finalistList);
            }
        }
    }
}

