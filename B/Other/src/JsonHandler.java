import com.google.gson.*;

import java.util.ArrayList;

/**
 * Utility class containing necessary method for parsing JSON and rendering output.
 */
public class JsonHandler {

    /**
     * Constructs instance of JsonHandler.
     */
    public JsonHandler() {
    }


    /**
     * Parses the string representing JSON elements and produces collection of parsde JSON elements
     * @param input String containing all inputs from user before input source was closed.
     * @return A collection of parsed JSON elements.
     */
    public ArrayList<ParsedElement> parse(String input) {

        ArrayList<ParsedElement> parsedList = new ArrayList<ParsedElement>();

        JsonStreamParser jsonStreamParser = new JsonStreamParser(input);

        while (jsonStreamParser.hasNext()) {
            JsonElement jsonElement = jsonStreamParser.next();
            ParsedElement p;
            // Checks if the JsonObject is a dictionary
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.keySet().contains("this") && jsonObject.get("this").getAsJsonPrimitive().isString()) {
                    //map.put(jsonObject.get("this").getAsString(), jsonObject);
                    p = new ParsedElement(jsonObject.get("this").getAsString(), jsonObject);
                    parsedList.add(p);
                } else {
                    throw new IllegalArgumentException("The JSON dictionary object must have a 'this' key with a string");
                }
            }
            // Checks if the JsonObject is a List
            else if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0 && jsonArray.get(0).getAsJsonPrimitive().isString()) {
                    //map.put(jsonArray.get(0).getAsString(), jsonArray);
                    p = new ParsedElement(jsonArray.get(0).getAsString(), jsonArray);
                    parsedList.add(p);
                } else {
                    throw new IllegalArgumentException("The first element of a JSON array must be a string");
                }
            }
            // Checks if the JsonObject is a string
            else if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isString()) {
                    //map.put(jsonPrimitive.getAsString(), jsonPrimitive);
                    p = new ParsedElement(jsonPrimitive.getAsString(), jsonPrimitive);
                    parsedList.add(p);
                } else {
                    throw new IllegalArgumentException("The JSON Primitive must be a string");
                }
            } else {
                throw new IllegalArgumentException("Please pass valid JSON Objects: dictionaries, lists, or strings");
            }
        }

        return parsedList;

    }
}
