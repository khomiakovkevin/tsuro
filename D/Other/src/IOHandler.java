import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;

public class IOHandler {
  private BufferedReader reader;
  private boolean hasNext;
  private String name;
  
  public IOHandler(String name) {
    this.reader = new BufferedReader(new InputStreamReader(System.in));
    this.hasNext = true;
    this.name = name;
  }
  
  public ArrayList<Command> readLine() throws IOException {
    String nextLine;
    nextLine = this.reader.readLine();
    if (nextLine == null) {
      this.hasNext = false;
      return null;
    } else {
      System.out.println(nextLine);
      return this.parseValid(nextLine);
    }
  }
  
  public boolean hasNext() {
    return this.hasNext;
  }
  
  public void handleResponse(String response) throws IOException {
    ArrayList<JsonElement> responseElements = parse(response);
    for (JsonElement message : responseElements) {
      handleMessage(message);
    }
  }
  
  private void handleMessage(JsonElement message) throws IOException {
    if (CommandValidator.isJsonString(message)) {
      System.out.println("[\"the server will call me\", " + this.name + "]");
    } else if (message.isJsonArray()) {
      responsePrinter(message.getAsJsonArray());
    } else {
      throw new IOException("Malformed response from server: " + message.toString());
    }
  }
  
  private void responsePrinter(JsonArray response) throws IOException {
    for (int ii = 0; ii < response.size() - 1; ++ii) {
      malformedAddPrinter(response.get(ii));
    }
    queryResponsePrinter(response.get(response.size() - 1));
  }
  
  private void malformedAddPrinter(JsonElement malformed) {
    System.out.println("[\"invalid\", " + malformed.toString() + "]");
  }
  
  private void queryResponsePrinter(JsonElement response) throws IOException {
    if (!response.isJsonPrimitive() || !response.getAsJsonPrimitive().isBoolean()) {
      throw new IOException("Malformed query response from server: " + response.toString());
    }
    System.out.println("[\"the response to\", QQ, \"is\", " + response.toString() + "]");
  }
  
  private ArrayList<Command> parseValid(String input) {
    ArrayList<JsonElement> parsedElements = this.parse(input);
    
    ArrayList<JsonElement> invalid = parsedElements.stream()
            .filter(jsonElement -> !CommandValidator.validate(jsonElement))
            .collect(Collectors.toCollection(ArrayList::new));
    
    for (JsonElement element : invalid) {
      System.out.println("[\"not a request\", " + element.toString() + "]");
    }
    
    ArrayList<Command> commands = parsedElements.stream()
            .filter(CommandValidator::validate)
            .map(JsonElement::getAsJsonArray)
            .map(CommandValidator::command)
            .collect(Collectors.toCollection(ArrayList::new));
    
    return commands;
  }
  
  public ArrayList<JsonElement> parse(String input) {
    ArrayList<JsonElement> parsedList = new ArrayList<JsonElement>();
    JsonStreamParser parser = new JsonStreamParser(input);
    JsonElement element;
    while (parser.hasNext()) {
      try {
        parsedList.add(parser.next());
      } catch (JsonParseException ex) {
        System.out.println(ex.getMessage());
      }
    }
    return parsedList;
  }
}
