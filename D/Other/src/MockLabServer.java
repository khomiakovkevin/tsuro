import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is the entry point of our JSON parsing program.
 */
public class MockLabServer {

  private Socket socket = null;
  private ServerSocket server = null;
  private BufferedReader in = null;
  private PrintWriter out = null;
  private BufferedReader reader = null;
  //private IOHandler ioh = null;

  public MockLabServer(int port) throws SocketException {
    try {
      server = new ServerSocket(port);
      socket = server.accept();
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);

      IOHandler io = new IOHandler("");

      String line = "";

      while (!socket.isClosed()) {
        try {
          line = in.readLine();
          System.out.println(line);
          if (line == null) {
            socket.close();
            break;
          }
          ArrayList<JsonElement> arr = io.parse(line);
          System.out.println(arr.size());
          for (JsonElement el : arr) {
            if (el.getAsJsonArray().get(0).isJsonPrimitive()
            && el.getAsJsonArray().get(0).getAsString().equals("lab")) {
              createLab(el);
            } else if (el.getAsJsonArray().get(0).isJsonArray()) {
              processBatch(el);
            } else {
              processName(el);
            }
          }

        } catch (IOException i) {
          System.out.println(i);
        }
      }
      ///socket.close();
      //in.close();
      //out.close();
    } catch (IOException i) {
      System.out.println(i);
    }
  }

  private void createLab(JsonElement el) {
    System.out.println("Created lab ");
  }

  private void processBatch(JsonElement el) throws IOException {
    JsonArray firstAdd = null;
    boolean randBool = new Random().nextBoolean();
//    for (int i = 0; i < el.getAsJsonArray().size(); i++) {
//      if (el.getAsJsonArray().get(i).getAsJsonArray().get(0).equals("add")) {
//        firstAdd = el.getAsJsonArray().get(i).getAsJsonArray();
//      }
//    }
    System.out.println("Processed Batch ");
    String response = "[" + el.getAsJsonArray().get(0).getAsJsonArray().toString()
            + ", " + randBool + "]";
    System.out.println(response);
    out.println(response);
    out.flush();
  }

  private void processName(JsonElement el) throws IOException {
    System.out.println("Received Name: " + el.getAsString());
    out.println("#127.0.0.1");
    out.flush();
  }

  /**
   * Creates instance of Input/Output handler that will parse json and display input.
   *
   * @param args run-time arguments: -up or -down.
   */
  public static void main(String[] args) throws SocketException {
    MockLabServer server = new MockLabServer(8000);
  }
}

// ["lab", {"from" : A, "to" : B}]
// ["add", "blue", "a"]["move", "blue", "a"]