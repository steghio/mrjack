package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;

/**
 * Represents the Sergent Goodley character.
 * Can force maximum 3 movement points out of other characters that must end up closer to him.
 * Characters cannot use holes or pass through obstacles.
 */
public class SergentGoodley extends CharacterToken {

    public SergentGoodley(){
        super(CharacterType.GOODLEY, "Sergent Goodley", "SG", 3,
                CharacterAbilityType.ANY_MOVE, true, new CellCoordinate(7,12));
    }

    /**
     * Allocate 3 movement points and use them to move 1 max 3 characters CLOSER to him
     * Characters CANNOT use holes to travel
     */
    @Override
    public void useAbility() {
        int movementToDo = 3;
        while(movementToDo > 0) {
            Game.printBoard();

            System.out.println(String.format("\nRemaining moves: %d", movementToDo));

            //get character to move and desired destination
            CharacterToken c = this.askCharacterToken(this.characterType, false);
            CellCoordinate dest = CellCoordinate.askInputCoordinate();

            //destination must be closer to SG than start position
            int initDistance = CellCoordinate.distance(c.getCurrentPosition(), this.currentPosition);
            int endDistance = CellCoordinate.distance(dest, this.currentPosition);

            if(endDistance >= initDistance){
                System.out.println(String.format("Move must end closer to %s", this.name));
                continue;
            }

            //attempt move within remaining steps
            int moved = c.moveCharacterNoHoles(dest, movementToDo);

            if (moved == -1) {
                System.out.println(String.format("Move cannot exceed %d steps", movementToDo));
            } else {
                Board.getCell(c.getCurrentPosition()).setCharacterToken(null);
                Board.getCell(dest).setCharacterToken(c);
                movementToDo -= moved;
            }
        }

    }
}
