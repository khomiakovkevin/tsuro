import java.util.ArrayList;

import com.google.gson.JsonArray;

public class Command {
  private JsonArray command;
  
  public Command(JsonArray command) {
    this.command = command;
  }
  
  public Command(ArrayList<Command> commands) {
    JsonArray batch = new JsonArray();
    for (Command command : commands) {
      batch.add(command.getCommand());
    }
    this.command = batch;
  }
  
  public String getType() {
    return this.command.get(0).getAsString();
  }
  
  public String getJsonText() {
    return this.command.toString();
  }
  
  private JsonArray getCommand() {
    return this.command;
  }
}
