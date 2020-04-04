import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ObserverTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Program must be invoked with only a single argument,"
                    + " representing the name of the test file to be used.");
        }

        String path;
        String fileName = args[0];

        boolean compiling = true;
        if (compiling) {
            path = "../../../../5/obs-tests/" + fileName;
        } else {
            path = "./Tsuro/5/obs-tests/" + fileName;
        }

        String input = new String(Files.readAllBytes(Paths.get(path)));
        System.out.println(input);

      ObserverTestProcessor observerTest = new ObserverTestProcessor();
      observerTest.processTest(input);
    }
}
