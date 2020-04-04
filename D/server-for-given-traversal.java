package SDWFerd.C.Task2;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * This represents a Labyrinth or undirected graph of nodes connected (or not connected) by edges.
 */
public class Labyrinth implements ILabyrinth {
    private ArrayList<INode> nodes = new ArrayList<>();

    /**
     * Constructor for Labyrinth.
     * @param names List of nodes in Labyrinth. Duplicate names will be disregarded.
     */
    Labyrinth(ArrayList<String> names) {
        ArrayList<String> namesNoDuplicates = new ArrayList<>(new LinkedHashSet<>(names));
        for (String name : namesNoDuplicates) {
            nodes.add(new Node(name, new ArrayList<>()));
        }
    }

    @Override
    public boolean getColoredPath(String token, String name) {
        INode nodeToFind = null;
        ArrayList<INode> nodesWithToken = new ArrayList<>();
        Token tokenStart = new Token(resolveColor(token));
        for (INode node : nodes) {
            if (node.getName() != null && node.getName().equals(name)) {
                nodeToFind = node;
            }
            if (node.getToken() != null && node.getToken().equals(tokenStart)) {
                nodesWithToken.add(node);
            }
        }
        if (nodeToFind == null || nodesWithToken.isEmpty()) {
            return false;
        }
        if (nodeToFind.getToken() != null && nodeToFind.getToken().equals(tokenStart)) {
            return true;
        }

        //Search nodes connected to the node to find
        ArrayList<INode> queue = new ArrayList<>();
        ArrayList<INode> searched = new ArrayList<>();
        queue.add(nodeToFind);
        while (!queue.isEmpty()) {
            INode atNode = queue.get(0);
            queue.remove(0);
            searched.add(atNode);
            for (INode connectedNode : atNode.getNeighbors()) {
                if (nodesWithToken.contains(connectedNode)) {
                    return true;
                }
                if (!searched.contains(connectedNode)) {
                    queue.add(connectedNode);
                }
            }
        }

        return false;
    }

    @Override
    public void addToken(String token, String name) {
        for (INode node : nodes) {
            if (node.getName().equals(name)) {
                node.setToken(new Token(resolveColor(token)));
                break;
            }
        }
    }

    /**
     * Function to return a TokenColor based on an inputted color string.
     * @param color Color of the token to be returned.
     * @return Returns a TokenColor based on the inputted color.
     */
    private TokenColor resolveColor(String color) {
        switch (color) {
            case "black":
                return TokenColor.black;
            case "white":
                return TokenColor.white;
            case "red":
                return TokenColor.red;
            case "blue":
                return TokenColor.blue;
            default:
                return TokenColor.green;
        }
    }

    @Override
    public void addEdge(String node1, String node2) {
        if (node1.equals(node2)) {
            return;
        }
        INode nodeA = null;
        INode nodeB = null;
        for (INode node : nodes) {
            if (node.getName().equals(node1)) {
                nodeA = node;
            } else if (node.getName().equals(node2)) {
                nodeB = node;
            }
        }
        if (nodeA != null && nodeB != null) {
            nodeA.addNeighbor(nodeB);
            nodeB.addNeighbor(nodeA);
        }
    }
}

/**
 * Interface for a Token. Represents a Token that can be added to a Node and return its color.
 */
interface IToken {
    /**
     * Gets the color of this token.
     * @return Color of this token.
     */
    TokenColor getColor();
}

/**
 * Interface for a Node. Represents a Node in the labyrinth.
 */
interface INode {
    /**
     * Gets the name of this Node.
     * @return Name of this node.
     */
    String getName();

    /**
     * Gets the nodes connected to this Node by an edge.
     * @return List of Nodes connected to this node.
     */
    ArrayList<INode> getNeighbors();

    /**
     * Connects this node to another node by an edge.
     * @param node Node to connect this one to.
     */
    void addNeighbor(INode node);

    /**
     * Adds a token to this node. If a token already exists, overwrite it.
     * @param token Token to be added.
     */
    void setToken(IToken token);

    /**
     * Gets the token on this Node.
     * @return Token on this node.
     */
    IToken getToken();
}

interface ILabyrinth {
    /**
     * Checks whether there is a path from a node with the inputted token to a node named name.
     * @param token Color of token that will be started at in the search.
     * @param name Name of node to be found by search.
     * @return true if there is a path. false if there is no path, no node with given token, or no node with given name.
     */
    boolean getColoredPath(String token, String name);

    /**
     * Adds a token to the given node. If a token already exists on the node, overwrite it.
     * @param token Token (as String) to be added to the node.
     * @param name Name of node that the token will be added to.
     */
    void addToken(String token, String name);

    /**
     * Adds an edge to the Labyrinth between node1 and node2. If the edge already exists, disregard it. If
     * an invalid node is given, disregard it.
     * @param node1 First node to be given.
     * @param node2 Node to connect to node1.
     */
    void addEdge(String node1, String node2);
}

/**
 * Implementation of Token. Represents a token that can be added to a Node.
 */
class Token implements IToken {
    private TokenColor color;

    /**
     * Constructor for Token.
     * @param color The Color of the Token being created.
     */
    Token(TokenColor color) {
        this.color = color;
    }

    @Override
    public TokenColor getColor() {
        return color;
    }

    /**
     * Token equivalence checker.
     * @param o Object to be checked if equivalent to this.
     * @return True if the color of the Token inputted is the same as the color of this Token. Otherwise return false.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Token) {
            return ((Token) o).color == this.color;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}

/**
 * Represents the possible colors for a Token.
 */
enum TokenColor {
    black, white, red, blue, green;
}

/**
 * Implementation for Node. Represents a node in the Labyrinth.
 */
class Node implements INode {
    private String name;
    private ArrayList<INode> neighbors;
    private IToken token;

    /**
     * Constructor for Node, without a token.
     * @param name Name of this Node.
     * @param neighbors The neighbors of this Node connected by an edge.
     */
    Node(String name, @NotNull ArrayList<INode> neighbors) {
        this.name = name;
        this.neighbors = neighbors;
    }

    /**
     * Constructor for Node, with a token.
     * @param name Name of this Node.
     * @param token Token on this Node.
     * @param neighbors The neighbors of this Node connected by an edge.
     */
    Node(String name, Token token, @NotNull ArrayList<INode> neighbors) {
        this.name = name;
        this.neighbors = neighbors;
        this.token = token;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<INode> getNeighbors() {
        return new ArrayList<>(neighbors);
    }

    @Override
    public void addNeighbor(INode node) {
        if (!neighbors.contains(node)) {
            neighbors.add(node);
        }
    }

    @Override
    public void setToken(IToken token) {
        this.token = token;
    }

    @Override
    public IToken getToken() {
        return token;
    }
}