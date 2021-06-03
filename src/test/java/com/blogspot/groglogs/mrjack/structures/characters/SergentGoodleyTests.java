package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SergentGoodleyTests {

    @Test
    public void useAbility() {
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        //we use the actual board for this test so we start with SG initial cell
        SergentGoodley sg = (SergentGoodley)Board.getCell(7, 12).getCharacterToken();

        //we need to allocate 3 movement points and use them to move any number of characters CLOSER to SG using max 3 steps total
        //we ignore invalid choices and we check destination end up closer to SG also we force using 3 movement points
        //0 - 7,10 attempts moving IL using 3 points which would be enough BUT we cannot use holes, invalid move
        //2 - 16,7 attempts moving JW farther away, invalid move
        //2 - 12,9 attempts moving JW closer, valid move and uses two steps
        //6 - 9,8 attempts moving SH closer but using more than remaining steps, invalid move
        //6 - 10,7 attempts moving SH closer using remaining steps, valid move
        String actions = "0\n7\n10\n2\n16\n7\n2\n12\n9\n6\n9\n8\n6\n10\n7\n";
        Stdinputter.setStringAsInputStream(actions);
        sg.useAbility();

        assertEquals("IL stayed in place", CharacterType.LESTRADE, Board.getCell(9, 4).getCharacterToken().getCharacterType());
        assertEquals("JW moved to 12,9", CharacterType.WATSON, Board.getCell(12, 9).getCharacterToken().getCharacterType());
        assertEquals("SH moved to 10,7", CharacterType.HOLMES, Board.getCell(10, 7).getCharacterToken().getCharacterType());
    }
}
