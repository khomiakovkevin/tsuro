import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * A validator for potential commands.
 */
public class CommandValidator {
  /**
   * Determines whether the passed JsonElement represents a valid command.
   * @param commandElement The JsonElement to be queried.
   * @return True if the JsonElement represents a valid command.
   */
  public static boolean validCommand(JsonElement commandElement) {
    try {
      JsonArray command = getAsJsonArray(commandElement);
      return validInitialPlacementCommand(command) ||
              validIntermediatePlacementCommand(command) ||
              validTurnSpecificationCommand(command);
    } catch (IllegalArgumentException ex) {
      System.out.println(ex.getMessage());
      return false;
    }
  }
  
  /**
   * Converts a JsonElement into an integer.
   * @param element The JsonElement to be converted.
   * @return The integer that the passed JsonElement represents.
   * @throws IllegalArgumentException if the passed JsonElement cannot be
   *                                  converted to an integer.
   */
  private static int getAsInt(JsonElement element)
          throws IllegalArgumentException {
    return getAsPrimitiveType(element, JsonPrimitive::isNumber,
            JsonPrimitive::getAsInt, "n int");
  }
  
  // TODO: Move above
  /**
   * Converts a JsonElement into a JsonArray.
   * @param element The JsonElement to be converted.
   * @return The converted JsonElement, now a JsonArray.
   * @throws IllegalArgumentException if element cannot be parsed as an array.
   */
  public static JsonArray getAsJsonArray(JsonElement element)
          throws IllegalArgumentException {
    if (element.isJsonArray()) {
      return element.getAsJsonArray();
    } else {
      throw new IllegalArgumentException("Cannot get the JsonElement " +
              element.toString() + " as a JsonArray.");
    }
  }
  
  
  /**
   * Converts a JsonElement into a String.
   * @param element The JsonElement to be converted.
   * @return The converted form of element into the String type.
   * @throws IllegalArgumentException if element cannot be parsed as a String.
   */
  private static String getAsString(JsonElement element)
          throws IllegalArgumentException{
    return getAsPrimitiveType(element, JsonPrimitive::isString,
            JsonPrimitive::getAsString, " String");
  }
  
  /**
   * Converts a JsonElement into a given primitive Java type.
   * @param element The JsonElement to be converted.
   * @param typePredicate A function that determines whether a JsonPrimitive is
   *                      of the correct target type.
   * @param typeConverter A function that converts a JsonPrimitive to the
   *                      correct target type.
   * @param typeName A String representing the name of the target type, preceded
   *                 by a space and perhaps an n, in case the type begins with a
   *                 vowel.
   * @param <T> The target type.
   * @return The converted form of element into the target type.
   * @throws IllegalArgumentException if element cannot be parsed as a member of
   *                                  the target type.
   */
  private static <T> T getAsPrimitiveType(JsonElement element,
                                          Predicate<JsonPrimitive> typePredicate,
                                          Function<JsonPrimitive, T> typeConverter,
                                          String typeName)
          throws IllegalArgumentException {
    if (element.isJsonPrimitive()) {
      JsonPrimitive primitive = element.getAsJsonPrimitive();
      if (typePredicate.test(primitive)) {
        return typeConverter.apply(primitive);
      }
    }
    throw new IllegalArgumentException("Cannot get the JsonElement " +
            element.toString() + " as a" + typeName + ".");
  }
  
  /**
   * Determines whether the passed JsonElement represents a value inside the
   * specified closed interval.
   * @param queryElement The JsonElement to be queried.
   * @param lowerBound The inclusive lower bound of the interval.
   * @param upperBound The inclusive upper bound of the interval.
   * @return True if queryElement represents a value inside the specified closed
   *         interval.
   * @throws IllegalArgumentException if queryElement cannot be parsed as an
   *                                  integer.
   */
  private static boolean liesInClosedInterval(JsonElement queryElement,
                                              int lowerBound, int upperBound)
          throws IllegalArgumentException {
    int query = getAsInt(queryElement);
    return lowerBound <= query && query <= upperBound;
  }
  
  /**
   * Determines whether the passed JsonElement represents a valid position on
   * the Board.
   * @param boardPositionElement The JsonElement to be queried.
   * @return True if boardPositionElement represents a valid position on the
   *         Board.
   * @throws IllegalArgumentException if boardPositionElement cannot be parsed
   *                                  as an integer.
   */
  private static boolean validBoardPosition(JsonElement boardPositionElement)
          throws IllegalArgumentException {
    return liesInClosedInterval(boardPositionElement, 0,
            9);
  }
  
  /**
   * Determines whether the passed JsonElement represents a valid color String.
   * @param colorStringElement The JsonElement to be queried.
   * @return True if colorStringElement represents a valid color String.
   * @throws IllegalArgumentException if colorStringElement cannot be parsed as
   *                                  a String.
   */
  private static boolean validColorString(JsonElement colorStringElement)
          throws IllegalArgumentException {
    String colorString = getAsString(colorStringElement);
    switch (colorString) {
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
  
  /**
   * Determines whether the passed JsonArray represents a valid initial
   * placement command.
   * @param command The JsonArray to be queried.
   * @return True if command does in fact represent a valid initial placement
   *         command.
   * @throws IllegalArgumentException if one of the entries of the command is
   *                                  malformed.
   */
  private static boolean validInitialPlacementCommand(JsonArray command)
          throws IllegalArgumentException {
    if (command.size() == 6) {
      boolean validTileIndex = validTileIndex(command.get(0));
      boolean validRotationAngle = validRotationAngle(command.get(1));
      boolean validColorString = validColorString(command.get(2));
      boolean validPortName = validPortName(command.get(3));
      boolean validXPosition = validBoardPosition(command.get(4));
      boolean validYPosition = validBoardPosition(command.get(5));
      return validTileIndex && validRotationAngle && validColorString &&
              validPortName && validXPosition && validYPosition;
    } else {
      return false;
    }
  }
  
  /**
   * Determines whether the passed JsonArray represents a valid intermediate
   * placement command.
   * @param command The JsonArray to be queried.
   * @return True if command does in fact represent a valid intermediate
   *         placement command.
   * @throws IllegalArgumentException if one of the entries of the command is
   *                                  malformed.
   */
  private static boolean validIntermediatePlacementCommand(JsonArray command)
          throws IllegalArgumentException {
    if (command.size() == 5) {
      boolean validColorString = validColorString(command.get(0));
      boolean validTileIndex = validTileIndex(command.get(1));
      boolean validRotationAngle = validRotationAngle(command.get(2));
      
      return validColorString && validTileIndex && validRotationAngle;
    } else {
      return false;
    }
  }
  
  /**
   * Determines whether the passed JsonElement represents a valid intermediate
   * placement command.
   * @param commandElement The JsonElement to be queried.
   * @return True if command does in fact represent a valid intermediate
   *         placement command.
   * @throws IllegalArgumentException if either commandElement cannot be parsed
   * as a JsonArray, or if the resulting command itself is malformed.
   */
  private static boolean validIntermediatePlacementCommand(JsonElement commandElement)
          throws IllegalArgumentException {
    JsonArray command = getAsJsonArray(commandElement);
    return validIntermediatePlacementCommand(command);
  }
  
  /**
   * Determines whether the passed JsonElement represents a valid Port name.
   * @param portNameElement The JsonElement to be queried.
   * @return True if portNameElement represents a valid Port name.
   * @throws IllegalArgumentException if portNameElement cannot be parsed as a
   *                                  String.
   */
  private static boolean validPortName(JsonElement portNameElement)
          throws IllegalArgumentException {
    String portName = getAsString(portNameElement);
    switch (portName) {
      case "A":
      case "B":
      case "C":
      case "D":
      case "E":
      case "F":
      case "G":
      case "H":
        return true;
      default:
        return false;
    }
  }
  
  /**
   * Determines whether the passed JsonElement represents a valid rotation angle
   * value (i.e. one of 0, 90, 180, 270).
   * @param rotationAngleElement The JsonElement to be queried.
   * @return True if rotationAngleElement represents a valid rotation angle.
   * @throws IllegalArgumentException if rotationAngleElement cannot be parsed
   *                                  as an integer.
   */
  private static boolean validRotationAngle(JsonElement rotationAngleElement)
          throws IllegalArgumentException {
    int rotationAngle = getAsInt(rotationAngleElement);
    return (rotationAngle == 0) || (rotationAngle == 90) ||
            (rotationAngle == 180) || (rotationAngle == 270);
  }
  
  /**
   * Determines whether the passed JsonElement represents a valid Tile index.
   * @param tileIndexElement The JsonElement to be queried.
   * @return True if tileIndexElement represents a valid Tile index
   *         (0 <= ii <= 34).
   * @throws IllegalArgumentException if tileIndexElement cannot be parsed as an
   *                                  integer.
   */
  private static boolean validTileIndex(JsonElement tileIndexElement)
          throws IllegalArgumentException {
    return liesInClosedInterval(tileIndexElement, -1, 34);
  }
  
  /**
   * Determines whether the passed JsonArray represents a valid turn
   * specification command.
   * @param command The JsonArray to be queried.
   * @return True if command represents a valid turn specification command.
   * @throws IllegalArgumentException if one of the entries of the command is
   *                                  malformed.
   */
  private static boolean validTurnSpecificationCommand(JsonArray command)
          throws IllegalArgumentException {
    if (command.size() == 3) {
      boolean validIntermediatePlacementCommand =
              validIntermediatePlacementCommand(command.get(0));
      boolean validTileIndex1 = validTileIndex(command.get(1));
      boolean validTileIndex2 = validTileIndex(command.get(2));
      return validIntermediatePlacementCommand && validTileIndex1 &&
              validTileIndex2;
    } else {
      return false;
    }
  }
}
