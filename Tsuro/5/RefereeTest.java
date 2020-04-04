import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RefereeTest {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Program must be invoked with only a single argument,"
              + " representing the name of the test file to be used.");
    }
    
    String path;
    String fileName = args[0];
    
    boolean compiling = false;
    if (compiling) {
      path = "../../../../5/ref-tests/" + fileName;
    } else {
      path = "./Tsuro/5/ref-tests/" + fileName;
    }
    
    String input = new String(Files.readAllBytes(Paths.get(path)));
    RefereeTestProcessor refereeTest = new RefereeTestProcessor();
    refereeTest.processTest(input);
  }
}
