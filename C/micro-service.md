# Protocol Specification for a Labyrinth Client-Server model

#### This document is a description of various interactions between user, client and server. 

* User runs the client

* Client reads JSON requests from STDIN, which contain specific commands, then sends these requests to the server
  * JSON requests should be well-formed, i.e.
    * After command "lab", which creates a labyrinth, name pairs specified in the "from"-"to" fields should be pairwise     distinct
    * After command "add", which adds a token to a given node, a name should already be a node in a labyrinth
    * After command "move", which moves a token to a given node, a token should have already been placed and the node refered to by name is a node in a labyrinth

* Server parses these requests, checks if the commands are valid, connects to the socket, processes the commands and returns the responses back to the Client

* Client shows the response to the User

**Below is a sequence diagram that helps visualize the interactions**

#### Sequence diagram

```
Server              Client            User
  |        ||         +<----------------| Start the client
  |        ||         |                 |
  |        ||         |                 |
  |<------------------|   Connect TCP   |
  |        ||         |                 |
  |<------------------|  Ask for Name   |
  |        ||         |                 |
  |------------------>|  Get the Name   | * End of Starting Period *
  |        ||         |                 |
  |        ||         |<----------------| Add the requests
  |        ||         |                 |
  |        ||         |<----------------| Check if it can be reached
  |        ||         |                 |
  |<------------------|  Send the input |
  |        ||         |                 |
  |------------------>| Get the response| 
  |        ||         |                 |
  |                   |---------------->| Execute the Response
  |        ||         |                 |
 ...      ....       ...               ... * Repeat steps after End of Starting Period if various commands are passed *
  |        ||         |                 | 
  |        ||         |                 |
  |        ||         |<----------------| Close console (^D)
  |        ||         |                 -
  |<------------------|  Disconnect TCP -
  |        ||         -                 -
```

**Below is a description of the fields in the diagram**

**Field** | **Description**
------| -----------
**TCP** | Protocol that provides delivery of a stream of bytes between applications
TCP address | LOCALHOST
TCP port | 8000
**Name** | Unique username
**Input** | One of:
Create Labyrinth | ["lab", {"from" : name:string, "to" : name:string} ...]
Add Token | ["add" , token:color-string, name:string]
Move Token | ["move", token:color-string, name:string]
**Response** | Boolean Value representing if the command can be executed
