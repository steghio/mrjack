package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class SherlockHolmesTests {

    @Test
    public void useAbility() {
        //we need to clear the board and initialize a game since the logic will check for valid data in the context
        Board.clearBoard();
        Game g = new Game();

        SherlockHolmes sh = new SherlockHolmes();
        sh.useAbility();
        assertFalse("Innocents for the current player is not empty", Game.getPlayerInnocents().isEmpty());
        //we have no way of knowing which innocent was picked since cards are randomized each game
    }
}
