import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.internal.runtime.JSONFunctions;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// Client class 
public class Client {
  public static void main(String[] args) throws IOException {
    run(InetAddress.getByName(RunTest.DEFAULT_IP), RunTest.DEFAULT_PORT, args[0], args[1]);
  }
  
  public static void run(InetAddress ip, int port, String name, String strategy) throws IOException {
    Player player = new Player(name, -1, strategy);
    Gson gson = new Gson();
    
    try
    {
      // getting localhost ip
      
      // establish the connection with server port 5056
      Socket s = new Socket(ip, port);
      
      // obtaining input and out streams
      ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
      ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
      
      dos.writeObject(name + "," + strategy);
      dos.flush();
      
      while (true)
      {
        
        String serverCommand = dis.readObject().toString();
        switch (serverCommand){
          case "Referee assigned you an avatar":
            String stringAvatar = dis.readObject().toString();
            Avatar avatar = gson.fromJson(stringAvatar, Avatar.class);
            player.setAvatar(avatar);
            break;
          case "Referee has sent you hand for the next round":
            String stringHand = dis.readObject().toString();
            ArrayList<Tiles> hand = gson.fromJson(stringHand,
                    new TypeToken<ArrayList<Tiles>>() {}.getType());
            player.setTileHand(hand);
            break;
          case "Referee has sent you updated board state":
            String stringBoard = dis.readObject().toString();
            Board board = gson.fromJson(stringBoard, Board.class);
            player.updateBoard(board);
            break;
          case "Provide initial placement":
            JsonArray initPlacementRequest = player.provideInitPlacement();
            String initRequestString = gson.toJson(initPlacementRequest);
            dos.writeObject(initRequestString);
            break;
          case "Provide intermediate placement":
            JsonArray interPlacementRequest = player.provideInterPlacement();
            String interRequestString = gson.toJson(interPlacementRequest);
            dos.writeObject(interRequestString);
            break;
          case "You were eliminated from the game!":
            s.close();
            break;
          case "You made an illegal move and were kicked from the game!":
            s.close();
            break;
          case "Congratulations! You have won":
            s.close();
            break;
          default: throw new IllegalStateException(serverCommand + " is not a valid server request");
        }
      }
  
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }catch (SocketException se){
      System.out.println(player.getName() + " has closed connection");
    }
  }
} 