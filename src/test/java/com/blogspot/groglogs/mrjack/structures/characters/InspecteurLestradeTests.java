package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InspecteurLestradeTests {

    @Test
    public void useAbility(){
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        InspecteurLestrade il = new InspecteurLestrade();
        //can swap barrier from a closed exit to an open exit, will skip invalid input and open exits
        String actions = "14\n11\n4\n1\n14\n1\n14\n11\n";
        Stdinputter.setStringAsInputStream(actions);
        il.useAbility();
        assertTrue("Exit 14,1 is open", Board.getCell(14,1).isExitOpen());
        assertFalse("Exit 14,11 is blocked", Board.getCell(14,11).isExitOpen());
    }
}
