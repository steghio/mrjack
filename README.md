# Mr Jack
A text-based videogame implementing the board game [Mr Jack](http://www.hurricangames.com/game/mr-jack-london-2016)

Code is released under [Attribution-ShareAlike 4.0 International (CC BY-SA 4.0) ](https://creativecommons.org/licenses/by-sa/4.0/legalcode)

**I am NOT affiliated with HurricanGames and/or the creators of the board game and to NOT own copyright to the game**.

## Usage

To play the game, download the [mrjack.jar](MrJack/out/artifacts/mrjack_jar/mrjack.jar) JAR file uploaded here or check out the whole project and build it yourself. You need at least **Java 8** on your system to run the game and the JDK of course if you wish to improve it.

Then run: `java -jar mrjack.jar`

## Game

The game lasts 8 turns and alternates between 2 players.

### Players

Two players alternate in the game: **INSPECTOR** and **JACK**.

The goal of the inspector is to discover who Jack is, while the goal for Jack is to escape.
Jack can escape only if he was hidden at the end of the previous turn and can move out of an open exit.

The inspector can accuse Jack by moving another character on top of Jack. If he is right, he has won the game, otherwise he loses.

If at the end of the last game turn the inspector has still not caught Jack, Jack wins.

### Game turn

Each turn, players will be presented with **2** batches of **4** characters to choose from and will choose one character to play alternating as follows:
* player1 chooses **1** character.
* player2 chooses **2** characters.
* player1 chooses **1** character.

On **ODD** turns the inspector starts, on **EVEN** turns Jack starts.

#### Player actions

Each player can execute the following actions on his turn:
* *PLAY_CHARACTER*: choose a character token and play it.
* *VIEW_INNOCENTS*: shows, if any, the innocents revealed through Holmes ability for this player so far.
* *VIEW_JACK(2)*: can only be done by Jack, shows which character is Jack.
* *VIEW_GAME_STATUS*: prints information about the current game turn.
* *VIEW_GAME_BOARD*: prints the current status of the game board.
* *ACCUSE_JACK*: can only be done by the inspector. Choose one character token and move it onto another character token in order to accuse him. 
  If no character is reachable, cannot execute the action.
  If the target can be reached, inspector wins if the target is indeed Jack, otherwise Jack wins.
  
### Visibility

At the end of each turn, each character will have a certain visibilty state: **VISIBLE** or **HIDDEN**.

A character is visible if:
* is near another character.
* is near an ON lamp.
* is under Watson's lamp light.

### Characters

The following characters are available in the game, each has a can move **3** steps per turn and has an ability.
Depending on the character, the order in which these actions can be performed might be forced.
A character **MUST** move at least one step in his turn.

#### Inspecteur Lestrade - IL

Must move a barrier from a **BLOCKED** to an **OPEN** exit anytime during his turn.

#### Jeremy Bert - JB

Must move a hole cover from a **CLOSED** to an **OPEN** hole anytime during his turn.

#### John H Watson - JW

At the end of his turn, must choose a direction in which to point his lamp. Every character under the lamp light is visible.
When the lamp light reaches an obstacle, it is interrupted.

The direction in which Watson's lamp is pointed is represented on the board, starting from the top of the hexagon and moving clockwise:
* *N*: North
* *NE*: NorthEast
* *SE*: SouthEast
* *S*: South
* *SW*: SouthWest
* *NW*: NorthWest

#### John Smith - JS

Must swap an **OFF** with an **ON** lamp anytime during his turn.

#### Miss Stealthy - MS

Can move **4** steps. Can pass through obstacles.

#### Sergent Goodley - SG

Must share **3** movement points among any number of characters and force them to move towards him without using holes or special abilities.
Can perform this action at once before or after his movement this turn.

#### Sherlock Holmes - SH

At the end of his turn, must draw a card from the innocents stack, can use this information to his advantage.

#### Sir William Gull - WG

Can decide whether to move or swap places with any other character on the board.

### Game Board

#### Cells

Each cell in the board is a hexagon and can have one of the following types:
* *PLAIN*: a simple cell with nothing in it, can accept a character token.
* *HOUSE*: an obstacle, cannot accept a character token.
* *LAMP*: an obstacle, cannot accept a character token. A lamp can be **ON** or **OFF**. Some lamps are on a timer and will automatically turn off at the end of a specific turn.
* *HOLE*: a cell with a hole, can accept a character token, is connected to other cells with holes. A hole can be **OPEN** or **CLOSED**.
* *EXIT*: an escape route, can accept a character token. Escaping requires a movement point. An exit can be **OPEN** or **BLOCKED**.
* *EXIT_HOLE*: a cell that is both *EXIT* and *HOLE*.

### Symbols

The game is text based, therefore it uses ASCII art to display the board. The following symbols are used to represent information:
* **#**: indicates the cell is a house.
* **Y**: indicates an **ON** lamp. If the lamp is on a timer, it will also display the **turn** after which it will turn off automatically.
* **I**: indicates an **OFF** lamp.
* **O**: indicates an **OPEN** hole.
* **X**: indicates a **CLOSED** hole.
* **E**: indicates an **OPEN** exit.
* **M**: indicates a **BLOCKED** exit.
* each character symbol is listed in the [character details](#characters).
* the possible directions for Watson's lamp are listed in [Watson's character description](#john-h-watson---jw).
* each cell will also print its coordinates in the form `row,column`.