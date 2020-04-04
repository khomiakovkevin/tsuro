# Issues with traversal.md specification

Although we had found several issues with the traversal.md specification, we had tried to implement it to the best of our ability. The code does not run, however, because of the following issues that have prevented us from doing so.

### Infinite class loop
In the specification, it states that a Labyrinth has several methods that take in Node objects, which means we must define the Node class before defining the Labyrinth class. However, the Node class takes in a Labyrinth, which means the Labyrinth class has to be defined before the Node class, but this will give an error because the Node class must be defined before Labyrinth, and Labyrinth must then be defined before Node, which results in an infinite loop of having to create a class before another which can never be solved.
One way to solve this is to remove the Labyrinth input from the Node class, as we are not sure why a Labyrinth should be passed to the Node class. This will allow for a break in the infinite loop and we will be able to successfully implement the two classes.

### Labyrinth has no container to hold Nodes
The Labyrinth class does not take in any kind of container to hold Nodes, nor does it initialize any sort of container. It also does not specify what a SimpleGraph should hold. Because of this, we were forced to create one (not very complex) to get it to make sense for us. However, the code will still nto work because of the infinite class loop.
To avoid confusion, we recommend the team clarify what kind of container to hold the Nodes in. This is very difficult for us to implement without knowing exactly what the team wants because there are many ways to interpret the container.

### The Node/Labyrinth class does not have a container to hold Tokens
In the specification, it says that Tokens should be added to Nodes. However, neither the Nodes class nor the Labyrinth class have any kind of container to hold any sort of Token value.
Again, it is uncertain where we should be able to hold Tokens. There are two main options: create a data structure in the Node class or create a container in the Labyrinth class. It is hard for us to get it 

### add_node(str) does not specify any connection to another Node
The add_node(str) function in the Labyrinth class does not specify if the given Node should be connected to any other Node. In addition, we are not provided what we should add it to (see above). There is no explanation for how we should deal with connections between Nodes.