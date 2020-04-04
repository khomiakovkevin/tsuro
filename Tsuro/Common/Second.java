import javax.swing.text.Position;
import java.awt.*;
import java.util.ArrayList;

/*
The main purpose of this strategy (and its differentiation between DumbStrategy) is implementing
rotations among the Tiles so that the player can make the decision to choose a Tile or rotation
to avoid reaching the edge of the Board.
 */

public class Second implements Strategy {
    private Board board;

    // Automatically choose the 3rd tile as the initial one with no rotation
    @Override
    public int chooseInitialTile() {
        return 2;
    }


    // Automatically choose the 2nd tile and look at all rotations
    // WHAT TO DO if this second tile doesn't "work"?
    // -- what does this mean? Is it if the all tile rotations lead to a player being out?
    // -- if so, then I should choose the first tile
    @Override
    public int chooseIntermediateTile() {
        int choice = 1;
        ArrayList<Tiles> tiles = new ArrayList<>();
        HandPanel hp = new HandPanel(tiles, choice);

        if(!isAcceptableTile(tiles, choice)){
            choice = 1;
        }

        return choice;
    }
    
    @Override
    public void updateBoard(Board board) {
        this.board = board;
    }
    
    private boolean isAcceptableTile(ArrayList<Tiles> hands, int choice) {
        Tiles possibleTile = hands.get(choice);
        Tiles[][] grid = new Board().getGrid();
        Point initialGridPlacement = chooseInitPosition();
        Point position = new Point();
        // this gets the initial tile's output port that the avatar leaves out of
        Port.PortName initialOutputPort = possibleTile.trace(choosePort(possibleTile, initialGridPlacement),
                false);

        // This is checking if the output port leads to the edge of the board, which means the tile and/or
        // its rotation is not acceptable
        if (possibleStartPorts(position.x, position.y).contains(initialOutputPort)){
            return false;
        }

        return true;

}


    // Look for the first legal spot available in the counter-clockwise direction
    // starting from (0, 0)
    @Override
    public Point chooseInitPosition() {
        Tiles[][] grid = this.board.getGrid();
        for (int ii = 0; ii < grid.length; ii++) {
            if (grid[ii][0] == null) {
                return new Point(ii, 0);
            }
        }

        for (int jj = 1; jj < grid.length - 1; jj++) {
            if (grid[grid.length - 1][jj] == null) {
                return new Point(grid.length - 1, jj);
            }
        }

        for (int ii = grid.length - 1; ii >= 0; ii--) {
            if (grid[ii][grid.length - 1] == null) {
                return new Point(ii, grid.length - 1);
            }
        }

        for (int jj = grid.length - 2; jj > 0; jj--) {
            if (grid[0][jj] == null) {
                return new Point(0, jj);
            }
        }
        throw new IllegalStateException("There are no more empty spots in the board");
    }

    // Look for the first legal port in counter-clockwise fashion that faces an empty square
    @Override
    public Port.PortName choosePort(Tiles tile, Point position) {
        ArrayList<Port.PortName> expectedDestinationPorts = desiredDestination(position.x, position.y);
        ArrayList<Port.PortName> desired = tile.findBeginningPorts(expectedDestinationPorts);
        ArrayList<Port.PortName> possible = possibleStartPorts(position.x, position.y);

        for (Port.PortName p : desired) {
            if (possible.contains(p)) {
                return p;
            }

        }
        throw new IllegalStateException("Something went wrong with initial port decision!");
    }


    private ArrayList<Port.PortName> desiredDestination(int x, int y) {
        ArrayList<Port.PortName> expectedDestinationPorts = new ArrayList<>();
        if (x == 0 || x == 9) {
            if (x == 0) {
                expectedDestinationPorts.add(Port.PortName.N1);
                expectedDestinationPorts.add(Port.PortName.N2);
            } else {
                expectedDestinationPorts.add(Port.PortName.S1);
                expectedDestinationPorts.add(Port.PortName.S2);
            }
            if (y == 0) {
                expectedDestinationPorts.add(Port.PortName.E1);
                expectedDestinationPorts.add(Port.PortName.E2);
                return expectedDestinationPorts;
            } else if (y == 9) {
                expectedDestinationPorts.add(Port.PortName.W1);
                expectedDestinationPorts.add(Port.PortName.W2);
                return expectedDestinationPorts;
            } else {
                expectedDestinationPorts.add(Port.PortName.E1);
                expectedDestinationPorts.add(Port.PortName.E2);
                expectedDestinationPorts.add(Port.PortName.W1);
                expectedDestinationPorts.add(Port.PortName.W2);
                return expectedDestinationPorts;
            }
        } else {
            expectedDestinationPorts.add(Port.PortName.N1);
            expectedDestinationPorts.add(Port.PortName.N2);
            expectedDestinationPorts.add(Port.PortName.S1);
            expectedDestinationPorts.add(Port.PortName.S2);
            if (y == 0) {
                expectedDestinationPorts.add(Port.PortName.E1);
                expectedDestinationPorts.add(Port.PortName.E2);
                return expectedDestinationPorts;
            } else {
                expectedDestinationPorts.add(Port.PortName.W1);
                expectedDestinationPorts.add(Port.PortName.W2);
                return expectedDestinationPorts;
            }
        }
    }

    // Find the possible starting points of the ports based on where the Tile is located
    private ArrayList<Port.PortName> possibleStartPorts(int x, int y) {
        ArrayList<Port.PortName> possibleStartPorts = new ArrayList<>();
        if (x == 0 || x == 9) {
            if (x == 0) {
                possibleStartPorts.add(Port.PortName.S1);
                possibleStartPorts.add(Port.PortName.S2);
            } else {
                possibleStartPorts.add(Port.PortName.N1);
                possibleStartPorts.add(Port.PortName.N2);
            }
            if (y == 0) {
                possibleStartPorts.add(Port.PortName.W1);
                possibleStartPorts.add(Port.PortName.W2);
                return possibleStartPorts;
            } else if (y == 9) {
                possibleStartPorts.add(Port.PortName.E1);
                possibleStartPorts.add(Port.PortName.E2);
                return possibleStartPorts;
            } else {
                return possibleStartPorts;
            }
        } else {
            if (y == 0) {
                possibleStartPorts.add(Port.PortName.W1);
                possibleStartPorts.add(Port.PortName.W2);
                return possibleStartPorts;
            } else {
                possibleStartPorts.add(Port.PortName.E1);
                possibleStartPorts.add(Port.PortName.E2);
                return possibleStartPorts;
            }
        }
    }

    // Need to choose a rotation that doesn't end up with an avatar going out of the board
    @Override
    public int chooseRotation() {
        return 0;
    }
}
