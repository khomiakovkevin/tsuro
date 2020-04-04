# Dillinger

```
Referee             Proxy                               Player(s)
  |         ||        +<---------------------------        | Start the client by providing name and strategy:
  |         ||        |                                    | "NAME, STRATEGY" (ex. "Bob, Dumb")
  |         ||        |                                    |
  |         ||        |                                    |
  |<------------------|------------------------------------| Connect TCP 
  |         ||        |                                    |
  |<------------------| Sends name and strategy            |
  |         ||        |                                    |
  |-------------------|----------------------------------->|  Assign avatar to player
  |         ||        |                                    | "Referee assigned you an avatar"
  |         ||        |                                    | 
  |         ||        |                                    |
  |         ||        |----------------------------------->| Show initial game board (String representation of JSON Array of grid)
  |         ||        |                                    |
  |         ||        |                                    | * End of Starting Period *
  |         ||        |                                    | * Begin Init Placement *
  |         ||        |                                    |
  |-------------------|----------------------------------->| Send initial hand to Player
  |         ||        |----------------------------------->| (String representation of ArrayList of 3 Tiles)
  |         ||        |                                    |
  |-------------------|----------------------------------->| "Provide initial placement"
  |         ||        |                                    |
  |<------------------|------------------------------------| Send initial placement:
  |         ||        |                                    | [ [color, tile-index, rotation, x, y], tile-index, tile-index, tile-index ] (JSON)
  |         ||        |                                    |
  |-------------------|----------------------------------->| "Referee has sent you updated board state"
  |         ||        |                                    | (JSON String representation of board)
  |         ||        |                                    |
  |-------------------|----------------------------------->| "Provide intermediate placement"
  |         ||        |----------------------------------->| (String representation of ArrayList of 2 Tiles)
  |         ||        |                                    |
  |<------------------|------------------------------------| Send intermediate placement:
  |         ||        |                                    | [ [color, tile-index, rotation, x, y], tile-index, tile-index ] (JSON)
  |         ||        |                                    |
  
  |<------------------|---------------OR-------------------| Made an illegal move
  |<------------------|---------------OR-------------------| Lost the game
  |<------------------|---------------OR-------------------| Won
  |        ...        |                ...                 |        
  |         ||        |                                    |
  |-------------------|----------------------------------->| "You made an illegal move and you were kicked from the game!"
  |-------------------|----------------------------------->| "You were eliminated from the game!"
  |-------------------|----------------------------------->| "You won the game!"
  |        ||         |                                    |
  |        ||         |                                    | 
  |        ||         |                                    |
  |<------------------|------------------------------------| Close console (^D)
  |        ||          |                 -
  |<-------------------|  Disconnect TCP -
  |        ||          -                 -
```