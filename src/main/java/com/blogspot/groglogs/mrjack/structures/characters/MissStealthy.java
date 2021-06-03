package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;

/**
 * Represents the Miss Stealthy character.
 * Can walk through obstacles.
 */
public class MissStealthy extends CharacterToken {

    public MissStealthy(){
        super(CharacterType.STEALTHY, "Miss Stealthy", "MS",4,
                CharacterAbilityType.ALWAYS, false, new CellCoordinate(9,0));
    }

    /**
     * MissStealthy CANNOT choose to use her ability, it is always active
     * @return always false for Miss Stealthy
     */
    @Override
    public boolean canUseAbility(){
        return false;
    }

    /**
     * Nothing to do, but remember she can pass between obstacles
     */
    @Override
    public void useAbility() {
        throw new UnsupportedOperationException("Cannot use Miss Stealthy ability, it is always active");
    }
}
