package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.Cell;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;

/**
 * Represents the Sir William Gull character.
 * Can swap places with another character, but if so he cannot move this turn.
 */
public class SirWilliamGull extends CharacterToken {

    public SirWilliamGull(){
        super(CharacterType.GULL, "Sir William Gull", "WG", 3,
                CharacterAbilityType.INSTEAD_OF_MOVE, false, new CellCoordinate(1,4));
    }

    /**
     * SirWilliamGull can use his ability ONLY if he did not move in the same turn
     * @return true if he did not move this turn
     */
    @Override
    public boolean canUseAbility(){
        return !hasMoved;
    }

    /**
     * SirWilliamGull can move ONLY if he did not use his ability in the same turn
     * @return true if he did not use his ability this turn
     */
    @Override
    public boolean canMove(){
        return !hasUsedAbility;
    }

    /**
     * Swaps place with another character
     * If ability is used, CANNOT move
     */
    @Override
    public void useAbility() {
        CharacterToken c = this.askCharacterToken(this.characterType, false);

        Cell current = Board.getCell(this.currentPosition);
        Cell target = Board.getCell(c.getCurrentPosition());

        CellCoordinate newPosition = c.getCurrentPosition();

        current.setCharacterToken(c);
        c.setCurrentPosition(this.currentPosition);

        target.setCharacterToken(this);
        this.currentPosition = newPosition;
    }
}
