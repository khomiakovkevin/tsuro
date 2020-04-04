import java.awt.*;

/**
 * Represents a location on a Tsuro Board, with x and y coordinates and a
 * Port.PortName for whichever Tiles is on the Board at those coordinates.
 */
public class Location {
  private int row;
  private int col;
  private Port.PortName portName;
  
  /**
   * Standard constructor for a Location.
   *
   * @param row The row of this location.
   * @param col The col of this location.
   * @param portName The portName of this location.
   */
  public Location(int row, int col, Port.PortName portName) {
    this.row = row;
    this.col = col;
    this.portName = portName;
  }
  
  public Location(Avatar avatar) {
    this.row = avatar.getPos().y;
    this.col = avatar.getPos().x;
    this.portName = avatar.getCurrentPort();
  }
  
  /**
   * Gets the x coordinate of this Location.
   *
   * @return The x coordinate of this Location.
   */
  public int getRow() {
    return row;
  }
  
  /**
   * Gets the y coordinate of this Location.
   *
   * @return The y coordinate of this Location.
   */
  public int getCol() {
    return col;
  }
  
  /**
   * Gets the Port.PortName of this Location.
   *
   * @return The Port.PortName of this Location.
   */
  public Port.PortName getPortName() {
    return portName;
  }
  
  /**
   * Tells which Location is immediately adjacent to this one.
   *
   * @return The Location immediately adjacent to this one.
   */
  public Location advance() {
    // Initialize x and y coordinates
    int newRow = this.row;
    int newCol = this.col;
    
    /*
      Depending on the direction of this.portName, modifies the coordinate
      indices.
     */
    switch (this.portName.direction()) {
      case "N":
        newRow += 1;
        break;
      case "E":
        newCol += 1;
        break;
      case "S":
        newRow -= 1;
        break;
      case "W":
        newCol -= 1;
        break;
      default:
        return null;
    }
    
    // Return the modified coordinates and advanced port of this Location.
    return new Location(newRow, newCol, this.portName.advance());
  }
}
