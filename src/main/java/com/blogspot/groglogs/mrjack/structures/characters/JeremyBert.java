package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.Cell;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CellType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;

/**
 * Represents the Jeremy Bert character.
 * Can move covers between two holes.
 */
public class JeremyBert extends CharacterToken {

    public JeremyBert(){
        super(CharacterType.BERT, "Jeremy Bert", "JB", 3,
                CharacterAbilityType.ANY_MOVE, true, new CellCoordinate(7,8));
    }

    /**
     * Moves a manhole cover from a covered hole to an open one
     */
    @Override
    public void useAbility() {
        Cell source = getSourceCover();
        Cell dest = getDestCover();
        //swap covers
        source.flipHole();
        dest.flipHole();
    }

    //old hole must be on a covered
    private Cell getSourceCover(){
        System.out.println("Enter hole coordinate to move open:");
        CellCoordinate oldCover = CellCoordinate.askInputCoordinate();
        Cell c = Board.getCell(oldCover);

        while((c.getCellType() != CellType.HOLE && c.getCellType() != CellType.EXIT_HOLE) || c.isHoleOpen()){
            System.out.println("Cover must be moved from a closed hole:");
            oldCover = CellCoordinate.askInputCoordinate();
            c = Board.getCell(oldCover);
        }

        return c;
    }

    //new hole must not be covered
    private Cell getDestCover(){
        System.out.println("Enter hole coordinate to close:");
        CellCoordinate newCover = CellCoordinate.askInputCoordinate();
        Cell c = Board.getCell(newCover);

        while((c.getCellType() != CellType.HOLE && c.getCellType() != CellType.EXIT_HOLE) || !c.isHoleOpen()){
            System.out.println("Cover must be placed on an open hole:");
            newCover = CellCoordinate.askInputCoordinate();
            c = Board.getCell(newCover);
        }

        return c;
    }
}
