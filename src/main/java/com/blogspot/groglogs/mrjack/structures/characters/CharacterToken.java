package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.ChoicePrompts;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.Cell;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterActionType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import com.blogspot.groglogs.mrjack.structures.enums.Direction;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a character token.
 */
public abstract class CharacterToken {

    protected CharacterType characterType;
    protected String name, shortName;
    protected int maxMovement;
    protected CharacterAbilityType characterAbilityType;
    protected boolean isJack, isVisible, mustUseAbility, hasMoved, hasUsedAbility;
    protected CellCoordinate currentPosition;
    //each turn a character can choose from this pool of actions
    protected Set<CharacterActionType> availableActions;

    public CharacterToken(CharacterType characterType, String name, String shortName, int maxMovement,
                          CharacterAbilityType characterAbilityType, boolean mustUseAbility, CellCoordinate currentPosition){
        //specific character input
        this.characterType = characterType;
        this.name = name;
        this.shortName = shortName;
        this.maxMovement = maxMovement;
        this.characterAbilityType = characterAbilityType;
        this.mustUseAbility = mustUseAbility;
        this.currentPosition = currentPosition;

        //common init
        this.hasMoved = false;
        this.hasUsedAbility = false;
        this.availableActions = new HashSet<>();
        for(CharacterActionType action : CharacterActionType.values()) {
            this.availableActions.add(action);
        }
    }

    public CharacterType getCharacterType(){
        return this.characterType;
    }

    public String getName(){
        return this.name;
    }

    public String getShortName(){
        return this.shortName;
    }

    public CellCoordinate getCurrentPosition(){
        return this.currentPosition;
    }

    public void setCurrentPosition(int row, int column){
        this.setCurrentPosition(new CellCoordinate(row, column));
    }

    public void setCurrentPosition(final CellCoordinate newPosition){
        if(!Board.getCell(newPosition).canCharacterMoveThrough()){
            throw new IllegalStateException(String.format("Coordinate: %s cannot accept a character token", newPosition));
        }
        this.currentPosition = newPosition;
    }

    public int getMaxMovement(){
        return this.maxMovement;
    }

    public Set<CharacterActionType> getAvailableActions(){
        return this.availableActions;
    }

    public boolean isJack(){
        return this.isJack;
    }

    public void setJack(){
        this.isJack = true;
    }

    public boolean isVisible(){
        return this.isVisible;
    }

    public void markVisible(){
        this.isVisible = true;
    }

    public void markInvisible(){
        this.isVisible = false;
    }

    /**
     * A character is visible if:
     * - nearby another character
     * - nearby a lit lamp
     * - under Watson's lamp light //calculated separately when evaluating Watson at end of turn
     */
    public void checkIsVisible(){
        //set invisible by default, then if necessary it will be updated correctly
        this.markInvisible();

        //include also obstacle neighbors since the lamps are there
        for(CellCoordinate c : Board.getCell(this.currentPosition).getNeighbors(true)){

            Cell neighbor = Board.getCell(c);

            //only mark visible here, flipping states is dangerous if not handled correctly eg: character close to an OFF lamp AND another character
            switch(neighbor.getCellType()){
                case LAMP:
                    if(neighbor.isLampOn()){
                        this.markVisible();
                    }
                    break;
                case HOUSE:
                    break;
                default:
                    if(neighbor.getCharacterToken() != null){
                        this.markVisible();
                    }
            }

            //if we already determined we are visible, no need to keep checking
            if(this.isVisible){
                break;
            }
        }
    }

    /**
     * Necessary to expose this information, only WATSON will implement this method.
     * @return WATSON's lamp direction if this character is WATSON, throws an exception otherwise.
     * @throws UnsupportedOperationException if we attempted to invoke this method on a character that is not WATSON.
     */
    public Direction getLampDirection(){
        throw new UnsupportedOperationException("Only Watson has a lamp");
    }

    /**
     * Each character must perform BOTH MOVE and USE_ABILITY actions, except if character logic supersedes that.
     * This method forces the user to choose all necessary actions and execute them in the correct order
     * based on the chosen character type.
     * Accusing Jack can always be done IF this character can reach the one to accuse.
     */
    public void executeCharacterActions(){
        CharacterActionType action;

        //Depending on the ability type, characters must move before using an ability or can't use both move and ability
        switch (this.characterAbilityType){
            case ALWAYS:
                this.executeMoveCharacter();
                break;
            case ANY_MOVE:
                //both actions allowed anytime
                action = this.askCharacterAction();
                executeCharacterAction(action);

                Game.printBoard();

                action = this.askCharacterAction();
                executeCharacterAction(action);
                break;
            case AFTER_MOVE:
                //ability must be used after moving
                this.executeMoveCharacter();

                Game.printBoard();

                if(!this.executeAbility()){
                    throw new IllegalStateException("Attempted to use ability which was not allowed.");
                }
                break;
            case INSTEAD_OF_MOVE:
                //only one action allowed
                action = this.askCharacterAction();
                executeCharacterAction(action);
                break;
            default:
                throw new IllegalStateException(String.format("Unknown action %s", this.characterAbilityType));
        }
        Game.printBoard();
    }

    //Each character must perform BOTH MOVE and USE_ABILITY actions, except if character logic supersedes that
    protected void executeCharacterAction(CharacterActionType action){
        switch (action){
            case MOVE:
                this.executeMoveCharacter();
                break;
            case USE_ABILITY:
                this.executeAbility();
                break;
            default:
                throw new IllegalStateException(String.format("Unknown action %s", action));
        }
    }

    private boolean isTryingDoubleAction(CharacterActionType action){
        switch(action){
            case MOVE:
                return this.hasMoved;
            case USE_ABILITY:
                return this.hasUsedAbility;
            default:
                throw new IllegalStateException(String.format("Unknown action: %s", action));
        }
    }

    /**
     * Each character must execute two things per turn: move and use ability
     * Depending on the character these must be done in a specific order or are mutually exclusive
     */
    public CharacterActionType askCharacterAction(){
        CharacterActionType action = null;

        while(action == null) {
            try {
                int choice = Stdinputter.getIntInput(0, CharacterActionType.values().length - 1, ChoicePrompts.promptCharacterAction(this), "Invalid action.");

                action = CharacterActionType.fromNumVal(choice);

                if(!this.availableActions.contains(action)){
                    System.out.println("Invalid action.");
                    throw new IllegalArgumentException();
                }

                if (this.isTryingDoubleAction(action)) {
                    System.out.println("Action cannot be performed twice in same turn.");
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                action = null;
            }
        }

        //now this action is no longer available to use in this turn
        this.availableActions.remove(action);

        return action;
    }

    /**
     * Most characters can always move ONCE.
     * Special ones MUST override this method with their own logic.
     * @return whether this character can move this turn.
     */
    public boolean canMove(){
        return !hasMoved;
    }

    /**
     * Prompts the player for valid coordinates and moves the character in the desired position.
     */
    private void executeMoveCharacter(){
        if(this.hasMoved){
            throw new IllegalStateException("Character has already moved.");
        }

        System.out.println("Enter destination coordinates to move this character:");
        CellCoordinate destCoordinate = CellCoordinate.askInputCoordinate();

        while(!this.moveCharacter(destCoordinate)){
            System.out.println("Invalid move, enter valid coordinates within the reach of this character's movement:");

            destCoordinate = CellCoordinate.askInputCoordinate();
        }

    }

    /**
     * Tries to move a character to dest cell within its allowed maxMovement.
     * Character can use holes to travel.
     * MissStealthy can pass through obstacles.
     * @param destCoordinate
     * @return false is movement is not allowed
     */
    private boolean moveCharacter(final CellCoordinate destCoordinate){
        if(!this.canMove()){
            return false;
        }

        Cell dest = Board.getCell(destCoordinate);
        if(!dest.canAcceptCharacter()){
            return false;
        }

        if(isValidMove(destCoordinate, this.maxMovement)){
            Board.getCell(this.currentPosition).setCharacterToken(null);
            dest.setCharacterToken(this);
            this.hasMoved = true;
            return true;
        }
        return false;
    }

    /**
     * Checks if this character can reach destination cell within the movement allowance and using holes.
     * Miss Stealthy can cross obstacles.
     * @param destCoordinate destination cell.
     * @param maxMovement the maximum movement allowed to try and reach the given destination.
     * @return true if destination could be reached, false otherwise.
     */
    private boolean isValidMove(final CellCoordinate destCoordinate, int maxMovement){
        return CellCoordinate.distance(this.currentPosition, destCoordinate, true, this.characterType == CharacterType.STEALTHY) <= maxMovement;
    }

    /**
     * Attempts to move a character to dest cell within given allowed maxMovement.
     * Character CANNOT use holes to travel.
     * Miss Stealthy CANNOT pass through obstacles.
     * Does NOT move the character, only calculates whether it can be done.
     * @param destCoordinate the desired destination for this character.
     * @return total steps moved to complete the operation, -1 if operation could not be completed.
     */
    protected int moveCharacterNoHoles(final CellCoordinate destCoordinate, int maxMovement){
        Cell dest = Board.getCell(destCoordinate);

        if(!dest.canAcceptCharacter()){
            return -1;
        }

        int distance = CellCoordinate.distance(this.currentPosition, destCoordinate);

        if(distance > maxMovement){
            return -1;
        }

        return distance;
    }

    /**
     * Each character implements its own logic here for their special ability.
     */
    protected abstract void useAbility();

    /**
     * Executes this character ability if possible
     * @return true if ability was used, false if it was not possible
     */
    private boolean executeAbility(){
        if(this.canUseAbility()){
            this.useAbility();
            this.hasUsedAbility = true;
            return true;
        }
        return false;
    }

    /**
     * Most characters can always use their ability ONCE.
     * Special ones MUST override this method with their own logic.
     * @return whether this character can use his ability this turn.
     */
    public boolean canUseAbility(){
        return !hasUsedAbility;
    }

    /**
     * At the end of a turn, reset character status: movement, ability, available actions.
     */
    public void resetCharacterTurn(){
        this.hasMoved = false;
        this.hasUsedAbility = false;
        for(CharacterActionType action : CharacterActionType.values()) {
            this.availableActions.add(action);
        }
    }

    /**
     * Used to prompt player to choose a character token among the given choices, without the excluded one.
     * @param exclude the character token to exclude from available choices.
     * @param allowCancel true if the player should be presented with the choice to cancel the action. In this case
     *                    it returns null.
     * @return the character token chosen by the player or null if player canceled the action.
     */
    public static CharacterToken askCharacterToken(CharacterType exclude, boolean allowCancel){
        CharacterToken character = null;

        while(character == null) {
            //lower bound is either 0 or -1 depending on whether player can cancel the action or not
            int lowerBound = 0;
            if(allowCancel){
                lowerBound--;
            }

            int choice = Stdinputter.getIntInput(lowerBound, CharacterType.values().length - 1, ChoicePrompts.promptCharacterChoice(exclude, allowCancel), "Invalid character.");

            if (choice == -1 && allowCancel) {
                return null;
            }

            character = Game.getCharacterToken(CharacterType.fromNumVal(choice));

            if(character.getCharacterType().equals(exclude)){
                System.out.println("This character cannot be chosen now.");
                character = null;
            }
        }

        return character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CharacterToken)) return false;
        CharacterToken other = (CharacterToken)o;
        return this.name.equals(other.name);
    }

}
