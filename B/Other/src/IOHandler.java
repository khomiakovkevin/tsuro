import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class representing a handler for inputting the well-formatted strings and outputting them sorted
 */
public class IOHandler {
    private BufferedReader reader;
    private boolean sortAscending;
    private ArrayList<ParsedElement> elements;
    private JsonHandler jsonHandler;

    /**
     * Constructor for our class, where we initialize the variables
     * @param reader std.in source of input
     * @param args arguments for our I/O handler
     */
    public IOHandler(BufferedReader reader, String[] args) {
        this.reader = reader;
        this.sortAscending = processArgs(args);
        this.elements = new ArrayList<ParsedElement>();
        this.jsonHandler = new JsonHandler();
    }

    /**
     * A function used to call the helper methods
     */
    public void run() {
        String input;
        try {
            input = this.readInput();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return;
        }
        this.elements = this.jsonHandler.parse(input);
        this.printOutput();
    }

    /**
     * A function used to read the input while the user is typing it into the console
     * @return  the concatenated input
     * @throws IOException an exception thrown if a reader cannot read this line
     */
    private String readInput() throws IOException {
        String input = "";
        String nextLine;
        while (true) {
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
    private void printOutput() {
        Collections.sort(this.elements);
        if (!this.sortAscending) {
            Collections.reverse(this.elements);
        }
        for (ParsedElement pe : this.elements) {
          System.out.println(pe.toString());
        }
    }

    /**
     * A function used to read the function arguments and based on them decide
     * if need to sort in ascending or descending order
     * @param args inputs the function arguments
     * @return a boolean representing which sort will the program use later on
     */
    private boolean processArgs(String[] args) {
        boolean sortAscending = true;
        if (args.length > 0 && args[0].equals("-up")) {
            sortAscending = true;
        } else if (args.length > 0 && args[0].equals("-down")) {
            sortAscending = false;
        } else {
            sortAscending = true;
            System.out.println("Defaulting to ascending sort order.");
        }

        if (args.length > 1) {
            System.out.println("Please only input one of \"-up\" or \"-down\" on the command line.");
        }

        return sortAscending;
    }
}
