package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JohnSmithTests {

    @Test
    public void useAbility(){
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        JohnSmith js = new JohnSmith();
        //can turn off a lamp and turn on another one, will skip invalid input and off holes
        String actions = "9\n6\n7\n2\n12\n1\n2\n5\n";
        Stdinputter.setStringAsInputStream(actions);
        js.useAbility();
        assertTrue("Lamp 2,5 is on", Board.getCell(2,5).isLampOn());
        assertFalse("Lamp 12,1 is off", Board.getCell(12,1).isLampOn());
        assertEquals("Lamp 2,5 off turn is 1", 1, Board.getCell(2,5).getlampOffTurn());
    }
}
