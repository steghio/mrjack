package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.Direction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JohnHWatsonTests {

    @Test
    public void useAbility() {
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        JohnHWatson jw = new JohnHWatson();
        //use his ability to point the lamp North, ignoring invalid input
        String actions = "100\n0\n";
        Stdinputter.setStringAsInputStream(actions);
        jw.useAbility();
        assertEquals("JW lamp points north", Direction.NORTH, jw.getLampDirection());
    }

    @Test
    public void checkVisibleUnderLampRange() {
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        JohnHWatson jw = new JohnHWatson();
        //move Watson to cell 12,9 then have him point NW
        CellCoordinate newLocation = new CellCoordinate(12, 9);
        jw.setCurrentPosition(newLocation);
        Board.getCell(newLocation).setCharacterToken(jw);
        jw.setLampDirection(Direction.NORTHWEST);

        //add a character in the range of the lamp in 10,7 and a character behind a house in 7,4
        CellCoordinate visibleCharacter = new CellCoordinate(10, 7);
        CellCoordinate hiddenCharacter = new CellCoordinate(7, 4);
        Board.getCell(visibleCharacter).setCharacterToken(new SherlockHolmes());
        Board.getCell(hiddenCharacter).setCharacterToken(new InspecteurLestrade());
        //mark both of them initially invisible
        Board.getCell(visibleCharacter).getCharacterToken().markInvisible();
        Board.getCell(hiddenCharacter).getCharacterToken().markInvisible();

        jw.checkVisibleUnderLampRange();

        assertTrue("Character under light is visible", Board.getCell(visibleCharacter).getCharacterToken().isVisible());
        assertFalse("Character behind house is not visible", Board.getCell(hiddenCharacter).getCharacterToken().isVisible());

        //move Watson to 11,12 and make him point N
        newLocation = new CellCoordinate(11, 12);
        jw.setCurrentPosition(newLocation);
        Board.getCell(newLocation).setCharacterToken(jw);
        jw.setLampDirection(Direction.NORTH);

        //add two characters normally not visible but both under Watson's lamp range
        CellCoordinate c1 = new CellCoordinate(3, 12);
        CellCoordinate c2 = new CellCoordinate(7, 12);//SG is here already
        Board.getCell(c1).setCharacterToken(new InspecteurLestrade());
        //mark both of the initially invisible
        Board.getCell(c1).getCharacterToken().markInvisible();
        Board.getCell(c2).getCharacterToken().markInvisible();

        jw.checkVisibleUnderLampRange();

        assertTrue("c1 under light is visible", Board.getCell(c1).getCharacterToken().isVisible());
        assertTrue("c2 under light is visible", Board.getCell(c2).getCharacterToken().isVisible());
    }
}
