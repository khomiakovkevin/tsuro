import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardTestProcessor {

    public static void processTest (String input, String output) {
        HashMap<String, Port.PortName> testPortstoOurs = new HashMap<>();
        testPortstoOurs.put("G", Port.PortName.W1 );
        testPortstoOurs.put("H", Port.PortName.W2 );
        testPortstoOurs.put("A", Port.PortName.N1 );
        testPortstoOurs.put("B", Port.PortName.N2 );
        testPortstoOurs.put("C", Port.PortName.E1 );
        testPortstoOurs.put("D", Port.PortName.E2 );
        testPortstoOurs.put("E", Port.PortName.S1 );
        testPortstoOurs.put("F", Port.PortName.S2 );

        HashMap<Port.PortName, String> oursToTestPorts = new HashMap<>();
        oursToTestPorts.put(Port.PortName.W1, "G");
        oursToTestPorts.put(Port.PortName.W2, "H");
        oursToTestPorts.put(Port.PortName.N1, "A");
        oursToTestPorts.put(Port.PortName.N2, "B");
        oursToTestPorts.put(Port.PortName.E1, "C");
        oursToTestPorts.put(Port.PortName.E2, "D");
        oursToTestPorts.put(Port.PortName.S1, "E");
        oursToTestPorts.put(Port.PortName.S2, "F");

        Board board = new Board();



        ArrayList<Tiles> all35tiles = Tiles.all35Tiles();
        ArrayList<JsonArray> commands = parse(input);

        for (JsonArray array: commands){
            for (JsonElement element: array){
                JsonArray curEl = element.getAsJsonArray();
                // Check if command is initial placement
                if (curEl.size() == 6){
                    int row = curEl.get(4).getAsInt();
                    int col = curEl.get(5).getAsInt();
                    Tiles tile = all35tiles.get(curEl.get(0).getAsInt());
                    tile.rotate(curEl.get(1).getAsInt());
                    Port.PortName portName = testPortstoOurs.get(curEl.get(3).getAsString());
                    Avatar avatar = new Avatar(curEl.get(2).getAsString());


                    board.initialPlacement(row, col,
                            tile, portName,avatar );

                } else {
                    // public void placeTile(int row, int column, Tiles tile, Avatar currentPlayer, boolean firstMove)
                    Avatar avatar = board.getRef().getPlayer(curEl.get(0).getAsString()).getAvatar();
                    Tiles tile = all35tiles.get(curEl.get(1).getAsInt());
                    tile.rotate(curEl.get(2).getAsInt());
                    int row = curEl.get(3).getAsInt();
                    int col = curEl.get(4).getAsInt();

                    board.placeTile(row, col, tile, avatar, false);

                }
            }
            JsonArray allPositions = board.getAllPlayersPosition(oursToTestPorts);

            boolean passed = allPositions.toString().equals(output);

            if (passed) {
                System.out.println("Test passed!" + '\n' + "Output: " + '\n' + allPositions);
            } else {
                System.out.println("Test failed!" + '\n' + "Actual: " + '\n' + allPositions + '\n' +
                        "Expected:" + '\n' + output);
            }

        }
    }

    private static ArrayList<JsonArray> parse(String input) {
        ArrayList<JsonArray> parsedList = new ArrayList<>();
        JsonStreamParser parser = new JsonStreamParser(input);
        while (parser.hasNext()) {
            try {
                parsedList.add(parser.next().getAsJsonArray());
            } catch (JsonParseException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return parsedList;
    }


}
