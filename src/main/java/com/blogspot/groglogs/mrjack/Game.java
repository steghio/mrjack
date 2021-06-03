package com.blogspot.groglogs.mrjack;

import com.blogspot.groglogs.mrjack.input.ChoicePrompts;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.Cell;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.deck.CharacterDeck;
import com.blogspot.groglogs.mrjack.structures.characters.CharacterToken;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import com.blogspot.groglogs.mrjack.structures.characters.JohnHWatson;
import com.blogspot.groglogs.mrjack.structures.enums.PlayerActionType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

/**
 * Drives the game logic.
 * Status is static, can be accessed from everywhere.
 */
public class Game {

    private final static int MAX_TURNS = 8;
    private final static int CHARACTER_BATCH_SIZE = 4;
    public final static boolean PLAYER_JACK = false;
    public final static boolean PLAYER_INSPECTOR = true;

    //game state
    private static Board board;
    private static int turn;
    private static CharacterDeck characterDeck;
    private static CharacterToken jack;
    //HOLMES can peek here to gain data
    private static Queue<CharacterToken> innocents;
    //for each player track here the data gathered through HOLMES
    private static Map<Boolean, Set<CharacterToken>> playerRevealedInnocents;
    private static Map<CharacterType, CharacterToken> characters;
    //turn state
    private static boolean isJackVisible; //must be kept separated from actual character status to avoid leaking info
    private static boolean hasAttemptedToAccuseJack;
    private static Queue<CharacterToken> turnCharacters;
    private static boolean currentPlayer;

    /**
     * Initializes the game
     */
    public Game(){
        turn = 1;

        //for each player track here the data gathered through HOLMES
        playerRevealedInnocents = new HashMap<>();

        //initialize board
        board = new Board();

        //init deck and pick random jack card
        characterDeck = new CharacterDeck();
        innocents = this.characterDeck.shuffle();
        jack = innocents.poll();
        jack.setJack();
        printJack();

        //place the characters on the board, we reuse the deck here
        characters = new TreeMap<>();
        this.initCharactersPositions();

        //calculate initial visibility state for all characters
        this.checkCharactersVisible();
        //at the beginning jack is ALWAYS visible
        isJackVisible = true;

        //inspector ALWAYS starts
        currentPlayer = PLAYER_INSPECTOR;
        hasAttemptedToAccuseJack = false;
    }

    /**
     * Place characters on the board in their initial position.
     * We use the deck to loop over all of them, then place them in a TreeMap
     * to have them ordered by CharacterType (roughly equal to name) for pretty printing
     * and easier access later.
     */
    private void initCharactersPositions(){
        for(int i = 0; i < CharacterDeck.NUM_CHARACTERS; i++){
            CharacterToken c = characterDeck.getCharacterTokenAtPosition(i);

            characters.put(c.getCharacterType(), c);

            CellCoordinate pos = c.getCurrentPosition();

            Cell cell = Board.getCell(pos);
            cell.setCharacterToken(c);
        }
    }

    public static boolean getCurrentPlayer(){
        return currentPlayer;
    }

    /**
     * Returns the revealed innocents through HOLMES for the current player.
     * @return the revealed innocents through HOLMES for the current player.
     */
    public static Set<CharacterToken> getPlayerInnocents(){
        return playerRevealedInnocents.get(currentPlayer);
    }

    /**
     * When HOLMES reveals an innocent, we track it in the data for the current player for later review.
     * @param c the revealed innocent character.
     */
    private static void addCharacterToPlayerInnocents(final CharacterToken c){
        Set<CharacterToken> chars = playerRevealedInnocents.get(currentPlayer);

        if(chars == null){
            chars = new HashSet<>();
        }

        chars.add(c);

        playerRevealedInnocents.put(currentPlayer, chars);
    }

    /**
     * Returns the character token for this character.
     * @param characterType the character to pick.
     * @return the character token for the given character.
     */
    public static CharacterToken getCharacterToken(CharacterType characterType){
        return characters.get(characterType);
    }

    /**
     * Used by HOLMES to pick an innocent card from our queue and reveal it.
     * @return the revealed innocent character.
     */
    public static CharacterToken revealInnocent(){
        if(!innocents.isEmpty()){
            CharacterToken c = innocents.poll();
            Game.addCharacterToPlayerInnocents(c);
            return c;
        }
        return null;
    }

    /**
     * Prints game information at this turn.
     */
    private static void printGameStatus(){
        System.out.println(String.format("Turn: %d", turn));
        System.out.println(String.format("Jack is visible: %b", isJackVisible));

        for(CharacterToken c : characters.values()){
            System.out.println(String.format("%s isVisible: %b", c.getName(), c.isVisible()));
        }

        System.out.println();
    }

    private static void printJack(){
        System.out.println(String.format("JACK is: %s", jack.getName()));
    }

    private static void printPlayerRevealedInnocents(){
        if(getPlayerInnocents() == null){
            System.out.println("No innocents discovered so far");
            return;
        }
        for(CharacterToken c : getPlayerInnocents()) {
            System.out.println(String.format("%s is INNOCENT", c.getName()));
        }
    }

    public static void printBoard(){
        Board.printBoard();
    }

    /**
     * Handles end of turn logic.
     */
    private void endTurn(){
        //reset character ability uses and moves
        for(CharacterToken c : characters.values()){
            c.resetCharacterTurn();
        }
        //order is important, first we must check visibility THEN we can turn off a lamp if necessary
        this.checkCharactersVisible();
        this.turnOffTimedLamp();
        //move to next player for this turn
        currentPlayer = !currentPlayer;
        hasAttemptedToAccuseJack = false;
        turn++;
    }

    /**
     * Update each character's visibility status at the end of a turn.
     * Initially there is no character near another one, and WATSON's lamp is pointing towards a house,
     * so the only visible ones are the ones close to an ON lamp.
     */
    private void checkCharactersVisible(){
        for(CharacterToken c : characters.values()){
            c.checkIsVisible();
        }

        ((JohnHWatson)characters.get(CharacterType.WATSON)).checkVisibleUnderLampRange();

        //update jack visibility status
        isJackVisible = characters.get(jack.getCharacterType()).isVisible();
    }

    /**
     * Turns off, if any, the timed lamp that has timer = this turn.
     */
    private void turnOffTimedLamp(){
        Cell c = board.getNextLampToTurnOff();
        if(c != null){
            c.flipLamp();
        }
    }

    /**
     * If the given character is JACK and it was invisible at the end of the previous turn AND it can make a move to an
     * open exit, then he can escape and win the game.
     * @param currCharacter the current character.
     * @return true if the current character is JACK and can escape.
     */
    public static boolean canJackEscape(final CharacterToken currCharacter){
        if(!currCharacter.isJack() || isJackVisible){
            return false;
        }

        //check JACK's distance from all exits. He can use special abilities and holes to travel.
        //he must make 1 EXTRA move to leave the exit and win.
        for(CellCoordinate exitCoordinate : Board.getExitCells()){
            int distance = CellCoordinate.distance(jack.getCurrentPosition(), exitCoordinate, true, jack.getCharacterType() == CharacterType.STEALTHY);
            if(!isJackVisible && distance + 1 <= jack.getMaxMovement()){
                return true;
            }
        }

        return false;
    }

    /**
     * Each turn shuffle the deck and draw CHARACTER_BATCH_SIZE characters. There will be two batches for a full turn.
     */
    private void initCharacterChoices(){
        turnCharacters = characterDeck.shuffle();
    }

    /**
     * Each turn we present two batches of CHARACTER_BATCH_SIZE characters as choices.
     * @return CHARACTER_BATCH_SIZE characters batch from the shuffled deck.
     */
    private Map<Integer, CharacterToken> drawBatchCharacterChoices(){
        Map<Integer, CharacterToken> characterBatch = new TreeMap<>();

        for(int i = 0; i < CHARACTER_BATCH_SIZE; i++){
            characterBatch.put(i, turnCharacters.poll());
        }

        return characterBatch;
    }

    /**
     * 2 batches of CHARACTER_BATCH_SIZE characters each turn.
     * pattern is: ODD turns inspector starts 1 - 2 - 1 choices then jack. EVEN turns is the opposite.
     * @param currCharacterChoices
     */
    private static void playCharacterBatch(final Map<Integer, CharacterToken> currCharacterChoices){
        while(!currCharacterChoices.isEmpty()) {

            //alternate between players after their choices are done
            //of course this is tied to the CHARACTER_BATCH_SIZE, should that ever change, this must be adapted
            if(currCharacterChoices.size() == 3 || currCharacterChoices.size() == 1){
                currentPlayer = !currentPlayer;
            }

            //show the available characters in case INSPECTOR wants to accuse JACK
            System.out.println(ChoicePrompts.promptBatchCharacterChoice(currCharacterChoices, false));

            //first, ask player if he wants to do anything BEFORE playing a character
            //until he decides to play a character he can keep doing other actions
            playPlayerAction(currCharacterChoices);

            //when he decides to play a character, play it
            playCharacter(currCharacterChoices);
        }

        //next batch players are inverted
        currentPlayer = !currentPlayer;
    }

    /**
     * Prompts the player for a player action. He can keep doing actions until he decides to play a character,
     * at that point, switch control to character player and continue.
     */
    private static void playPlayerAction(final Map<Integer, CharacterToken> currCharacterChoices){
        int choice;
        PlayerActionType playerAction = null;

        while(playerAction == null){
            //only inspector can attempt to accuse JACK
            int upperBound = PlayerActionType.values().length - 1;
            if(currentPlayer == PLAYER_JACK){
                upperBound--;
            }

            choice = Stdinputter.getIntInput(0, upperBound, ChoicePrompts.promptPlayerAction(), "Invalid choice.");

            playerAction = PlayerActionType.fromNumVal(choice);

            switch (playerAction){
                case VIEW_JACK:
                    //only JACK can view its card
                    if(currentPlayer == PLAYER_JACK) {
                        printJack();
                    }
                    else {
                        System.out.println("Only JACK can view its card.");
                    }
                    playerAction = null;
                    break;
                case VIEW_INNOCENTS:
                    printPlayerRevealedInnocents();
                    playerAction = null;
                    break;
                case VIEW_GAME_STATUS:
                    printGameStatus();
                    playerAction = null;
                    break;
                case VIEW_GAME_BOARD:
                    printBoard();
                    playerAction = null;
                    break;
                case ACCUSE_JACK:
                    //can only attempt once per turn to accuse jack
                    if(!hasAttemptedToAccuseJack){
                        //after this, either a winner was found, or player canceled and we can continue the game
                        playAccuseJack(currCharacterChoices);
                    }
                    else {
                        System.out.println("You can only attempt to accuse JACK once per turn.");
                    }
                    hasAttemptedToAccuseJack = true;
                    playerAction = null;
                    break;
                case PLAY_CHARACTER:
                    break;
                default:
                    throw new IllegalStateException(String.format("Unknown player action %s", playerAction));
            }
        }
    }

    /**
     * Prompts the player for a character. He can move this character onto another character location to accuse that
     * character.
     * If target character is JACK, INSPECTOR wins, otherwise JACK wins.
     * If no valid choice can be made, player can continue playing other choices.
     * Only the inspector can attempt to accuse jack.
     */
    //TODO would be nice if the choices presented as possible characters to accuse are only all valid reachable characters from the chosen one
    private static void playAccuseJack(final Map<Integer, CharacterToken> currCharacterChoices){
        if(currentPlayer != PLAYER_INSPECTOR){
            throw new IllegalStateException("Only the INSPECTOR can attempt to accuse JACK.");
        }

        int choice = promptBatchCharacter(currCharacterChoices);
        CharacterToken currCharacter = currCharacterChoices.get(choice);

        //player must now choose another character to accuse. This character must be reachable using holes and special abilities
        int distance = -1;

        while(distance == -1){
            System.out.println("You can accuse Jack only if you can reach him, choose Jack:");
            CharacterToken characterToAccuse = CharacterToken.askCharacterToken(currCharacter.getCharacterType(), true);

            if(characterToAccuse == null){
                return;
            }

            distance = CellCoordinate.distance(currCharacter.getCurrentPosition(), characterToAccuse.getCurrentPosition(), true, currCharacter.getCharacterType() == CharacterType.STEALTHY);

            if(distance > 0 && distance <= currCharacter.getMaxMovement()){
                if(characterToAccuse.isJack()){
                    System.out.println("JACK IS CAUGHT - INSPECTOR WON");
                }
                else {
                    System.out.println(String.format("%s IS INNOCENT, JACK ESCAPES", characterToAccuse.getName()));
                }

                System.exit(0);
            }

            //reset our marker to keep prompting user
            distance = -1;
        }
    }

    /**
     * Prompts the player to choose a character from the available batch to play.
     * @param currCharacterChoices the available batch of characters to choose from.
     * @return the chosen character to play.
     */
    private static int promptBatchCharacter(final Map<Integer, CharacterToken> currCharacterChoices){
        int choice = -1;
        CharacterToken currCharacter = null;

        while(currCharacter == null) {
            choice = Stdinputter.getIntInput(0, 3, ChoicePrompts.promptBatchCharacterChoice(currCharacterChoices, true), "Invalid character.");
            currCharacter = currCharacterChoices.get(choice);
            if(currCharacter == null){
                System.out.println("Invalid character.");
            }
        }

        return choice;
    }

    /**
     * Prompts the player for a character action. He must do the actions according to character specific logic.
     * If chosen character is JACK, it automatically attempts to escape.
     */
    private static void playCharacter(final Map<Integer, CharacterToken> currCharacterChoices){
        int choice = promptBatchCharacter(currCharacterChoices);
        CharacterToken currCharacter = currCharacterChoices.get(choice);

        //this charatcer is no longer available this turn, remove it from the choices
        currCharacterChoices.remove(choice);

        //if jack can escape, game is finished, no need to ask the player if he wants to try and win
        //therefore there is no ESCAPE_JACK player action
        if(Game.canJackEscape(currCharacter)){
            System.out.println("JACK ESCAPED");
            System.exit(0);
        }

        //otherwise, play this character
        currCharacter.executeCharacterActions();
    }

    public static void main(String args[]){
        Game g = new Game();

        while(turn <= MAX_TURNS){
            printBoard();
            printGameStatus();

            g.initCharacterChoices();

            //first batch
            Map<Integer, CharacterToken> currCharacterChoices = g.drawBatchCharacterChoices();
            playCharacterBatch(currCharacterChoices);

            //second batch
            currCharacterChoices = g.drawBatchCharacterChoices();
            playCharacterBatch(currCharacterChoices);

            g.endTurn();
        }

        //if inspector did not catch Jack in time, he wins
        System.out.println("JACK WON");
    }
}
