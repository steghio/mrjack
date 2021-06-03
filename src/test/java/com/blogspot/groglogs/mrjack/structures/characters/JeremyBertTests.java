package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JeremyBertTests {

    @Test
    public void useAbility(){
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        JeremyBert jb = new JeremyBert();
        //can swap hole cover from a closed hole to an open hole, will skip invalid input and open holes
        String actions = "9\n6\n7\n2\n2\n11\n4\n1\n";
        Stdinputter.setStringAsInputStream(actions);
        jb.useAbility();
        assertTrue("Hole 2,11 is open", Board.getCell(2,11).isHoleOpen());
        assertFalse("Hole 4,1 is blocked", Board.getCell(4,1).isHoleOpen());
    }
}
