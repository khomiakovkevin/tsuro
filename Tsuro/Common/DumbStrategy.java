
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class DumbStrategy implements Strategy {
    private Board board;
    private int height;
    private int width;
    
    public DumbStrategy() {
    }

    public void setBoard(Board board) {
        this.board = board;
        this.height = board.getHeight();
        this.width = board.getWidth();
    }

    /**
     * @return index of the initial tile the player should choose
     */
    @Override
    public int chooseInitialTile() {
        return 2;
    }

    /**
     * @return index of the intermediate tile the player should choose
     */
    @Override
    public int chooseIntermediateTile() {
        return 0;
    }

    @Override
    public void updateBoard(Board board) {
        this.board = board;
        this.height = board.getHeight();
        this.width = board.getWidth();
    }

    /**
     * @return the position to be chosen
     */
    @Override
    public Point chooseInitPosition() {
        for (int ii = 0; ii < this.height; ii++) {
            if (Rules.validCoordinates(this.board, 0, ii)) {
                return new Point(0, ii);
            }
        }
        
        for (int jj = 1; jj < this.width - 1; jj++) {
            if (Rules.validCoordinates(this.board, jj, this.height - 1)) {
                return new Point(jj, this.height - 1);
            }
        }
        
        for (int ii = this.height - 2; ii >= 0; ii--) {
            if (Rules.validCoordinates(this.board, this.width - 1, ii)) {
                return new Point(this.width - 1, ii);
            }
        }
        
        for (int jj = this.width - 2; jj > 0; jj--) {
            if (Rules.validCoordinates(this.board, jj, 0)) {
                return new Point(jj, 0);
            }
        }
        
        throw new IllegalStateException("There are no more empty spots in the board.");
    }

    /**
     * @param tile tile to choose the port from
     * @param position position of the tile on the board
     * @return name of the port to be chosen
     */
    @Override
    public Port.PortName choosePort(Tiles tile, Point position) {
        ArrayList<Port.PortName> possibleStarts = possibleStartPorts(position);
        ArrayList<Port.PortName> possibleEnds = possibleEndPorts(tile, possibleStarts);
        ArrayList<Boolean> endsValid = endsValid(position, possibleEnds);
        
        for (int ii = 0; ii < endsValid.size(); ++ii) {
            if (endsValid.get(ii)) {
                return possibleStarts.get(ii);
            }
        }
        
        throw new IllegalArgumentException("No valid ports to place at this location.");
    }
    
    /**
    * Produces a list of portnames that the player could choose to start at
    * @param position The place of the Tile on the Board
    */
    private ArrayList<Port.PortName> possibleStartPorts(Point position) {
        int xx = position.x;
        int yy = position.y;
        ArrayList<Port.PortName> possibleStartPorts = new ArrayList<>();
        
        if (xx == 0) {
            if (yy == 0) {
                possibleStartPorts.add(Port.PortName.S1);
                possibleStartPorts.add(Port.PortName.S2);
            }
            possibleStartPorts.add(Port.PortName.W1);
            possibleStartPorts.add(Port.PortName.W2);
        }
        
        if (yy == this.height - 1) {
            possibleStartPorts.add(Port.PortName.N1);
            possibleStartPorts.add(Port.PortName.N2);
        }
        
        if (xx == this.width - 1) {
            possibleStartPorts.add(Port.PortName.E1);
            possibleStartPorts.add(Port.PortName.E2);
        }
        
        if (yy == 0 && xx != 0) {
            possibleStartPorts.add(Port.PortName.S1);
            possibleStartPorts.add(Port.PortName.S2);
        }
        
        return possibleStartPorts;
    }
    
    /**
    * Produces a list of portnames that the player could end at
    * @param tile The Tile chose by the player on the Board
    * @param possibleStarts List of portnames that could be started at
    */
    private ArrayList<Port.PortName> possibleEndPorts(Tiles tile, ArrayList<Port.PortName> possibleStarts) {
        ArrayList<Port.PortName> possibleEnds = new ArrayList<>();
        for (Port.PortName possibleStart : possibleStarts) {
            possibleEnds.add(tile.trace(possibleStart, false));
        }
        return possibleEnds;
    }
    
    /**
    * Produces a list of booleans that produce whether the end is a valid ending (not resulting in death)
    * @param position place of the Tile on the Board
    * @param possibleEnds List of portnames that could be ended at
    */
    private ArrayList<Boolean> endsValid(Point position, ArrayList<Port.PortName> possibleEnds) {
        ArrayList<Boolean> endsValid = new ArrayList<>();
        for (Port.PortName possibleEnd : possibleEnds) {
            boolean facesEdge = Rules.connectsToEdge(this.board, position.x, position.y, possibleEnd);
            if (facesEdge) {
                endsValid.add(false);
            } else {
                Point newPosition = this.board.advancePosition(position, possibleEnd);
                endsValid.add(this.board.getTileAt(newPosition.x, newPosition.y) == null);
            }
        }
        return endsValid;
    }
    
     /**
     * @return the rotation to place the tile with
     */
    @Override
    public int chooseRotation() {
        return 0;
    }
}
