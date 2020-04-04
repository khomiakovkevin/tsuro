import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import javafx.scene.shape.Ellipse;


/**
 * Represents a Tsuro tile with 8 ports and 4 pairwise distinct connections
 * between each one and its pair.
 */
public class Tiles implements Serializable {
    public ArrayList<Pair<Port, Port>> paths;
    private int rotation;
    private int idxInAll35;

    /**
     * Standard constructor, taking String names of all ports in order.
     *
     * @param path1StartName The first end of Path 1.
     * @param path1EndName   The second end of Path 1.
     * @param path2StartName The first end of Path 2.
     * @param path2EndName   The second end of Path 2.
     * @param path3StartName The first end of Path 3.
     * @param path3EndName   The second end of Path 3.
     * @param path4StartName The first end of Path 4.
     * @param path4EndName   The second end of Path 4.
     * @param idxInAll35     The index of this tile in the list of distinct tiles
     *                       given in all35Tiles().
     */
    public Tiles(String path1StartName, String path1EndName,
                 String path2StartName, String path2EndName,
                 String path3StartName, String path3EndName,
                 String path4StartName, String path4EndName,
                 int idxInAll35) {
        this.rotation = 0;
        this.idxInAll35 = idxInAll35;
        try {
            // Convert all strings to enum value, or throw exception
            ArrayList<Port.PortName> portPaths = new ArrayList<>();
            portPaths.add(Port.PortName.valueOf(path1StartName));
            portPaths.add(Port.PortName.valueOf(path1EndName));
            portPaths.add(Port.PortName.valueOf(path2StartName));
            portPaths.add(Port.PortName.valueOf(path2EndName));
            portPaths.add(Port.PortName.valueOf(path3StartName));
            portPaths.add(Port.PortName.valueOf(path3EndName));
            portPaths.add(Port.PortName.valueOf(path4StartName));
            portPaths.add(Port.PortName.valueOf(path4EndName));

            // If we reach here, portPaths must only contain valid Port values
            ArrayList<Port.PortName> distinctPorts = portPaths.stream()
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));
            if (distinctPorts.size() != 8) {
                // We have duplicates in the inputs, meaning ports are missing from assignment
                throw new IllegalArgumentException("Must construct Tiles with all 8 ports only once each");
            }

            // Set paths
            this.paths = new ArrayList<>();
            paths.add(new Pair<>(new Port(portPaths.get(0)), new Port(portPaths.get(1))));
            paths.add(new Pair<>(new Port(portPaths.get(2)), new Port(portPaths.get(3))));
            paths.add(new Pair<>(new Port(portPaths.get(4)), new Port(portPaths.get(5))));
            paths.add(new Pair<>(new Port(portPaths.get(6)), new Port(portPaths.get(7))));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }





    public Tiles clone () {
        Tiles clone = new Tiles(this.paths.get(0).fst.getName().toString(),
                this.paths.get(0).snd.getName().toString(),
                this.paths.get(1).fst.getName().toString(),
                this.paths.get(1).snd.getName().toString(),
                this.paths.get(2).fst.getName().toString(),
                this.paths.get(2).snd.getName().toString(),
                this.paths.get(3).fst.getName().toString(),
                this.paths.get(3).snd.getName().toString(),
                this.idxInAll35);
        clone.rotate(this.getRotation());
        return clone;
    }

    public boolean hasAvatar() {
        for (Pair<Port, Port> path : paths) {
            if (path.fst.getHasAvatar() || path.snd.getHasAvatar()) {
                return true;
            }
        }
        return false;
    }

    public Port getPort(Port.PortName name){
        for (Pair<Port, Port> path: this.paths){
            if (path.fst.getName().toString().equals(name.toString())){
                return path.fst;
            } else if (path.snd.getName().toString().equals(name.toString())){
                return path.snd;

            }
        }
        return null;
    }

    /**
     * Nonstandard constructor for use in Tiles rotation.
     *
     * @param paths The paths of the new Tiles.
     */
    private Tiles(ArrayList<Pair<Port, Port>> paths) {
        this.paths = paths;
    }

    /**
    * @return An integer representation of the rotation of a Tile
    */
    public int getRotation() {
        return this.rotation;
    }

    /**
    * @return An integer representation of the position of the Tile in all 35 Tiles
    */
    public int getIdxInAll35() {
        return this.idxInAll35;
    }

    /**
    * The drawing of a Tile
    * @param g The graphics component
    * @param d The dimension component
    */
    public void drawTile(Graphics g, Dimension d) {
        HashMap<Port.PortName, Pair<Integer, Integer>> cords = createCords(d);
        Graphics2D g2 = (Graphics2D) g;
        float width = 3;
        g2.setStroke(new BasicStroke(width));

        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(66, 17, 18));
        colors.add(new Color(202, 13, 81));
        colors.add(new Color(213, 180, 39));
        colors.add(new Color(72, 174, 29));

        for (int ii = 0; ii < 4; ++ii) {
            Pair<Port, Port> thisPath = this.paths.get(ii);

            Color thisColor = new Color(255, 255, 255);
            Path2D path = new Path2D.Double();
            path.moveTo(cords.get(thisPath.fst.getName()).fst,
                    cords.get(thisPath.fst.getName()).snd);
            g.setColor(thisColor);
            path.curveTo(cords.get(thisPath.fst.getName()).fst,
                    cords.get(thisPath.fst.getName()).snd,
                    d.width / 2, d.height / 2, cords.get(thisPath.snd.getName()).fst,
                    cords.get(thisPath.snd.getName()).snd);
            g2.draw(path);

            if (thisPath.fst.getHasAvatar()) {
                Pair<Integer, Integer> cordsPathFirst = getAvatarCords(cords.get(thisPath.fst.getName()), d);
                Color col = thisPath.fst.getAvatar().getColor().getColorObject();
                g.setColor(col);
                g.fillOval(cordsPathFirst.fst, cordsPathFirst.snd, 15, 15);

            } else if (thisPath.snd.getHasAvatar()) {
                Pair<Integer, Integer> cordsPathSecond = getAvatarCords(cords.get(thisPath.snd.getName()), d);
                Color col = thisPath.snd.getAvatar().getColor().getColorObject();
                g.setColor(col);
                g.fillOval(cordsPathSecond.fst,
                        cordsPathSecond.snd, 15, 15);
            }
        }
    }

    private Pair<Integer, Integer> getAvatarCords(Pair<Integer, Integer> portCords, Dimension d) {
        if (portCords.fst == d.height) {
            return new Pair<Integer, Integer>(portCords.fst - 15, portCords.snd - 8);
        } else if (portCords.snd == d.width) {
            return new Pair<Integer, Integer>(portCords.fst - 8, portCords.snd - 15);
        } else if (portCords.fst == 0) {
           return new Pair<Integer, Integer>(portCords.fst, portCords.snd - 4);
        } else {
            return new Pair<Integer, Integer>(portCords.fst - 6, portCords.snd);
        }
    }

    public Port.PortName getAvatarPort(Avatar avatar) {
        for (Pair<Port, Port> path : paths) {
            if (path.fst.getHasAvatar() && path.fst.getAvatar().getColor().toString().equals(avatar.getColor().toString())) {
                return path.fst.getName();
            } else if (path.snd.getHasAvatar() && path.snd.getAvatar().getColor().toString().equals(avatar.getColor().toString())) {
                return path.snd.getName();
            }
        }
        throw new IllegalArgumentException("Given avatar was not found on any of the tile's ports");
    }

    public void moveAvatarInTile(Avatar avatar) {
        Port.PortName p = getAvatarPort(avatar);
        for (Pair<Port, Port> path : paths) {
            if (p.toString().equals(path.fst.getName().toString())) {
                path.fst.removeAvatar();
                path.snd.addAvatar(avatar);
            }
            if (p.toString().equals(path.snd.getName().toString())) {
                path.snd.removeAvatar();
                path.fst.addAvatar(avatar);
            }
        }
    }

    /**
     * Places an Avatar onto a given location on this Tiles.
     *
     * @param name   The name of the port onto which the Avatar should be placed.
     * @param avatar The Avatar to be placed.
     */
    public void placeAvatar(Port.PortName name, Avatar avatar) {
        // Check each path for the given name.
        for (Pair<Port, Port> path : paths) {
            Port end1 = path.fst;
            Port end2 = path.snd;

            // If one of these ports has the given name, then add the Avatar.
            if (end1.getName().equals(name)) {
                end1.addAvatar(avatar);
            } else if (end2.getName().equals(name)) {
                end2.addAvatar(avatar);
            }
        }
    }

    public void removeAvatar(Avatar avatar) {
        for (Pair<Port, Port> path : this.paths) {
            if (path.fst.getHasAvatar() &&
                    path.fst.getAvatar().getColor().toString().equals(avatar.getColor().toString())) {
                path.fst.removeAvatar();
            }
            if (path.snd.getHasAvatar() &&
                    path.snd.getAvatar().getColor().toString().equals(avatar.getColor().toString())) {
                path.snd.removeAvatar();
            }
        }
    }

    /**
     * Gives a rotated version of this Tiles.
     *
     * @param degrees The number of degrees to be rotated by.
     * @return A version of this Tiles, rotated by the given angle.
     * @throws IllegalArgumentException If rotation angle is not one of: 0, 90,
     *                                  180, or 270.
     */
    public Tiles rotate(int degrees) throws IllegalArgumentException {
        /*
          Only process rotation requests with angles of 90, 180, or 270 degrees.
         */
        if (degrees != 0 && degrees != 90 && degrees != 180 && degrees != 270) {
            throw new IllegalArgumentException("Rotation angle must be " +
                    "one of: 90, 180, 270.");
        }

        // Process rotation:
        int numOf90s = degrees / 90;
        Tiles postRotation = this;
        for (int ii = 0; ii < numOf90s; ++ii) {
            postRotation = postRotation.rotateClockwise();
        }

        return postRotation;
    }

    /**
     * Follows the path on this tile with the given entry point and gives the
     * Port.PortName of that path's other end.
     *
     * @param entryPoint The entry Port.PortName of the path to be traced.
     * @return The other end of the traced path.
     */
    public Port.PortName trace(Port.PortName entryPoint, boolean physicalMove) {
        // Check each path on this Tiles:
        for (Pair<Port, Port> path : this.paths) {
            // Gets other end of the requested path.
            if (path.fst.getName() == entryPoint) {
                if (physicalMove) {
                    Avatar avatar = path.fst.getAvatar();
                    path.fst.removeAvatar();
                    path.snd.addAvatar(avatar);
                    avatar.setCurrentPort(path.snd.getName());
                }
                return path.snd.getName();
            }
            if (path.snd.getName() == entryPoint) {
                if (physicalMove) {
                    Avatar avatar = path.snd.getAvatar();
                    path.snd.removeAvatar();
                    path.fst.addAvatar(avatar);
                    avatar.setCurrentPort(path.fst.getName());
                }
                return path.fst.getName();
            }
        }
        return null;
    }

    /**
     * Finds all ports that lead to the destination ports for the initial
     * [0,0] tile placement scenario.
     *
     * @return The other end of the traced path.
     */
    public ArrayList<Port.PortName> findBeginningPorts(List<Port.PortName> desired) {

        // container for all acceptable ports
        ArrayList validPorts = new ArrayList<Port.PortName>();

        // list of ports that we want to have the avatar end up on
        List<Port.PortName> expectedDestinationPorts = Arrays.asList(Port.PortName.N1,
                Port.PortName.N2,
                Port.PortName.E1,
                Port.PortName.E2);

        // checks the origin ports of each of the expectedDestinationPorts and
        // adds them to the container list
        for (Port.PortName destinationPort : expectedDestinationPorts) {
            Port.PortName beginningPort = trace(destinationPort, false);
            validPorts.add(beginningPort);
        }

        return validPorts;
    }
    
    public void trimOuterAvatars(ArrayList<Port.PortName> outerPorts) {
        for (Port.PortName portName : outerPorts) {
            Port port = this.getPort(portName);
            if (port.getHasAvatar()) {
                port.removeAvatar();
            }
        }
    }

    /**
    * Create cords for paths
    * @param d The dimension component
    */
    private HashMap<Port.PortName, Pair<Integer, Integer>> createCords(Dimension d) {
        int width = d.width;
        int height = d.height;
        HashMap<Port.PortName, Pair<Integer, Integer>> cords = new HashMap<>();

        Pair<Integer, Integer> w1 = new Pair<>(0, (int) (height * (2. / 3)));
        cords.put(Port.PortName.W1, w1);
        Pair<Integer, Integer> w2 = new Pair<>(0, (int) (height * (1. / 3)));
        cords.put(Port.PortName.W2, w2);

        Pair<Integer, Integer> e1 = new Pair<>(width, (int) (height * (1. / 3)));
        cords.put(Port.PortName.E1, e1);
        Pair<Integer, Integer> e2 = new Pair<>(width, (int) (height * (2. / 3)));
        cords.put(Port.PortName.E2, e2);

        Pair<Integer, Integer> n1 = new Pair<>((int) (width * (1. / 3)), 0);
        cords.put(Port.PortName.N1, n1);
        Pair<Integer, Integer> n2 = new Pair<>((int) (width * (2. / 3)), 0);
        cords.put(Port.PortName.N2, n2);

        Pair<Integer, Integer> s1 = new Pair<>((int) (width * (2. / 3)), height);
        cords.put(Port.PortName.S1, s1);
        Pair<Integer, Integer> s2 = new Pair<>((int) (width * (1. / 3)), height);
        cords.put(Port.PortName.S2, s2);

        return cords;
    }

    /**
     * Rotates this Tiles 90 degrees clockwise.
     *
     * @return Returns a rotated version of this Tiles.
     */
    private Tiles rotateClockwise() {
        // Initialize rotated paths ArrayList:
        ArrayList<Pair<Port, Port>> newPaths = new ArrayList<>();
        
        /*
          For each path in this Tiles, rotate each one and store it in the
          temporary ArrayList.
         */
        for (Pair<Port, Port> path : this.paths) {
            Pair<Port, Port> newPath = new Pair<>(path.fst.rotate(),
                    path.snd.rotate());
            newPaths.add(newPath);
        }

        // Return new Tiles with the rotated Paths.
        return new Tiles(newPaths);
    }
    
    
    @Override
    public String toString() {
        Pair<Port, Port> path0 = this.paths.get(0);
        String path0fst = path0.fst.getName().toString();
        String path0snd = path0.snd.getName().toString();
        String path0String = path0fst + "-" + path0snd;
        
        Pair<Port, Port> path1 = this.paths.get(1);
        String path1fst = path1.fst.getName().toString();
        String path1snd = path1.snd.getName().toString();
        String path1String = path1fst + "-" + path1snd;
    
        Pair<Port, Port> path2 = this.paths.get(2);
        String path2fst = path2.fst.getName().toString();
        String path2snd = path2.snd.getName().toString();
        String path2String = path2fst + "-" + path2snd;
    
        Pair<Port, Port> path3 = this.paths.get(3);
        String path3fst = path3.fst.getName().toString();
        String path3snd = path3.snd.getName().toString();
        String path3String = path3fst + "-" + path3snd;
        
        return path0String + "; " + path1String + "; " + path2String + "; " + path3String;
    }
    
    /**
     * Create ArrayList of all 35 distinct Tsuro Tiles.
     *
     * @return List of all 35 distinct Tsuro Tiles.
     */
    public static ArrayList<Tiles> all35Tiles() {
        ArrayList<Tiles> distinctTiles = new ArrayList<>();
        // Tile 0
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "E1",
                "N2", "N1",
                "W2", "W1", 0));
        // Tile 1
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "E1",
                "N2", "W2",
                "N1", "W1", 1));
        // Tile 2
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "E1",
                "N2", "W1",
                "N1", "W2", 2));
        // Tile 3
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "N2",
                "E1", "W2",
                "N1", "W1", 3));
        // Tile 4
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "N2",
                "E1", "W1",
                "N1", "W2", 4));
        // Tile 5
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "N1",
                "E1", "W2",
                "N2", "W1", 5));
        // Tile 6
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "N1",
                "E1", "W1",
                "N2", "W2", 6));
        // Tile 7
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "W2",
                "E1", "N2",
                "N1", "W1", 7));
        // Tile 8
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "W2",
                "E1", "N1",
                "N2", "W1", 8));
        // Tile 9
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "W2",
                "E1", "W1",
                "N2", "N1", 9));
        // Tile 10
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "W1",
                "E1", "N2",
                "N1", "W2", 10));
        // Tile 11
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "W1",
                "E1", "N1",
                "N2", "W2", 11));
        // Tile 12
        distinctTiles.add(new Tiles("S2", "S1",
                "E2", "W1",
                "E1", "W2",
                "N2", "N1", 12));
        // Tile 13
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "E1",
                "N2", "W2",
                "N1", "W1", 13));
        // Tile 14
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "E1",
                "N2", "W1",
                "N1", "W2", 14));
        // Tile 15
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "N2",
                "E1", "W2",
                "N1", "W1", 15));
        // Tile 16
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "N2",
                "E1", "W1",
                "N1", "W2", 16));
        // Tile 17
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "N1",
                "E1", "W2",
                "N2", "W1", 17));
        // Tile 18
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "N1",
                "E1", "W1",
                "N2", "W2", 18));
        // Tile 19
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "W2",
                "E1", "N2",
                "N1", "W1", 19));
        // Tile 20
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "W2",
                "E1", "N1",
                "N2", "W1", 20));
        // Tile 21
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "W1",
                "E1", "N2",
                "N1", "W2", 21));
        // Tile 22
        distinctTiles.add(new Tiles("S2", "E2",
                "S1", "W1",
                "E1", "N1",
                "N2", "W2", 22));
        // Tile 23
        distinctTiles.add(new Tiles("S2", "E1",
                "S1", "E2",
                "N2", "W1",
                "N1", "W2", 23));
        // Tile 24
        distinctTiles.add(new Tiles("S2", "E1",
                "S1", "N2",
                "E2", "W2",
                "N1", "W1", 24));
        // Tile 25
        distinctTiles.add(new Tiles("S2", "E1",
                "S1", "N2",
                "E2", "W1",
                "N1", "W2", 25));
        // Tile 26
        distinctTiles.add(new Tiles("S2", "E1",
                "S1", "N1",
                "E2", "W2",
                "N2", "W1", 26));
        // Tile 27
        distinctTiles.add(new Tiles("S2", "E1",
                "S1", "W2",
                "E2", "N1",
                "N2", "W1", 27));
        // Tile 28
        distinctTiles.add(new Tiles("S2", "N2",
                "S1", "E2",
                "E1", "W2",
                "N1", "W1", 28));
        // Tile 29
        distinctTiles.add(new Tiles("S2", "N2",
                "S1", "E2",
                "E1", "W1",
                "N1", "W2", 29));
        // Tile 30
        distinctTiles.add(new Tiles("S2", "N2",
                "S1", "E1",
                "E2", "W2",
                "N1", "W1", 30));
        // Tile 31
        distinctTiles.add(new Tiles("S2", "N2",
                "S1", "N1",
                "E2", "W2",
                "E1", "W1", 31));
        // Tile 32
        distinctTiles.add(new Tiles("S2", "N2",
                "S1", "N1",
                "E2", "W1",
                "E1", "W2", 32));
        // Tile 33
        distinctTiles.add(new Tiles("S2", "N1",
                "S1", "N2",
                "E2", "W1",
                "E1", "W2", 33));
        // Tile 34
        distinctTiles.add(new Tiles("S2", "W1",
                "S1", "E2",
                "E1", "N2",
                "N1", "W2", 34));
        return distinctTiles;
    }
}
