# Tsuro Overview

This repository implements the Tsuro board game. As we progress through the project we will add more to this README description with more concrete details about the repository. Additionally, we will also be able to explain how to run tests and/or the program.

Tsuro is a board game meant for 3 to 5 players who navigate through a 10x10 grid of square holes where they place tiles. The players place tiles on the game board to navigate ports and try to remain on the game board the longest by trying not to reach the edge of the board.

Phase 2 Commit Link: https://github.ccs.neu.edu/cs4500-fall2019-neu/TikiTech/tree/c84d125ed9776913492375fc3471747511d7e31d/Tsuro

Phase 3 Commit Link: https://github.ccs.neu.edu/cs4500-fall2019-neu/TikiTech/tree/06d990e7d6ab05fbbfcd1697668f8522dcfb185a/Tsuro

Phase 4 Commit Link: https://github.ccs.neu.edu/cs4500-fall2019-neu/TikiTech/tree/1f10584b67fc5919c80dcf5277f8b703e820e7e7/Tsuro

Phase 5 Commit Link: https://github.ccs.neu.edu/cs4500-fall2019-neu/TikiTech/tree/b0b6cce52f6b674c76b6906b93eba30f8c4f86c6

Phase 6 Commit Link: https://github.ccs.neu.edu/cs4500-fall2019-neu/TikiTech/tree/23d1ea16215f7fecea17c9c0b8dcd152b0f1c59e

The repository structure currently contains the following subfolders:
- Admin:
    1. IReferee.java
    2. Referee.java
- Planning: 
    1. README.md file to describe the repository and its layout
    2. board.md file to create the interface of the Board
    3. observer.md file to create the interface of the Observer
    4. plan.md file that contains the analysis and plan in the form of a memo
    5. player.md file that contains the interface of a Player
    6. referee.md file that contains the interface of a Referee
    7. rules.md file to create the interface of the RuleChecker
- Common folder that will contain the implementations of Tsuro:
    1. Avatar.java
    2. Board.java
    3. DrawATile.java
    4. DumbStrategy.java
    5. IPlayer.java
    6. Location.java
    7. Player.java
    8. Port.java
    9. Rules.java
    10. Strategy.java
    11. Tiles.java
- Player:
    1. Player.java
- 1:
    1. AllTiles.java file that computes the dtaa representation of all 35 unique configurations of tiles
    2. tile-1.png file that creates 1 unique tile configuration
    3. tile-2.png file that creates 1 unique tile configuration
    4. tile-3.png file that creates 1 unique tile configuration
- 2:
    1. TestProcessor.java
    2. TileTest.java
    3. tile-test.txt
    4. xtiles: bash script
- 3:
    1. BoardTest.java
    2. BoardTestProcessor.java
    3. xboard: bash script
    4. board-tests: folder
- 4:
    1. BiMap.java
    2. CommandValidator.java
    3. RulesTest.java
    4. RulesTestProcessor.java
    5. xrules: bash script
- 5:
    1. observer.java
    2. xref: bash script
    3. ref-tests
    4. xobs: bash script
    5. obs-tests
    6. README-tests.md
