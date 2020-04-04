import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CommandValidator {
  public static boolean validate(JsonElement element) {
    if (!element.isJsonArray()) {
      return false;
    }
    JsonArray array = element.getAsJsonArray();
    
    return validLabCreation(array) ||
            validOtherCommand(array, "add") ||
            validOtherCommand(array, "move");
  }
  
  public static Command command(JsonArray array) {
    String name = array.get(0).getAsString();
    if (name.equals("lab")) {
      return processLab(array);
    }
    return new Command(array);
  }
  
  public static boolean isJsonString(JsonElement el) {
    return el.isJsonPrimitive() && el.getAsJsonPrimitive().isString();
  }
  
  private static boolean validLabCreation(JsonArray array) {
    if (array.size() < 2) {
      return false;
    }
    JsonElement first = array.get(0);
    boolean validCommand = isJsonString(first) && first.getAsString().equals("lab");
    for (int ii = 1; ii < array.size(); ++ii) {
      validCommand &= validEdge(array.get(ii));
    }
    return validCommand;
  }
  
  private static boolean validEdge(JsonElement edge) {
    if (!edge.isJsonObject()) {
      return false;
    }
    JsonObject edgeObj = edge.getAsJsonObject();
    if (edgeObj.size() != 2 ||
            !edgeObj.has("from") ||
            !edgeObj.has("to")) {
      return false;
    }
    JsonElement from = edgeObj.get("from");
    JsonElement to = edgeObj.get("to");
    return isJsonString(from) && isJsonString(to);
  }
  
  private static boolean validOtherCommand(JsonArray array, String commandName) {
    if (array.size() != 3) {
      return false;
    }
    JsonElement first = array.get(0);
    boolean validName = isJsonString(first) && first.getAsString().equals(commandName);
    boolean validColor = isColorString(array.get(1));
    return validName && validColor && isJsonString(array.get(2));
  }
  
  private static boolean isColorString(JsonElement element) {
    if (!isJsonString(element)) {
      return false;
    }
    switch (element.getAsString()) {
      case "white":
      case "black":
      case "red":
      case "green":
      case "blue":
        return true;
      default:
        return false;
    }
  }
  
  private static Command processLab(JsonArray array) {
    ArrayList<Pair<String, String>> edges = new ArrayList<Pair<String, String>>();
    for (int ii = 1; ii < array.size(); ++ii) {
      JsonObject edge = array.get(ii).getAsJsonObject();
      String from = edge.get("from").getAsString();
      String to = edge.get("to").getAsString();
      edges.add(new Pair<String, String>(from, to));
    }
    
    ArrayList<String> nodes = new ArrayList<String>();
    JsonArray edgesArray = new JsonArray();
    
    for (Pair<String, String> edge : edges) {
      JsonArray edgeArray = new JsonArray();
      edgeArray.add(edge.getKey());
      nodes.add(edge.getKey());
      edgeArray.add(edge.getValue());
      nodes.add(edge.getValue());
      edgesArray.add(edgeArray);
    }
    
    nodes = nodes.stream()
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));
    JsonArray nodesArray = new JsonArray();
    for (String node : nodes) {
      nodesArray.add(node);
    }
    
    JsonArray command = new JsonArray();
    command.add("lab");
    command.add(nodesArray);
    command.add(edgesArray);
    return new Command(command);
  }
}
