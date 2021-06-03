package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.input.ChoicePrompts;
import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.Cell;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import com.blogspot.groglogs.mrjack.structures.enums.Direction;

/**
 * Represents the John H Watson character.
 * Has a lamp pointed in a specific direction.
 * All characters under the lamp light are visible. Lamp light stops when it is a blocking object: HOUSE, LAMP.
 */
public class JohnHWatson extends CharacterToken {

    private Direction direction;

    public JohnHWatson(){
        super(CharacterType.WATSON, "John H. Watson", "JW", 3,
                CharacterAbilityType.AFTER_MOVE, true, new CellCoordinate(15,8));
        this.direction = Direction.NORTHEAST;
    }

    public Direction getLampDirection(){
        return this.direction;
    }

    /**
     * Used only for testing.
     * @param d the new direction where to point WATSON's lamp.
     */
    protected void setLampDirection(Direction d){
        this.direction = d;
    }

    /**
     * JohnHWatson can use his ability ONLY AFTER he moved
     * @return true if he did move this turn
     */
    @Override
    public boolean canUseAbility(){
        return hasMoved;
    }

    @Override
    public void useAbility() {
        int choice = Stdinputter.getIntInput(0, Direction.values().length - 1, ChoicePrompts.promptLampDirection(), "Invalid direction");

        Direction lampDirection = Direction.fromNumVal(choice);

        this.direction = lampDirection;
    }

    /**
     * Lamp light travels in Watson's direction until it hits an obstacle.
     * All characters under the light are visible.
     */
    public void checkVisibleUnderLampRange(){
        //get coordinate delta for Watson's lamp based on his current direction
        CellCoordinate delta = Cell.getNearbyCells().get(this.direction);

        for(int i = this.currentPosition.getRow() + delta.getRow(), j = this.currentPosition.getColumn() + delta.getColumn();
        i >= 0 && i < Board.ROWS && j >= 0 && j < Board.COLUMNS;
        i += delta.getRow(),
        j += delta.getColumn()
        ){
            Cell neighbor = Board.getCell(i, j);
            switch (neighbor.getCellType()) {
                case HOUSE:
                case LAMP:
                    return;            
                default:
                    CharacterToken c = neighbor.getCharacterToken();
                    if(c != null){
                        c.markVisible();
                    }
            }

        }
    }
}
