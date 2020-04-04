import java.io.IOException;

public class LabyrinthClientProgram {
  public static void main(String[] args) throws IOException {
    TCPClient client = new TCPClient();
    client.init(args);
    client.run();
  }
}
