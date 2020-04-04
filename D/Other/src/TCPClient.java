import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class TCPClient {
  private String ip;
  private int port;
  private String name;
  private boolean labCreated;
  
  private Socket socket = null;
  private PrintWriter out;
  private BufferedReader in;
  private IOHandler ioh;
  private ArrayList<Command> commandBuffer;
  
  public TCPClient() {
    this.labCreated = false;
    this.commandBuffer = new ArrayList<>();
  }
  
  public void init(String[] args) {
    processRuntimeArgs(args);
    
    try {
      this.socket = new Socket(this.ip, this.port);
      System.out.println("Connected");
      this.out = new PrintWriter(socket.getOutputStream(), true);
      this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.ioh = new IOHandler(this.name);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void processRuntimeArgs(String[] args) {
    String ip = "127.0.0.1";
    String port = "8000";
    String name = "John Doe";
    switch (args.length) {
      default:
        System.out.println("Program will ignore any arguments past the ones " +
                "representing IP address, port number, and name.");
      case 3:
        name = args[2];
      case 2:
        port = args[1];
      case 1:
        ip = args[0];
        break;
      case 0:
        break;
    }
    processIP(ip);
    processPort(port);
    this.name = name;
  }
  
  private void processIP(String ip) {
    String[] nums = ip.split(".");
    if (nums.length != 4) {
      System.out.println("Invalid IP address. Defaulting to \"127.0.0.1\".");
      this.ip = "127.0.0.1";
      return;
    }
    for (int ii = 0; ii < 4; ++ii) {
      String numString = nums[ii];
      int num;
      try {
        num = Integer.parseInt(numString);
        if (!(num >= 0 && num <= 255)) {
          System.out.println("Invalid IP address. Defaulting to \"127.0.0.1\".");
          this.ip = "127.0.0.1";
          return;
        }
      } catch (NumberFormatException ex) {
        System.out.println("Invalid IP address. Defaulting to \"127.0.0.1\".");
        this.ip = "127.0.0.1";
        return;
      }
    }
    this.ip = ip;
  }
  
  private void processPort(String port) {
    int portNum;
    try {
      portNum = Integer.parseInt(port);
      if (!(portNum >= 0 && portNum <= 65535)) {
        System.out.println("Invalid port number. Defaulting to 8000.");
        this.port = 8000;
        return;
      }
    } catch (NumberFormatException ex) {
      System.out.println("Invalid port number. Defaulting to 8000.");
      this.port = 8000;
      return;
    }
    this.port = portNum;
  }
  
  public void run() {
    while (this.ioh.hasNext()) {
      try {
        ArrayList<Command> currCommands = this.ioh.readLine();
        if (currCommands != null && currCommands.size() > 0) {
          if (!this.labCreated) {
            if (currCommands.get(0).getType().equals("lab")) {
              sendCommand(currCommands.get(0));
              currCommands.remove(0);
              this.labCreated = true;
            } else {
              this.socket.close();
            }
          }
          
          addToBuffer(currCommands);
        }
        
        System.out.println(this.commandBuffer);
        Command batchCommand = formBatch();
        while (batchCommand != null) {
          System.out.println(this.commandBuffer);
          sendCommand(batchCommand);
          batchCommand = formBatch();
        }
        
        // Functional server response interpreter, but commented out due to malfunction with our own mock server
        // checkForServerResponse();
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println("hasNext: " + this.ioh.hasNext());
    }
    
    if (commandBuffer.size() != 0) {
      System.out.println("Make sure to follow all add commands up with a move command.");
    }
  }
  
  private void addToBuffer(ArrayList<Command> commands) throws IOException {
    for (Command command : commands) {
      if (command.getType().equals("lab")) {
        this.socket.close();
        this.out.close();
        break;
      } else {
        this.commandBuffer.add(command);
      }
    }
  }
  
  private Command formBatch() {
    ArrayList<Command> batch = new ArrayList<>();
    for (Command command : this.commandBuffer) {
      if (command.getType().equals("move")) {
        batch.add(command);
        int commandIndex = this.commandBuffer.indexOf(command);
        List<Command> remainder = this.commandBuffer.subList(commandIndex + 1, this.commandBuffer.size());
        this.commandBuffer = new ArrayList<>(remainder);
        return new Command(batch);
      } else {
        batch.add(command);
      }
    }
    return null;
  }
  
  private void sendCommand(Command command) throws IOException {
    this.out.println(command.getJsonText());
  }
  
  private void checkForServerResponse() throws IOException {
    System.out.println("Checking server response");
    String line = this.in.readLine();
    System.out.println("Fetched server response");
    System.out.println(line);
    if (line != null && !line.equals("")) {
      this.ioh.handleResponse(line);
    }
  }
}