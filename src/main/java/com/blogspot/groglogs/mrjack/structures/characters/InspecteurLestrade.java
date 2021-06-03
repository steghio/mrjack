package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.Cell;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CellType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;

/**
 * Represents the Inspecteur Lestrade character.
 * Can move barriers between two exits.
 */
public class InspecteurLestrade extends CharacterToken {

    public InspecteurLestrade(){
        super(CharacterType.LESTRADE, "Inspecteur Lestrade", "IL", 3,
                CharacterAbilityType.ANY_MOVE, true, new CellCoordinate(9,4));
    }

    /**
     * Moves a police barrier from one blocked exit to an open one
     */
    @Override
    public void useAbility() {
        Cell source = getSourceBarrier();
        Cell dest = getDestBarrier();
        //swap barriers
        source.flipExit();
        dest.flipExit();
    }

    //old barrier must be present where we want to remove it from
    private Cell getSourceBarrier(){
        System.out.println("Enter barrier coordinate of blocked exit to open:");
        CellCoordinate oldBarrier = CellCoordinate.askInputCoordinate();
        Cell c = Board.getCell(oldBarrier);

        while((c.getCellType() != CellType.EXIT && c.getCellType() != CellType.EXIT_HOLE) || c.isExitOpen()){
            System.out.println("Barrier must be moved from a blocked exit:");
            oldBarrier = CellCoordinate.askInputCoordinate();
            c = Board.getCell(oldBarrier);
        }

        return c;
    }

    //new barrier must not be present where we want to place it
    private Cell getDestBarrier(){
        System.out.println("Enter barrier coordinate of open exit to close:");
        CellCoordinate newBarrier = CellCoordinate.askInputCoordinate();
        Cell c = Board.getCell(newBarrier);

        while((c.getCellType() != CellType.EXIT && c.getCellType() != CellType.EXIT_HOLE) || !c.isExitOpen()){
            System.out.println("Barrier must be moved to an open exit:");
            newBarrier = CellCoordinate.askInputCoordinate();
            c = Board.getCell(newBarrier);
        }

        return c;
    }
}