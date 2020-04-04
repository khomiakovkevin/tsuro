// TODO: Inside

import java.net.InetAddress;

/**
 * A tester for the Tsuro client.
 */
public class ClientTest {
  // TODO: Inside
  /**
   * Main method for the testing of the client.
   *
   * @param args The command line arguments for this program.
   */
  public static void main(String[] args) {
    try {
      validateArgs(args);
      String ip = args[0];
      int port = Integer.parseInt(args[1]);
      String name = args[2];
      String strategy = args[3];
      
      Client.run(InetAddress.getByName(ip), port, name, strategy);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
  
  /**
   * Validates the command line arguments to this program as an ip address, a
   * port number, a desired player name, and a valid strategy type.
   *
   * @param args The command line arguments to this program.
   * @throws IllegalArgumentException if it is not passed exactly 4 command line
   * arguments, or if the ip, port, or strategy are invalid.
   */
  private static void validateArgs(String[] args)
          throws IllegalArgumentException {
    if (args.length != 4) {
      throw new IllegalArgumentException("Error: this client requires 4 " +
              "command line arguments: an IP address of the server to connect" +
              " to,\na port of said server, a name for the desired player, " +
              "and a strategy to be used by that player.");
    }
    validateIP(args[0]);
    validatePort(args[1]);
    validateStrategy(args[3]);
  }
  
  /**
   * Parses the given String for an integer, and then checks if it lies in the
   * given closed interval.
   *
   * @param integer The String to be parsed and validated.
   * @param low The lower bound of the closed interval.
   * @param high The upper bound of the closed interval.
   * @return True if the String represents an integer inside the given closed
   *         interval.
   * @throws NumberFormatException if integer does not contain a parsable
   *                               integer.
   */
  private static boolean validateClosedIntervalString(String integer,
                                                      int low, int high)
          throws NumberFormatException {
    int portNumber = Integer.parseInt(integer);
    return low <= portNumber && portNumber <= high;
  }
  
  /**
   * Checks if the given String represents a valid IP address.
   *
   * @param ip The String to be verified as an IP address.
   * @throws IllegalArgumentException if ip does not contain four strings
   * adjoined by periods, or if any of those four strings cannot be directly
   * parsed into an integer lying in the range [0, 255].
   */
  private static void validateIP(String ip)
          throws IllegalArgumentException {
    String[] splitIP = ip.split("\\.");
    if (splitIP.length != 4) {
      throw new IllegalArgumentException("IP address (" + ip + ") is " +
              "malformed, it should contain four integers in the range " +
              "[0, 255] separated by periods.");
    }
    for (String ipSegment : splitIP) {
      if (!validateClosedIntervalString(ipSegment, 0, 255)) {
        throw new IllegalArgumentException("One of the entries of the IP " +
                "address (" + ipSegment + ") does not contain a valid " +
                "numerical entry in the range [0, 255].");
      }
    }
  }
  
  /**
   * Checks if the given String can be parsed into a valid port number.
   *
   * @param port The potential port number to be validated.
   * @throws IllegalArgumentException if port does not contain a parsable integer,
   * or if that integer lies outside the range of [0, 65535].
   */
  private static void validatePort(String port)
          throws IllegalArgumentException {
    if (!validateClosedIntervalString(port, 0, 65535)) {
      throw new IllegalArgumentException("The requested port number lies " +
              "outside the valid range of [0, 65535]: " + port);
    }
  }
  
  /**
   * Checks if the given strategy name is a valid type.
   *
   * @param strategy The name of a potential strategy to be checked.
   * @throws IllegalArgumentException if strategy does not correspond to a valid
   * strategy name.
   */
  private static void validateStrategy(String strategy)
          throws IllegalArgumentException {
    switch (strategy) {
      case "dumb":
      case "Dumb":
      case "second":
      case "Second":
        return;
      default:
        throw new IllegalArgumentException("Given strategy name (" + strategy +
                ") does not correspond to a valid strategy type.");
    }
  }
}
