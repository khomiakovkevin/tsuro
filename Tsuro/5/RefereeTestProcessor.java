import com.google.gson.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RefereeTestProcessor {
  private Referee referee;
  
  public RefereeTestProcessor() {
    this.referee = new Referee();
  }
  
  public void processTest(String input) {
    ArrayList<JsonElement> parsedInput = parse(input);
    JsonArray validInput;
    if (validateInput(parsedInput)) {
      validInput = (JsonArray) parsedInput.get(0);
    } else {
      throw new IllegalArgumentException("Input must consist solely of a " +
                                         "Json Array of 3-5 strings.");
    }
    processInput(validInput);
  }
  
  private void processInput(JsonArray validInput) {
    ArrayList<String> allColors = Avatar.AvatarColor.getAllColors();
    int numPlayers = validInput.size();
    for (int ii = 0; ii < numPlayers; ++ii) {
      String name = validInput.get(ii).getAsString();
      this.referee.addPlayer(name, numPlayers - ii, allColors.get(ii));
    }
    this.referee.runGame();
    processOutput();
  }
  
  private void processOutput() {
    ArrayList<ArrayList<IPlayer>> eliminated = this.referee.getEliminated();
    ArrayList<IPlayer> kicked = this.referee.getKicked();
    JsonArray eliminatedJson = getEliminatedJson(eliminated);
    JsonArray kickedJson = getKickedJson(kicked);
    JsonObject output = new JsonObject();
    output.add("winners", eliminatedJson);
    output.add("losers", kickedJson);
    System.out.println(output.toString());
    Observer observer = new Observer(referee.board);
  }
  
  private static JsonArray getEliminatedJson(ArrayList<ArrayList<IPlayer>> eliminated) {
    ArrayList<JsonArray>  eliminatedTiers = map(eliminated, RefereeTestProcessor::getKickedJson);
    Collections.reverse(eliminatedTiers);
    return arrayListToJsonArray(eliminatedTiers);
  }
  
  private static JsonArray getKickedJson(ArrayList<IPlayer> kicked) {
    ArrayList<String> kickedNames = map(kicked, IPlayer::getPlayerName);
    Collections.sort(kickedNames);
    ArrayList<JsonPrimitive> kickedNamesJson = map(kickedNames, JsonPrimitive::new);
    return arrayListToJsonArray(kickedNamesJson);
  }
  
  private static <T, U> ArrayList<U> map(ArrayList<T> input,
                                                   Function<T, U> mapper) {
    return input.stream()
                .map(mapper)
                .collect(Collectors.toCollection(ArrayList::new));
  }
  
  private static <T extends JsonElement> JsonArray arrayListToJsonArray(ArrayList<T> arrayList) {
    JsonArray jsonArray = new JsonArray();
    for (T element : arrayList) {
      jsonArray.add(element);
    }
    return jsonArray;
  }
  
  private static ArrayList<JsonElement> parse(String input) {
    ArrayList<JsonElement> parsedList = new ArrayList<>();
    JsonStreamParser parser = new JsonStreamParser(input);
    while (parser.hasNext()) {
      try {
        parsedList.add(parser.next());
      } catch (JsonParseException ex) {
        System.out.println(ex.getMessage());
      }
    }
    return parsedList;
  }
  
  private static boolean validateInput(ArrayList<JsonElement> parsedInput) {
    if (parsedInput.size() == 1 && parsedInput.get(0).isJsonArray()) {
      boolean inputIsValid = true;
      JsonArray input = parsedInput.get(0).getAsJsonArray();
      for (JsonElement element : input) {
        inputIsValid &= element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
      }
      inputIsValid &= input.size() >= 3 && input.size() <= 5;
      return inputIsValid;
    } else {
      return false;
    }
  }
  
  
}
