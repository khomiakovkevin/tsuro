import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import com.google.gson.*;


import javafx.util.Pair;

/**
 * Class representing a client module handling client-server interactions.
 */
class Client {
    public static void main(String[] args) {
        // "["lab", {"from" : "B", "to" : "C"}]["add" , "blue", "B"]["move", "black", "B"]"
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        LabyrinthServer server = new MockLabyrinth();
        String nextLine;
        ArrayList<ParsedElement> commands;
        try {
            while (true) {
                nextLine = reader.readLine();
                if (nextLine == null) {
                    break;
                } else {
                    commands = parse(nextLine);
                    for (ParsedElement command : commands){
                        processCommand(command, server);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method responsible for processing single parsed command and invoking
     * appropriate server methods.
     * @param command parsed user command.
     * @param server instance of the server interface.
     * @throws IllegalArgumentException if provided command is not a JSON array or if "lab" command
     * was malformed.
     */
    private static void processCommand(ParsedElement command, LabyrinthServer server)
            throws IllegalArgumentException {
        String commandType = command.getName();
        switch (commandType) {
            case "lab":
                processLabyrinth(command, server);
                break;
            case "add":
            case "move":
                try {
                    processAddMove(command, server);
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
                return;
            default:
                throw new IllegalArgumentException("Command type must be one of \"lab\", \"add\", or \"move\".");
        }
    }

  /**
   * Processes "create labyrinth" command.
   * @param command parsed command containing "lab" as first element of array.
   * @param server instance of the server interface.
   * @throws IllegalArgumentException if lab command is malformed.
   */
    private static void processLabyrinth(ParsedElement command, LabyrinthServer server) throws IllegalArgumentException {
        JsonArray commandArray = command.getFullVersion().getAsJsonArray();
        if (commandArray.size() < 2) {
            throw new IllegalArgumentException("Labyrinth output must contain at least one edge.");
        }
        ArrayList<Pair<String, String>> edges = new ArrayList<>();
        for (int ii = 1; ii < commandArray.size(); ++ii) {
            edges.add(processEdge(commandArray.get(ii)));
        }
        server.setNodes(edges);
    }

  /**
   * Process edges inside of "lab" command.
   * @param edge JSON Object representing an edge.
   * @return Pair<String, String> reprseneting an edge.
   * @throws IllegalArgumentException if edge object is malformed.
   */
    private static Pair<String, String> processEdge(JsonElement edge) throws IllegalArgumentException {
        if (!edge.isJsonObject()) {
            throw new IllegalArgumentException("Edge data must be a Json object.");
        }
        JsonObject edgeObject = edge.getAsJsonObject();
        Set<String> edgeFields = edgeObject.keySet();
        if (edgeFields.size() != 2 || !edgeFields.contains("from") || !edgeFields.contains("to")) {
            throw new IllegalArgumentException("Edge data must be an object containing only the fields \"from\" " +
                                               "and \"to\".");
        }
        JsonElement from = edgeObject.get("from");
        JsonElement to = edgeObject.get("to");
        if (!from.isJsonPrimitive() || !from.getAsJsonPrimitive().isString() ||
                !to.isJsonPrimitive() || !to.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("\"from\" and \"to\" fields on edge must have string values.");
        }
        return new Pair<>(edgeObject.get("from").getAsJsonPrimitive().getAsString(),
                                        edgeObject.get("to").getAsJsonPrimitive().getAsString());
    }

  /**
   * Processes "add" or "move" command and invokes appropriate server method.
   * @param command parsed user command that either containes "add" or "move" as first element
   * @param server instance of server interface.
   * @throws IllegalArgumentException if "move" or "add" command is malformed.
   */
    private static void processAddMove(ParsedElement command, LabyrinthServer server) throws IllegalArgumentException {
        JsonArray commandArray = command.getFullVersion().getAsJsonArray();
        if (commandArray.size() != 3) {
            throw new IllegalArgumentException("For \"add\" and \"move\" operations, command must have " +
                                               "three entries.");
        }
        JsonElement second = commandArray.get(1);
        JsonElement third = commandArray.get(2);
        if (!second.isJsonPrimitive() || !second.getAsJsonPrimitive().isString() ||
            !third.isJsonPrimitive() || !third.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("For \"add\" and \"move\" operations, second and " +
                                               "third arguments must be strings.");
        }
        // We know the command must be of valid form now
        switch (command.getName()) {
            case "add":
                server.addToken(second.getAsJsonPrimitive().getAsString(),
                                third.getAsJsonPrimitive().getAsString());
                break;
            case "move":
                boolean result = server.getColoredPath(second.getAsJsonPrimitive().getAsString(),
                                                       third.getAsJsonPrimitive().getAsString());
                System.out.println(result);
                break;
            default:
        }
    }

    /**
     * Parses the string representing JSON ARRAY and produces collection of parsed JSON commands.
     * @param input String containing one or more commands from the user.
     * @return A collection of parsed JSON commands.
     */
    private static ArrayList<ParsedElement> parse(String input) {

        ArrayList<ParsedElement> parsedList = new ArrayList<>();
        JsonStreamParser jsonStreamParser = new JsonStreamParser(input);

        while (jsonStreamParser.hasNext()) {
            JsonElement jsonElement = jsonStreamParser.next();
            ParsedElement p;

            // Checks if the JsonObject is a List
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0 && jsonArray.get(0).isJsonPrimitive()
                && jsonArray.get(0).getAsJsonPrimitive().isString()) {
                    //map.put(jsonArray.get(0).getAsString(), jsonArray);
                    p = new ParsedElement(jsonArray.get(0).getAsString(), jsonArray);
                    parsedList.add(p);
                } else {
                    throw new IllegalArgumentException("The first element of a JSON array must be a string");
                }
            }
            else {
                throw new IllegalArgumentException("Command must be a JSON array");
            }
        }
        return parsedList;
    }
}


/**
 * Class representing parsed JSON element where firstElement represent a string to be used for sorting
 * and fullVersion is the full JSON element (Array, Object or String).
 */
class ParsedElement {
    private String name;
    private JsonElement fullVersion;

    /**
     * Constructs ParsedElement given first string and full version of JSON element.
     * @param name string to be used for sorting.
     * @param fullVersion full version of JSON element.
     */
    public ParsedElement (String name, JsonElement fullVersion){
        this.name = name;
        this.fullVersion = fullVersion;
    }

    public String getName() {
        return this.name;
    }

    public JsonElement getFullVersion() {
        return this.fullVersion;
    }
}

/**
 * Interface representing expected behaviour of labyrinth server.
 *
 * We had to revisit our initial traversal.md and revise signatures of our methods.
 * Detailed changes will be described in each method's java doc.
 */
interface LabyrinthServer {

  /**
   * Responsible for moving token over the labyrinth.
   * REVISED:
   * Method will be taking two String and doing all necessary computations to check whether received
   * token string matches allowed color names and string representing node name is both exists and
   * is reachable.
   * @param token String representing color of the token.
   * @param node String representing the name of the destination node
   * @return boolean representing whether requested move is possible.
   * @throws IllegalArgumentException if nodes has not been initialized, token of the specified
   * color has not been placed, or if given named node is not in nodes.
   */
    boolean getColoredPath(String token, String node) throws IllegalArgumentException;


  /**
   * Adds a token to a given node.
   * REVISED:
   * Method will be taking two String and doing all necessary computations to check whether received
   * token string matches allowed color names and string node name is both exists and
   * is not occupied yet.
   * @param token String representing color of the token.
   * @param node String representing the name of the destination node.
   * @throws IllegalArgumentException if nodes has not yet been initialized, the string representing
   * color is not one of the allowed colors, or if the node of the given name is not in nodes.
   */
    void addToken(String token, String node) throws IllegalArgumentException;


  /**
   * Creates the list of nodes that represent the current Labyrinth from the passed edge data.
   * REVISED:
   * This method substitutes behaviour we initially delegated to constructor.
   * Method takes edge data rather than node names and constructs nodes in Labyrinth with the
   * specified edges embedded within the nodes, as long as passed edges are pairwise distinct,
   * and that nodes has not already been initialized.
   * @param edges ArrayList<Pair<String, String>> representing collection of each node pair with an
   *              edge between them
   * @throws IllegalArgumentException if nodes has already been initialized, or if edges is not
   *         pairwise distinct
   */
    void setNodes (ArrayList<Pair<String, String>> edges) throws IllegalArgumentException;


}


/**
 * Class representing a single node from the labyrinth.
 */
class Node{
    String from;
    String to;
    Token token;

  /**
   * Constructs a node based on its two edges
   * @param from String representing from node.
   * @param to String representing to node.
   */
    public Node(String from, String to){
        this.to = to;
        this.from = from;
    }

  /**
   * Adds token to ths node.
   * @param token token color.
   */
    public void addToken(Token token){
        this.token = token;
    }
}


/**
 * Enum representing allowed token colors.
 * REVISED: Instead of creating class that contains solely enum of colors, we made token to be
 * enum itself.
 */
enum Token{
    white, black, red, green, blue
}

/**
 * Mock labyrinth server used to test client module.
 */
class MockLabyrinth implements LabyrinthServer{

    ArrayList<Node> nodes;

  /**
   * Construct a mock labyrinth server.
   */
    MockLabyrinth(){}

    @Override
    public void setNodes (ArrayList<Pair<String, String>> edges){
        System.out.println("Adding nodes to the labyrinth...");
    }

    @Override
    public boolean getColoredPath(String token, String node) {
        //return false;
        Random random = new Random();
        return random.nextBoolean();
    }

    @Override
    public void addToken(String token, String node) {
        System.out.println("Adding token " + token + " to a node " + node + "...");
        //return;
    }
}

