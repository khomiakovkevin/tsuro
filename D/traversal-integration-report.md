# Question 1: Justification of traversal.md implementation

We received a successful implementation of our traversal.md specification written in Java. All three of the major operations that must be supported (as outlined in Task 2 of the website) are implemented in addition to other features such as containers, as described below. One specific part that the implementation added that we did not think of was the addition of an addEdge() function to add edges between nodes. This is helpful for the features needed in the implementation.

### Creation of a plain labyrinth with named nodes
The creation of a plain labyrinth with named nodes is done very elegantly. Initially, the names of the nodes are passed in via ArrayList, and the implementation takes them in via a Linked Hash Set of names, which is an ordered version of a Hash set that maintains a doubly linked list across all of the nodes. In addition, this allows functionality to get rid of all duplicate nodes. This is very elegant because it does not require any additional function to make sure there are no duplicates (and this function may risk being buggy and producin inaccurate results).

### Addition of a colored token to a node
A token can be added to a node via its constructor. The Token class implements the IToken interface, which contains one method (getColor). In addition, the Token class takes in a TokenColor, which is an enum of colors that we detailed in our specification. The Node class implements the INode interface, which contains two constructors: one that has a token and one that does not. This allows for the ability for a Node to be created without tokens, which is what our specification listed as the requirement (the user passes in an ArrayList of node names and no way for us to pass in tokens). 


### Query whether some colored token can reach some named graph node
The getColoredPath() function allows for the query of whether some colored token can reach some named graph node. The implementation of this is fairly simple: the Token class implements the IToken interface, which contains a method called getColor(). The Token constructor passes in a TokenColor, which is an enum representing several different kinds of colors. The getColor() method in the Token class simply returns the color of the token. 
The getColoredPath() function looks within the arraylist of nodes and checks whether the node associated with the colored token has an edge between the given graph node. If it does, it returns True, and if it doesn't it returns False.


### Appropriate abstraction
The implementation incorporates many abstract classes and interfaces to allow for abstraction of certain classes. This ensures that any last-minute or minute changes are able to be implemented without too much effort.



# Question 2: Integration of traversal.md implementation
We have been able to successfully integrate this implementation with our client module (Task 3 of Assignment C). The implementation was very easy to read and it followed our specification very closely. In addition, the abstract classes and interfaces helped us to understand the purpose of specific classes. The effort required for us to integrate the specification with our client was approximately 3 to 4 hours. This amount of time includes the understanding of the specification, our research for building client modules, and discussion with group members for appropriate further actions.

# Question 3: Improvements for traversal.md specification
All in all, we believe the implementation we received is very well done. It contains all the necessary information needed in regards to the assignment and our specification. One thing our group could have done better is to specify exactly what classes and interfaces are needed so that we can easily read the code and understand exactly what the code specifies.