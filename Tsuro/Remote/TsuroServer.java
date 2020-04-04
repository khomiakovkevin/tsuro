import com.google.gson.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class TsuroServer {
    public static void main(String[] args) throws IOException {
      run(InetAddress.getByName(RunTest.DEFAULT_IP), RunTest.DEFAULT_PORT);
    }
    
    public static void run(InetAddress ip, int port) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./xserver.log", true));
        // For testing, use "./xserver.log" when executing in ".../TikiTech/Tsuro/6/"
        // For programming, use "Tsuro/6/xserver.log" when executing in ".../TikiTech/"
        writer.write("<<< KEVIN LOH");
        Logger logger = Logger.getLogger(TsuroServer.class.getName());
        logger.setUseParentHandlers(false);
        FileHandler fh = new FileHandler("./xserver.log", false);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.addHandler(fh);
        ServerSocket ss = new ServerSocket(8080);
        IReferee referee = new Referee();
        
        // running infinite loop for getting
        // client request
        while (true) {
            Socket s = null;
            long processingTime = 0;
            
            for (int ii = 0; ii < 5; ii++) {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    if (ii == 2){
                        ss.setSoTimeout(30000);
                    }
                    if (ii == 3){
                        ss.setSoTimeout((int) (30000 - processingTime));
                    }
                    // socket object to receive incoming client requests
                    s = ss.accept();

                    // obtaining input and out streams
                    ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
                    ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                    
                    String[] nameAndStrat = dis.readObject().toString().split(",");

                    logger.info("<< " + nameAndStrat[0] + " ," + nameAndStrat[1]);

                    // create a new thread object
                    Thread player = new ProxyPlayer(nameAndStrat[0], 3 - ii, nameAndStrat[1], s, dis, dos, logger);
                    referee.addPlayer((IPlayer)player);

                    processingTime = System.currentTimeMillis() - startTime;
                    
                    
                } catch (SocketTimeoutException e) {
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            referee.runGame();
            closeTheGame(referee.getEliminated().get(referee.getEliminated().size() - 1));
            ss.close();
            
            Observer observer = new Observer(((Referee) referee).board);
            
            return;
        }
    }
    
    private static void closeTheGame (ArrayList<IPlayer> activePlayers) {
        for (IPlayer player: activePlayers){
            ProxyPlayer pp = (ProxyPlayer)player;
            pp.notifyWinner();
        }
    }
}

class ProxyPlayer extends Thread implements IPlayer {
    Logger logger;
    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    final Socket s;
    private String name;
    private int age;
    private Strategy strategy;
    private Point curPos;
    private Avatar avatar;
    private ArrayList<Tiles> tileHand;
    Gson gson = new Gson();

    // Constructor
    public ProxyPlayer(String name, int age, String stratName, Socket s,
                       ObjectInputStream dis, ObjectOutputStream dos, Logger logger) throws IOException {
        this.name = name;
        this.age = age;
        this.strategy = strategyFactory(stratName);
        Point point = null;
        Avatar avatar = null;
        this.tileHand = new ArrayList<>(3);
        this.logger = logger;
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

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

    public void notifyWinner(){
        try{
            logger.info(">> To [" + this.name + "]: Congratulations! You have won");
            dos.writeObject("Congratulations! You have won");
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    private void sendHand(){
        try {

            Gson gson = new Gson();

            String tileHand = gson.toJson(this.tileHand);
            logger.info(">> To [" + this.name + "]: Referee has sent you hand for the next round");
            logger.info(">> To [" + this.name + "]: "+ tileHand);
            dos.writeObject("Referee has sent you hand for the next round");
            dos.writeObject(tileHand);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    @Override
    public JsonArray provideInitPlacement() {
        sendHand();
        JsonArray clientRequest = null;
        try {
            logger.info(">> To [" + this.name + "]: Provide initial placement");
            dos.writeObject("Provide initial placement");
            clientRequest = gson.fromJson(dis.readObject().toString(), JsonArray.class);
            logger.info("<< " + "From [" + this.name + "]: " + clientRequest.toString());

        } catch (IOException io) {
            io.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clientRequest;
    }

    @Override
    public JsonArray provideInterPlacement() {
        sendHand();
        JsonArray clientRequest = null;

        try {
            dos.writeObject("Provide intermediate placement");
            logger.info(">> To [" + this.name + "]: Provide intermediate placement");
            clientRequest = gson.fromJson(dis.readObject().toString(), JsonArray.class);
            logger.info("<< From [" + this.name + "]: " + clientRequest.toString());

        } catch (IOException io) {
            io.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clientRequest;
    }

    @Override
    public void setAvatar(Avatar avatar) {
        try{
            Gson gson = new Gson();
            String avatarString = gson.toJson(avatar);
            logger.info(">> To [" + this.name + "]: " + "Referee assigned you an avatar"
                    + " with " + avatar.getColor().toString() + " color");
            logger.info(">> To [" + this.name + "]: " + avatarString);
            dos.writeObject("Referee assigned you an avatar");
            dos.writeObject(avatarString);
        } catch (IOException io) {
            io.printStackTrace();
        }
        this.avatar = avatar;
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
        try{
            logger.info(">> To [" + this.name + "]: " + "You were eliminated from the game!");
            dos.writeObject("You were eliminated from the game!");
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    @Override
    public void setKicked() {
        try{
            logger.info(">> To [" + this.name + "]: " + "You made an illegal move and were kicked from the game!");
            dos.writeObject("You made an illegal move and were kicked from the game!");
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    @Override
    public String getAvatarName() {
        return this.avatar.getColor().toString().toLowerCase();
    }

    public Avatar getAvatar() {
        return this.avatar;
    }

    public void setPosition (Point newPos) {
        this.curPos = newPos;
    }

    public boolean hasExited() {
        return this.avatar.hasExited();
    }

    public void setTileHand(ArrayList<Tiles> tileHand) {
        if (tileHand.size() > 3) {
            throw new IllegalArgumentException("Cannot give a hand of more than three tiles.");
        }
        this.tileHand = tileHand;
        sendHand();
    }

    /**
     * Tells if this Avatar currently has no moves that will not result in its
     * death.
     *
     * @param board The Board that this Avatar is playing on.
     * @return True if this Avatar has no valid moves left.
     */
    public boolean hasNoMoves(Board board, Tiles placingTile, boolean firstMove) {
        // Where is this Avatar immediately trying to move to?
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
    public void updateBoard(Board b) {
        try {
            Gson gson = new Gson();
            String boardString = gson.toJson(b);
            logger.info(">> To [" + this.name + "]: Referee has sent you updated board state");
            logger.info(">> To [" + this.name + "]:  Board JSON - " + boardString);
            dos.writeObject("Referee has sent you updated board state");
            dos.writeObject(boardString);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    @Override
    public int compareTo(IPlayer o) {
        return 0;
    }
}
