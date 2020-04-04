import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class representing a handler for inputting the well-formatted strings and outputting them sorted
 */
public class IOHandler {
    private Socket socket = null;
    private BufferedReader reader;
    private ArrayList<ParsedElement> elements;
    private JsonHandler jsonHandler;

    /**
     * Constructor for our class, where we initialize the variables
     * @param reader std.in source of input
     */
    public IOHandler(BufferedReader reader) {
        this.reader = reader;
        this.elements = new ArrayList<ParsedElement>();
        this.jsonHandler = new JsonHandler();
    }

    /**
     * A function used to call the helper methods
     */
    public void run() {
        try {
            String input = this.readInput();
            this.elements = this.jsonHandler.parse(input);
            this.printOutput(input);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return;
        }

    }

    /**
     * A function used to read the input while the user is typing it into the console
     * @return  the concatenated input
     * @throws IOException an exception thrown if a reader cannot read this line
     */
    private String readInput() throws IOException {
        String input = "";
        String nextLine = "";
        while (!(nextLine.equals("Over"))) {
//            System.out.println("WAS HERERERERE" + nextLine);
            nextLine = reader.readLine();
            if (nextLine == null) {
                break;
            } else {
                input += nextLine;
            }
        }
        return input;
    }

    /**
     * Sorting the elements and outputting them
     */
    public void printOutput(String input) throws IOException {
        this.elements = this.jsonHandler.parse(input);
        Collections.sort(elements);
        for (ParsedElement pe : this.elements) {
            System.out.println(pe.toString());
        }
    }

}
