## Labyrinth Specification (Python 3.6.8)

### _module_ **labyrinth**


### _class_ labyrinth.**Labyrinth**()

The labyrinth class specifies a collection of Nodes which compose a simple graph.

| Instance Method | Description | Example |
|-----------------|-------------|---------|
| add_node(name: str) -> Node | Adds a node with the given name to the labyrinth. Raises a 'ValueError' if a node with the given name already exists. | `node_A = lab1.add_node('A')`
| get_node_by_name(name: str) -> Optional[Node] | Retrieves the node corresponding to the given name. If no node is found, returns `None`. | `node_A = lab1.get_node_by_name('A')`
| add_token(color: str, node: Node) -> Token | Adds a token with the given color (specified as 6 digit hex code RGB, case insensitive) to the given node. Raises a 'ValueError' if a token with this color exists, or if the given color is invalid. | `token_blue = lab1.add_token('#0000ff', node_A)`
get_token_by_color(color: str) -> Optional[Token] | Retrieves the token with the given color if it exists. If no token is found, returns `None`. Raises a 'ValueError' if the given hex color is invalid. | `token_blue = lab1.get_token_by_color('#0000ff')` 

### _class_ labyrinth.**Node**(labyrinth: Labyrinth, name: str)

Instantiates a node with the given name in the given labyrinth. Throws a 'ValueError' if the node with the given name already exists in the labyrinth.
Nodes should only be instantiated through the `add_node` instance method of `Labyrinth`.

| Instance Method | Description | Example |
|-----------------|-------------|---------|
| add_edge(node: Node) | Adds a bi-directional edge between this node and the given node. Throws a 'ValueError' if such an edge already exists. | `node_A.add_edge(node_B) # there now exists an edge between node_A and node_B`

### _class_ labyrinth.**Token**(node: Node, color: str)

Instantiates a token with the given color located at the given node. Throws a 'ValueError' if the given node is invalid or if the color is an invalid hex color.
Tokens should only be instantiated through the `add_token` instance method of `Labyrinth`.

| Instance Method | Description | Example |
|-----------------|-------------|---------|
| can_reach(node: Node) -> bool | Returns whether or not there exists a path within this labyrinth to reach the given Node from the Node containing this Token. | `can_reach_a = token_blue.can_reach(node_a) # Returns true if there is a path from token_blue's node to node_a` |
