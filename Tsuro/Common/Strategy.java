import java.awt.*;

public interface Strategy {

    /**
     * @return index of the initial tile the player should choose
     */
    public int chooseInitialTile();

    /**
     * @return index of the intermediate tile the player should choose
     */
    public int chooseIntermediateTile();

    void  updateBoard (Board board);


    /**
     * @return the position to be chosen
     */
    public Point chooseInitPosition();

    /**
     * @param tile tile to choose the port from
     * @param position position of the tile on the board
     * @return name of the port to be chosen
     */
    public Port.PortName choosePort(Tiles tile, Point position);

    /**
     * @return the rotation to place the tile with
     */
    public int chooseRotation();


}
