package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SirWilliamGullTests {

    @Test
    public void useAbility() {
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        SirWilliamGull wg = new SirWilliamGull();
        //attempt to swap places with SG, ignoring invalid input
        //SG is number 5 since we do not offer WG as potential choice
        String actions = "100\n5\n";
        Stdinputter.setStringAsInputStream(actions);
        wg.useAbility();

        assertEquals("WG is in place of SG", CharacterType.GULL, Board.getCell(7, 12).getCharacterToken().getCharacterType());
        assertEquals("SG is in place of WG", CharacterType.GOODLEY, Board.getCell(1, 4).getCharacterToken().getCharacterType());
    }
}
