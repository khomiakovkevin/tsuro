import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an Avatar in the game of Tsuro.
 */
public class Avatar implements Serializable {
  private Point pos;
  private Port.PortName currentPort;
  private AvatarColor color;
  private boolean exited;
  
  /**
   * Standard constructor for Avatar.
   *
   * @param color The color of this Avatar.
   */
  public Avatar(String color) {
    this.pos = new Point();
    this.color = AvatarColor.mapColor(color);
    this.exited = false;
  }
  
  /**
   * Gets the current position of this Avatar on the board.
   *
   * @return The positional coordinates of this Avatar.
   */
  public Point getPos() {
    return this.pos;
  }

  /**
   * Gives the current port this Avatar is at.
   *
   * @return The current name of the port this Avatar is at on the Tiles it
   *         occupies.
   */
  public Port.PortName getCurrentPort() {
    return this.currentPort;
  }
  
  /**
   * Gets the color of this Avatar.
   *
   * @return The color of this Avatar.
   */
  public AvatarColor getColor() {
    return this.color;
  }
  
  /**
   * Tells whether this Avatar has exited from the board.
   * @return True if this Avatar has exited from the board.
   */
  public boolean hasExited() {
    return this.exited;
  }
    
  /**
   * Updates the current position of this Avatar.
   * @param xPos The new x coordinate.
   * @param yPos The new y coordinate.
   */
  public void updatePosition(int xPos, int yPos) {
    this.pos.x = xPos;
    this.pos.y = yPos;
  }
    
  /**
   * Updates the current position of this Avatar.
   * @param xPos The new x coordinate.
   * @param yPos The new y coordinate.
   * @param newPort The new Port.Portname.
   */
  public void updatePosition(int xPos, int yPos, Port.PortName newPort) {
    this.pos.x = xPos;
    this.pos.y = yPos;
    this.currentPort = newPort;
  }
  
  /**
   * Set the current port to the given value.
   *
   * @param newPort The new port to set this Avatar's location to.
   */
  public void setCurrentPort(Port.PortName newPort) {
    this.currentPort = newPort;
  }
  
  /**
   * Set the exited flag for this avatar.
   */
  public void setExited() {
    this.exited = true;
  }
  
  /**
   * Represents a valid color of an Avatar.
   */
  public enum AvatarColor {
    WHITE, BLACK, RED, GREEN, BLUE;
    
    /**
     * Decodes a String reference into a valid AvatarColor.
     *
     * @param color The color to be decoded.
     * @return The decoded instance of AvatarColor that corresponds with the
     * given String.
     */
    public static AvatarColor mapColor(String color) {
      switch (color) {
        case "white":
          return WHITE;
        case "black":
          return BLACK;
        case "red":
          return RED;
        case "green":
          return GREEN;
        case "blue":
          return BLUE;
        default:
          throw new IllegalArgumentException("Color is not supported: " + color);
      }
    }
    
    public static ArrayList<String> getAllColors() {
      ArrayList<String> allColors = new ArrayList<>();
      allColors.add("white");
      allColors.add("black");
      allColors.add("red");
      allColors.add("green");
      allColors.add("blue");
      return allColors;
    }
  
    /**
     * Gets a corresponding Color for whichever type of AvatarColor this is.
     *
     * @return The corresponding Color for this AvatarColor.
     */
    public Color getColorObject() {
      switch (this) {
        case WHITE:
          return new Color(255,255,255);
        case BLACK:
          return new Color(0,0,0);
        case RED:
          return new Color(255,0,0);
        case GREEN:
          return new Color(0,255,0);
        case BLUE:
          return new Color(0,0,255);
        default:
          return null;
      }

    }
  }
}
