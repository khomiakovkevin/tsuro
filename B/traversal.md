# Specification for Labyrinth
Language: Java 8

You will be tasked with creating the following interfaces: Labyrinth, Node, and Token, with an Enumeration for TokenColor.

### Labyrinth

The labyrinth class will be the overall labyrinth containing all the nodes.

Constructor:

-- Labyrinth(ArrayList names), which contains a list of names for each node in the graph.

Fields: 

-- nodeList (an ArrayList) that contains the list of nodes in the Labyrinth.

Methods: 

-- getColoredPath(token, name): returns a boolean, can any node with this token reach the given node?

-- addToken(token, name): add this token to the node with the given name

---

### Node

Constructors (2):
 
-- Node(String name, ArrayList neighbors)
 
-- Node(String name, Token token, ArrayList neighbors)

---

### Token

Constructor:

-- Token(TokenColor color)

Fields:

-- TokenColor(color)

---

### TokenColor

A TokenColor can be one of: "white", "black", "red", "green", or "blue"