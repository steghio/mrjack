package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterActionType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import com.blogspot.groglogs.mrjack.structures.enums.Direction;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CharacterTokenTests {

    private Board b;
    CharacterToken characterToken;

    @Before
    //since we track static state in the board, we need to reinitialize correctly for each test
    public void initGame(){
        Board.clearBoard();
        b = new Board();
    }

    @Test
    public void getAvailableActions() {
        characterToken = new InspecteurLestrade();
        assertEquals("Character initially has 2 available actions", 2, characterToken.getAvailableActions().size());

        for(CharacterActionType actionType : CharacterActionType.values()){
            assertTrue("Character has expected action", characterToken.getAvailableActions().contains(actionType));
        }
    }

    @Test
    public void setCurrentPosition(){
        characterToken = new InspecteurLestrade();
        //coordinates of a plain cell
        CellCoordinate newPosition = new CellCoordinate(5,0);
        characterToken.setCurrentPosition(newPosition);
        assertEquals("Character is in new position", newPosition, characterToken.getCurrentPosition());
    }

    @Test(expected = IllegalStateException.class)
    public void setCurrentPositionThrowsExceptionIfNewPositionCannotAcceptCharacter(){
        characterToken = new InspecteurLestrade();
        //coordinates of a house
        CellCoordinate newPosition = new CellCoordinate(0,3);
        characterToken.setCurrentPosition(newPosition);
    }

    @Test
    public void checkIsVisible(){
        characterToken = new InspecteurLestrade();

        //initially IL is close to a lamp so it must report being visible
        characterToken.checkIsVisible();
        assertTrue("Character is visible", characterToken.isVisible());

        //if we turn off the lamp and recalculate, he must report being hidden
        Board.getCell(10, 5).flipLamp();
        characterToken.checkIsVisible();
        assertFalse("Character is hidden", characterToken.isVisible());

        //if we place a character near him and recalculate, he must report being visible
        Board.getCell(7, 4).setCharacterToken(new SherlockHolmes());
        characterToken.checkIsVisible();
        assertTrue("Character is visible", characterToken.isVisible());

        //if we turn on the lamp and recalculate, he must stay visible
        Board.getCell(10, 5).flipLamp();
        characterToken.checkIsVisible();
        assertTrue("Character is visible", characterToken.isVisible());
    }

    @Test
    public void getLampDirection(){
        //initially Watson's lamp points NE
        characterToken = new JohnHWatson();
        assertEquals("Lamp points NE", Direction.NORTHEAST, characterToken.getLampDirection());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getLampDirectionThrowsExceptionIfCharacterIsNotWatson(){
        characterToken = new InspecteurLestrade();
        characterToken.getLampDirection();
    }

    @Test
    public void executeCharacterActionsAlwaysAbilityType(){
        characterToken = new MissStealthy();
        //MS can only move so input will be coordinates of the new location
        String actions = "5\n0\n";
        Stdinputter.setStringAsInputStream(actions);
        characterToken.executeCharacterActions();
        assertFalse("MS cannot move anymore", characterToken.canMove());
        assertEquals("MS is at new coordinates 5,0", new CellCoordinate(5,0), characterToken.getCurrentPosition());
    }

    @Test
    public void executeCharacterActionsAnyMoveAbilityType(){
        characterToken = new InspecteurLestrade();
        //IL can move or use his ability any time, must use both
        //we first move him to 7,4 then use his ability to swap barrier from 14,1 to 14,11
        String actions = "0\n7\n4\n1\n14\n1\n14\n11\n";
        Stdinputter.setStringAsInputStream(actions);
        characterToken.executeCharacterActions();
        assertFalse("IL cannot move anymore", characterToken.canMove());
        assertFalse("IL cannot use ability anymore", characterToken.canUseAbility());
        assertEquals("IL is at new coordinates 7,4", new CellCoordinate(7,4), characterToken.getCurrentPosition());
        assertTrue("Exit 14,1 is open", Board.getCell(14,1).isExitOpen());
        assertFalse("Exit 14,11 is blocked", Board.getCell(14,11).isExitOpen());

        //now try opposite, first use ability and then move
        characterToken = new InspecteurLestrade();
        //we first swap barrier from 14,11 to 14,1 then move him to 9,4
        actions = "1\n14\n11\n14\n1\n0\n9\n4\n";
        Stdinputter.setStringAsInputStream(actions);
        characterToken.executeCharacterActions();
        assertFalse("IL cannot move anymore", characterToken.canMove());
        assertFalse("IL cannot use ability anymore", characterToken.canUseAbility());
        assertEquals("IL is at new coordinates 9,4", new CellCoordinate(9,4), characterToken.getCurrentPosition());
        assertTrue("Exit 14,11 is open", Board.getCell(14,11).isExitOpen());
        assertFalse("Exit 14,1 is blocked", Board.getCell(14,1).isExitOpen());
    }

    @Test
    public void executeCharacterActionsAfterMoveAbilityType(){
        characterToken = new JohnHWatson();
        //JW must move before he uses his ability
        //we first move him to 13,8 then use his ability to point the lamp North
        String actions = "13\n8\n0\n";
        Stdinputter.setStringAsInputStream(actions);
        characterToken.executeCharacterActions();
        assertFalse("JW cannot move anymore", characterToken.canMove());
        //This check is linked to whether he moved, so it will be true even AFTER the move
        //this is because the logic to govern this is within the executeCharacterActions method itself which forces
        //the move then act order, which cannot be overturned
        assertTrue("JW can use ability", characterToken.canUseAbility());
        assertEquals("JW is at new coordinates 13,8", new CellCoordinate(13,8), characterToken.getCurrentPosition());
        assertEquals("JW lamp points north", Direction.NORTH, characterToken.getLampDirection());
    }

    @Test
    public void executeCharacterActionsInsteadOfMoveAbilityType(){
        //WG can either move OR use his ability
        characterToken = new SirWilliamGull();
        //we try to move him
        String actions = "0\n3\n4\n";
        Stdinputter.setStringAsInputStream(actions);
        characterToken.executeCharacterActions();
        //this check is linked to whether he used his ability so it will be true even after he moved
        //this is because the logic to govern this is within the executeCharacterActions method itself which forces
        //WG to only execute one action per turn
        assertTrue("WG can move", characterToken.canMove());
        assertFalse("WG cannot use ability", characterToken.canUseAbility());
        assertEquals("WG is at new coordinates 3,4", new CellCoordinate(3,4), characterToken.getCurrentPosition());

        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();
        //now we try to use his ability
        characterToken = new SirWilliamGull();
        //we try to swap him with IL at 9,4
        actions = "1\n0\n";
        Stdinputter.setStringAsInputStream(actions);
        characterToken.executeCharacterActions();
        assertFalse("WG cannot move", characterToken.canMove());
        assertTrue("WG can use ability", characterToken.canUseAbility());
        assertEquals("WG is at IL coordinates 9,4", new CellCoordinate(9,4), characterToken.getCurrentPosition());
        assertEquals("IL is at WG coordinates 1,4", CharacterType.LESTRADE, Board.getCell(1, 4).getCharacterToken().getCharacterType());
    }

    @Test(expected = IllegalStateException.class)
    public void executeCharacterActionsThrowsExceptionIfCharacterHasAlreadyMovedAndMovesAgain(){
        characterToken = new MissStealthy();
        characterToken.hasMoved = true;
        characterToken.executeCharacterActions();
    }

    @Test
    public void askCharacterAction(){
        characterToken = new InspecteurLestrade();

        //try to select move action after some invalid ones
        String actions = "-1\n30\n0\n";
        Stdinputter.setStringAsInputStream(actions);
        assertEquals("Action is MOVE", CharacterActionType.MOVE, characterToken.askCharacterAction());
        assertFalse("Character cannot choose move anymore", characterToken.getAvailableActions().contains(CharacterActionType.MOVE));

        //now if we attempt to move again we get prompted again for a valid action
        actions = "0\n1\n";
        Stdinputter.setStringAsInputStream(actions);
        assertEquals("Action is USE_ABILITY", CharacterActionType.USE_ABILITY, characterToken.askCharacterAction());
        assertTrue("Character has no more available actions", characterToken.getAvailableActions().isEmpty());
    }

    @Test
    public void moveCharacterNoHoles(){
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        characterToken = new InspecteurLestrade();
        //try to move to 10,5 which is a lamp so not allowed
        assertEquals("Trying to move more to obstacle cell returns -1", -1, characterToken.moveCharacterNoHoles(new CellCoordinate(10, 5), 10));

        //try to move to a cell that already has a character which is not allowed
        assertEquals("Trying to move more to occupied cell returns -1", -1, characterToken.moveCharacterNoHoles(new CellCoordinate(11, 6), 10));

        //try to go in 5,4 with only 1 allowed step which is too short
        assertEquals("Trying to move more than 1 step returns -1", -1, characterToken.moveCharacterNoHoles(new CellCoordinate(5, 4), 1));

        //try to stay in place
        assertEquals("Trying to stay in place returns -1", -1, characterToken.moveCharacterNoHoles(characterToken.getCurrentPosition(), 1));

        //try to go in 5,4 with enough steps
        assertEquals("Trying to move within allowed movement returns distance", 2, characterToken.moveCharacterNoHoles(new CellCoordinate(5, 4), 2));

        //try to go in 16,7 with enough steps but without using the hole, which therefore makes it too far
        assertEquals("Trying to move within allowed movement wihtout holes returns -1", -1, characterToken.moveCharacterNoHoles(new CellCoordinate(10, 7), 4));
    }

    @Ignore
    @Test
    public void useAbility(){
        //will be tested for each specific character
    }

    @Test
    public void resetCharacterTurn(){
        characterToken = new InspecteurLestrade();
        characterToken.hasMoved = true;
        characterToken.hasUsedAbility = true;
        characterToken.availableActions = new HashSet<>();
        assertFalse("Character cannot move", characterToken.canMove());
        assertFalse("Character cannot use ability", characterToken.canUseAbility());
        assertTrue("Character has no available actions", characterToken.getAvailableActions().isEmpty());

        characterToken.resetCharacterTurn();
        assertTrue("Character can move", characterToken.canMove());
        assertTrue("Character can use ability", characterToken.canUseAbility());
        assertEquals("Character has 2 available actions", 2, characterToken.getAvailableActions().size());
        for(CharacterActionType action : CharacterActionType.values()){
            assertTrue("Character has action", characterToken.getAvailableActions().contains(action));
        }
    }

    @Test
    public void askCharacterToken(){
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        //if allowCancel is true, we can back out of the choice, we skip invalid input and accept -1
        String choices = "100\n-1\n";
        Stdinputter.setStringAsInputStream(choices);
        assertNull("Canceled character choice returns null", CharacterToken.askCharacterToken(CharacterType.BERT, true));

        //if instead we do not allow cancelling, then -1 is also treated as invalid input
        choices = "100\n-1\n0\n";
        Stdinputter.setStringAsInputStream(choices);
        assertNotNull("Non cancellable character choice returns a character", CharacterToken.askCharacterToken(CharacterType.BERT, false));

        //the character we give to exclude is not selectable
        choices = "1\n0\n";
        Stdinputter.setStringAsInputStream(choices);
        CharacterToken c = CharacterToken.askCharacterToken(CharacterType.BERT, false);
        assertNotNull("Excluded character is not selectable", c);
        assertEquals("Chosen character was selected", CharacterType.LESTRADE, c.getCharacterType());
    }
}
