import java.util.ArrayList;

/**
 * Represents a Port on a Tsuro Tiles.
 */
public class Port {
  private PortName name;
  private Avatar avatar;
  private boolean hasAvatar;
  
  /**
   * Standard constructor.
   *
   * @param name The name of this Port.
   */
  public Port(PortName name) {
    this.name = name;
    this.hasAvatar = false;
  }
  
  /**
   * Gets the name of this Port.
   *
   * @return The name of this Port.
   */
  public PortName getName() {
    return this.name;
  }
  
  /**
   * Gets the Avatar at this Port, if applicable.
   *
   * @return The Avatar at this Port, or null if none exists.
   */
  public Avatar getAvatar() {
    return this.avatar;
  }
  
  /**
   * Tells whether or not an Avatar is at this Port.
   *
   * @return True if this port has an Avatar.
   */
  public boolean getHasAvatar() {
    return this.hasAvatar;
  }
  
  /**
   * Adds an Avatar to this Port.
   *
   * @param avatar The avatar to be added to this Port.
   */
  public void addAvatar(Avatar avatar) {
    this.avatar = avatar;
    this.hasAvatar = true;
  }
  
  /**
   * Removes the Avatar from this port, if applicable.
   */
  public void removeAvatar() {
    this.hasAvatar = false;
    this.avatar = null;
  }
  
  /**
   * Rotates this port by 90 degrees.
   *
   * @return A rotated version of this Port.
   */
  public Port rotate() {
    return new Port(this.name.rotate());
  }
  
  
  /**
   * Represents the name of a Port.
   */
  public enum PortName {
    N1, N2, E1, E2, S1, S2, W1, W2;
  
    /**
     * Gives a name of what Port on an adjacent Tiles would be connected to by
     * this one.
     *
     * @return The name of which Port on an adjacent Tiles what this Port would
     *         connect to.
     */
    public PortName advance() {
      switch (this) {
        case N1:
          return S2;
        case N2:
          return S1;
        case S1:
          return N2;
        case S2:
          return N1;
        case E1:
          return W2;
        case E2:
          return W1;
        case W1:
          return E2;
        case W2:
          return E1;
        default:
          return null;
      }
    }
  
    public String toString() {
      switch (this) {
        case N1:
          return "N1";
        case N2:
          return "N2";
        case S1:
          return "S1";
        case S2:
          return "S2";
        case E1:
          return "E1";
        case E2:
          return "E2";
        case W1:
          return "W1";
        case W2:
          return "W2";
        default:
          return null;
      }
    }
  
    /**
     * Gives the cardinal direction of this Port.
     *
     * @return The cardinal direction of this Port.
     */
    public String direction() {
      switch (this) {
        case N1:
        case N2:
          return "N";
        case E1:
        case E2:
          return "E";
        case S1:
        case S2:
          return "S";
        case W1:
        case W2:
          return "W";
        default:
          return null;
      }
    }
  
    /**
     * Gives a name of what this Port rotated by 90 degrees would be.
     *
     * @return A name of a Port yielded by rotating this one by 90 degrees.
     */
    public PortName rotate() {
      switch (this) {
        case N1:
          return E1;
        case N2:
          return E2;
        case E1:
          return S1;
        case E2:
          return S2;
        case S1:
          return W1;
        case S2:
          return W2;
        case W1:
          return N1;
        case W2:
          return N2;
        default:
          return null;
      }
    }
    
    public static ArrayList<PortName> allPortNames() {
      ArrayList<PortName> allPortNames = new ArrayList<>();
      allPortNames.add(N1);
      allPortNames.add(N2);
      allPortNames.add(E1);
      allPortNames.add(E2);
      allPortNames.add(S1);
      allPortNames.add(S2);
      allPortNames.add(W1);
      allPortNames.add(W2);
      return allPortNames;
    }
    
  }
}
