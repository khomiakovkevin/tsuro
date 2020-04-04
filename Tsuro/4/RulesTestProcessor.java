import java.sql.Ref;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;

/**
 * A test processor for the Rules class.
 */
public class RulesTestProcessor {
  private BiMap<String, Port.PortName> namingSchemeLegend;
  private Referee referee;
  private Board board;
  private ArrayList<Tiles> all35Tiles;
  
  public RulesTestProcessor() {
    this.namingSchemeLegend = getNamingSchemeLegend();
    
    this.referee = new Referee();
    this.referee.connectToBoard();
    this.board = this.referee.getBoard();
    this.all35Tiles = Tiles.all35Tiles();
  }
  
  public void processTest(String input) {
    ArrayList<JsonArray> commands = parse(input);
    
    for (JsonArray command : commands) {
      switch (command.size()) {
        case 3:
          this.processTurnSpecification(command);
          break;
        case 5:
          this.processIntermediatePlacement(command);
          break;
        case 6:
          this.processInitialPlacement(command);
          break;
        default:
      }
    }
  }
  
  private void processInitialPlacement(JsonArray placement) {
    int tileIndex = placement.get(0).getAsInt();
    int rotation = placement.get(1).getAsInt();
    String color = placement.get(2).getAsString();
    String port = placement.get(3).getAsString();
    int xx = placement.get(4).getAsInt();
    int yy = placement.get(5).getAsInt();
    Tiles placingTile = this.all35Tiles.get(tileIndex).rotate(rotation);
    Port.PortName correctedPort =
            (Port.PortName) this.namingSchemeLegend.get(port);
    
    this.referee.addPlayer(color);
    Avatar playerAvatar = this.referee.getPlayer(color).getAvatar();
    playerAvatar.updatePosition(xx, yy, correctedPort);
    this.board.initialPlacement(yy, xx, placingTile, correctedPort, playerAvatar);
  }
  
  
  
  private void processIntermediatePlacement(JsonArray placement) {
    String color = placement.get(0).getAsString();
    int tileIndex = placement.get(1).getAsInt();
    int rotation = placement.get(2).getAsInt();
    int xx = placement.get(3).getAsInt();
    int yy = placement.get(4).getAsInt();
    
    Tiles tile = this.all35Tiles.get(tileIndex).rotate(rotation);
    Avatar currentPlayer = this.board.getRef().getPlayer(color).getAvatar();
    
    this.board.placeTile(yy, xx, tile, currentPlayer, false);
  }
  

  
  
  private void processTurnSpecification(JsonArray specification) {
    JsonArray command = CommandValidator.getAsJsonArray(specification.get(0));
    String color = command.get(0).getAsString();
    int tilePlacingIndex = command.get(1).getAsInt();
    int rotation = command.get(2).getAsInt();
    int xx = command.get(3).getAsInt();
    int yy = command.get(4).getAsInt();
    
    IPlayer currentPlayer = this.board.getRef().getPlayer(color);
    if (currentPlayer == null) {
      System.out.println("illegal");
      return;
    }
    Tiles toPlace = this.all35Tiles.get(tilePlacingIndex).rotate(rotation);
    ArrayList<Tiles> newHand = new ArrayList<>();
    newHand.add(toPlace);
  
    int tileHandIndex1 = specification.get(1).getAsInt();
    Tiles tileHand1 = null;
    if (tileHandIndex1 >= 0) {
      tileHand1 = this.all35Tiles.get(tileHandIndex1);
      newHand.add(tileHand1);
    }
  
    int tileHandIndex2 = specification.get(2).getAsInt();
    Tiles tileHand2 = null;
    if (tileHandIndex2 >= 0) {
      tileHand2 = this.all35Tiles.get(tileHandIndex1);
      newHand.add(tileHand2);
    }
    
    currentPlayer.setTileHand(newHand);
    
    if (Rules.isIntermediatePlacementLegal(this.board, xx, yy, toPlace,
            currentPlayer)) {
      System.out.println("legal");
    } else {
      System.out.println("illegal");
    }
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  /**
   * Creates a bidirectional map that represents the cipher/translation between
   * our Port naming scheme, and that of the assignments.
   * @return The translation between the two aforementioned Port labelings.
   */
  private static BiMap<String, Port.PortName> getNamingSchemeLegend() {
    BiMap<String, Port.PortName> namingSchemeLegend =
            new BiMap<>(String.class, Port.PortName.class);
    namingSchemeLegend.put("A", Port.PortName.N1);
    namingSchemeLegend.put("B", Port.PortName.N2);
    namingSchemeLegend.put("C", Port.PortName.E1);
    namingSchemeLegend.put("D", Port.PortName.E2);
    namingSchemeLegend.put("E", Port.PortName.S1);
    namingSchemeLegend.put("F", Port.PortName.S2);
    namingSchemeLegend.put("G", Port.PortName.W1);
    namingSchemeLegend.put("H", Port.PortName.W2);
    return namingSchemeLegend;
  }
  
  /**
   * Parses String input for valid Json commands.
   * @param input The String containing the program input.
   * @return An ArrayList of valid commands.
   */
  private static ArrayList<JsonArray> parse(String input) {
    ArrayList<JsonArray> parsedList = new ArrayList<>();
    JsonStreamParser parser = new JsonStreamParser(input);
    
    try {
      JsonElement parsedInput = parser.next();
      if (!(parsedInput instanceof JsonArray)) {
        throw new IllegalArgumentException("Input must be a Json Array of " +
                                           "valid commands.");
      }
      for (JsonElement entry : (JsonArray) parsedInput) {
        if (CommandValidator.validCommand(entry)) {
          parsedList.add(entry.getAsJsonArray());
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    
    return parsedList;
  }
}

/*

Develop a test harness for the rule checker’s capability of evaluating the
legality of a turn.

The input is a JSON array of initial and intermediate placements as in Phase 3
(describing the current state of the board), followed by a turn specification:

[ [color, tile-index, rotation, x, y], tile-index, tile-index ]

The turn specification contains: the player requesting the placement, the chosen
tile index, its rotation and requested position, as well as tiles that were
provided to the player by the referee.

The outputs are "legal" and "illegal".

Create five tests and place them in the directory rules-tests. The test harness
can accept input from STDIN or it can take a filename as an argument. You do not
have to implement both options, but you need to specify which your test harness
uses. Provide a README-tests.md with instructions and an example run of your
test harness.

 */