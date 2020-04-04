import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ObserverTestProcessor {
    public void processTest(String input) {

        // [ [color, tile-index, rotation, x, y, port], tile-index, tile-index, tile-index ]

        // [ [color, tile-index, rotation, x, y], tile-index, tile-index ]


        HashMap<String, Port.PortName> testPortstoOurs = new HashMap<>();
        testPortstoOurs.put("G", Port.PortName.W1);
        testPortstoOurs.put("H", Port.PortName.W2);
        testPortstoOurs.put("A", Port.PortName.N1);
        testPortstoOurs.put("B", Port.PortName.N2);
        testPortstoOurs.put("C", Port.PortName.E1);
        testPortstoOurs.put("D", Port.PortName.E2);
        testPortstoOurs.put("E", Port.PortName.S1);
        testPortstoOurs.put("F", Port.PortName.S2);

        HashMap<Port.PortName, String> oursToTestPorts = new HashMap<>();
        oursToTestPorts.put(Port.PortName.W1, "G");
        oursToTestPorts.put(Port.PortName.W2, "H");
        oursToTestPorts.put(Port.PortName.N1, "A");
        oursToTestPorts.put(Port.PortName.N2, "B");
        oursToTestPorts.put(Port.PortName.E1, "C");
        oursToTestPorts.put(Port.PortName.E2, "D");
        oursToTestPorts.put(Port.PortName.S1, "E");
        oursToTestPorts.put(Port.PortName.S2, "F");

        MockReferee ref = new MockReferee();

        ArrayList<Tiles> all35tiles = Tiles.all35Tiles();
        ArrayList<JsonArray> commands = parse(input);

        for (JsonArray array : commands) {
            for (JsonElement element : array) {
                JsonArray curEl = element.getAsJsonArray();
                JsonArray firstEl = curEl.get(0).getAsJsonArray();
                if (curEl.get(0).getAsJsonArray().size() == 6) {
                    int row = firstEl.get(3).getAsInt();
                    int col = firstEl.get(4).getAsInt();
                    Tiles tile = all35tiles.get(firstEl.get(1).getAsInt());
                    tile.rotate(firstEl.get(2).getAsInt());
                    Port.PortName portName = testPortstoOurs.get(firstEl.get(5).getAsString());
                    String color = firstEl.get(0).getAsString();

                    ArrayList<Tiles> options = new ArrayList<>();

                    options.add(all35tiles.get(curEl.get(1).getAsInt()));
                    options.add(all35tiles.get(curEl.get(2).getAsInt()));
                    options.add(all35tiles.get(curEl.get(3).getAsInt()));

                    ref.processInitPlacement(row, col, tile, color, options, portName);


                } else {
                    IPlayer player = ref.getPlayer(firstEl.get(0).getAsString());
                    Tiles tile = all35tiles.get(firstEl.get(1).getAsInt());
                    tile.rotate(firstEl.get(2).getAsInt());
                    int row = firstEl.get(3).getAsInt();
                    int col = firstEl.get(4).getAsInt();

                    ArrayList<Tiles> optionsInter = new ArrayList<>();
                    optionsInter.add(all35tiles.get(curEl.get(1).getAsInt()));
                    optionsInter.add(all35tiles.get(curEl.get(2).getAsInt()));

                    ref.processIntermediatePlacement(row, col, tile, player, optionsInter);

                }
            }

        }
        
        Observer obs = new Observer(ref.getBoard());

        HandPanel handPanel = new HandPanel(ref.getCurOptions(), ref.getSelected());
        obs.add(handPanel, BorderLayout.EAST);

        JLabel playerTurn = new JLabel("Player Turn: " + ref.getCurPlayer());
        playerTurn.setFont(playerTurn.getFont().deriveFont(24.0f));
        playerTurn.setForeground(Color.BLUE);
        obs.add(playerTurn, BorderLayout.PAGE_START);
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
