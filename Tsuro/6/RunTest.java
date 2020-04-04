import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.*;
import com.sun.tools.javac.util.Pair;

/**
 * A tester for the client and server.
 */
public class RunTest {
  public static final int DEFAULT_PORT = 8080;
  public static final String DEFAULT_IP = "127.0.0.1";
  
  /**
   * Main method for the testing of the client and server together.
   *
   * @param args The command line arguments for this program. They are ignored.
   */
  public static void main(String[] args) {
    try {
      String input = getInput();
      ArrayList<JsonElement> parsedInput = parse(input);
      boolean isInputValid = isInputValid(parsedInput);
      if (isInputValid) {
        ArrayList<Pair<String, String>> convertedInput = convertInput(parsedInput);
        processInput(convertedInput);
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
  
  /**
   * Converts the parsed input into a list of pairs of strings, representing
   * names and strategies of the requested players. This method is not
   * guaranteed to work if parsedInput fails {@link #isInputValid(ArrayList)}.
   *
   * @param parsedInput A list containing the parsed input to the program.
   * @return A list of pairs of strings, representing names and strategies of
   *         the requested players.
   */
  private static ArrayList<Pair<String, String>> convertInput(ArrayList<JsonElement> parsedInput) {
    JsonArray inputArray = parsedInput.get(0).getAsJsonArray();
    ArrayList<Pair<String, String>> convertedInput = new ArrayList<>();
    for (JsonElement element : inputArray) {
      JsonObject elementObject = element.getAsJsonObject();
      String name = elementObject.get("name").getAsString();
      String strategy = elementObject.get("strategy").getAsString();
      convertedInput.add(new Pair<>(name, strategy));
    }
    return convertedInput;
  }
  
  /**
   * Gets input from the STDIN and returns it as a String.
   *
   * @return A String containing the input read from STDIN.
   * @throws IOException if an I/O error occurs.
   */
  private static String getInput() throws IOException {
    StringBuilder input = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String nextLine = reader.readLine();
    while (nextLine != null) {
      input.append(nextLine);
      input.append("\n");
      nextLine = reader.readLine();
    }
    return input.toString();
  }
  
  /**
   * Checks if the passed Json element is a valid player specification object,
   * or namely that it only has the following two members:
   *
   * @param element The element to be queried.
   * @return True if element is a valid player specification object.
   * @throws IllegalArgumentException if element is not a Json object, if it
   *                                  does not have both a "name" and a
   *                                  "strategy" member, if those members are
   *                                  not strings, or if the "strategy" member
   *                                  is not a valid strategy name
   *                                  (case-insensitive).
   */
  private static boolean isElementValidObject(JsonElement element)
          throws IllegalArgumentException {
    if (!element.isJsonObject()) {
      throw new IllegalArgumentException("Element is not a Json Object: " +
              element);
    }
    JsonObject elementObject = element.getAsJsonObject();
    
    if (elementObject.has("name")) {
      JsonElement name = elementObject.get("name");
      if (!name.isJsonPrimitive() || !name.getAsJsonPrimitive().isString()) {
        throw new IllegalArgumentException("Object's name member must be a " +
                "string: " + elementObject);
      }
    } else {
      throw new IllegalArgumentException("Object does not have a \"name\" " +
              "member: " + elementObject);
    }
    
    if (elementObject.has("strategy")) {
      JsonElement strategy = elementObject.get("strategy");
      if (!strategy.isJsonPrimitive() || !strategy.getAsJsonPrimitive().isString()) {
        throw new IllegalArgumentException("Object's strategy member must be " +
                "a string: " + elementObject);
      } else {
        switch (strategy.getAsString()) {
          case "dumb":
          case "Dumb":
          case "second":
          case "Second":
            return true;
          default:
            throw new IllegalArgumentException("Object's strategy must be a " +
                    "valid strategy name: " + elementObject);
        }
      }
    } else {
      throw new IllegalArgumentException("Object does not have a \"strategy\"" +
              " member: " + elementObject);
    }
  }
  
  /**
   * Checks if the parsed program input is valid - namely, if it is a single
   * JsonArray which only contains valid player specification objects.
   *
   * @param parsedInput A list containing the parsed input to the program.
   * @return True if parsedInput is valid.
   * @throws IllegalArgumentException if parsedInput does not have only one
   *                                  element, if the only element is not a
   *                                  Json Array, if the Json Array does not
   *                                  have between 3 and 5 elements, or if one
   *                                  of those elements is not a valid player
   *                                  specification object.
   */
  private static boolean isInputValid(ArrayList<JsonElement> parsedInput)
          throws IllegalArgumentException {
    if (parsedInput.size() != 1) {
      throw new IllegalArgumentException("Program input should be only a " +
              "single Json element, namely an Array of valid player " +
              "specification objects.");
    } else if (!parsedInput.get(0).isJsonArray()) {
      throw new IllegalArgumentException("Program input must be a Json Array " +
              "of valid player specification " +
              "objects.");
    } else {
      JsonArray inputArray = parsedInput.get(0).getAsJsonArray();
      if (inputArray.size() < 3) {
        throw new IllegalArgumentException("Input array should have at least " +
                "three valid player specifications.");
      } else if (inputArray.size() > 5) {
        throw new IllegalArgumentException("Input array should have at most " +
                "five valid player specifications.");
      }
      
      boolean isInputValid = true;
      for (JsonElement element : inputArray) {
        isInputValid &= isElementValidObject(element);
      }
      return isInputValid;
    }
  }
  
  /**
   * Parses the given input into a list of JsonElements.
   *
   * @param input The String to be parsed.
   * @return A list of JsonElements parsed from input.
   * @throws JsonParseException if input is malformed Json.
   */
  private static ArrayList<JsonElement> parse(String input) throws JsonParseException {
    ArrayList<JsonElement> parsedInput = new ArrayList<>();
    JsonStreamParser parser = new JsonStreamParser(input);
    while (parser.hasNext()) {
      parsedInput.add(parser.next());
    }
    return parsedInput;
  }
  
  /**
   * Processes the input to this program by executing the server and instancing
   * the client for each player specification.
   *
   * @param convertedInput The converted input to this program.
   * @throws IOException if an I/O error occurs.
   */
  private static void processInput(ArrayList<Pair<String, String>> convertedInput) throws IOException {
    startServer();
    for (Pair<String, String> playerSpec : convertedInput) {
      startClient(playerSpec.fst, playerSpec.snd);
    }
  }
  
  /**
   * Starts the client executable with standard IP and port, and with the given
   * name and strategy.
   *
   * @param name The desired name of the player.
   * @param strategy The desired strategy of the player.
   * @throws IOException if an I/O error occurs.
   */
  private static void startClient(String name, String strategy) throws IOException {
    String command = "./xclient " + DEFAULT_IP + " " + DEFAULT_PORT + " " + name + " " + strategy;
    Runtime.getRuntime().exec(command);
  }
  
  /**
   * Starts the server executable with standard arguments.
   *
   * @throws IOException if an I/O error occurs.
   */
  private static void startServer() throws IOException {
    String command = "./xserver " + DEFAULT_IP + " " + DEFAULT_PORT;
    Runtime.getRuntime().exec(command);
  }
}
