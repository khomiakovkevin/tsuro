import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This is the entry point of our JSON parsing program.
 */
public class JsonParserProgram {

    /**
     * Creates instance of Inpot/Output handler that will parse json and display input.
     * @param args run-time arguments: -up or -down.
     */
    public static void main (String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        IOHandler io = new IOHandler(reader, args);
        io.run();
    }
}
