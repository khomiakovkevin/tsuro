import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BoardTest {
  public static void main(String[] args) throws IOException {

    String input = new String(Files.readAllBytes(Paths.get("../../../../3/board-tests/5-in.json")));

    String output = new String(Files.readAllBytes(Paths.get("../../../../3/board-tests/5-out.json")));

    BoardTestProcessor.processTest(input, output);
  }
}
