package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;

/**
 * Represents the Sherlock Holmes character.
 * Can discover an innocent character per turn.
 */
public class SherlockHolmes extends CharacterToken {

    public SherlockHolmes(){
        super(CharacterType.HOLMES, "Sherlock Holmes", "SH", 3,
                CharacterAbilityType.AFTER_MOVE, true, new CellCoordinate(11,6));
    }

    /**
     * SherlockHolmes can use his ability ONLY AFTER he moved
     * @return true if he did move this turn
     */
    @Override
    public boolean canUseAbility(){
        return hasMoved;
    }

    /**
     * Picks a card from the innocent list
     * We have 8 turns and 7 innocents, therefore we could end up in a scenario where we can't reveal another innocent
     */
    @Override
    public void useAbility() {
        CharacterToken c = Game.revealInnocent();
        if(c != null){
            System.out.println();
            System.out.println(String.format("Character %s is INNOCENT", c.getName()));
            System.out.println();
        }
    }
}
