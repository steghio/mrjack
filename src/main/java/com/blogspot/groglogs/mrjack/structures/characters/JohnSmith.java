package com.blogspot.groglogs.mrjack.structures.characters;

import com.blogspot.groglogs.mrjack.structures.board.Board;
import com.blogspot.groglogs.mrjack.structures.board.Cell;
import com.blogspot.groglogs.mrjack.structures.board.CellCoordinate;
import com.blogspot.groglogs.mrjack.structures.enums.CellType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterAbilityType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;

/**
 * Represents the John Smith character.
 * Can switch two lamps, one must be OFF and the other ON.
 */
public class JohnSmith extends CharacterToken {

    public JohnSmith(){
        super(CharacterType.SMITH, "John Smith", "JS", 3,
                CharacterAbilityType.ANY_MOVE, true, new CellCoordinate(5,6));
    }

    /**
     * Swaps an off lamp with an on lamp
     */
    @Override
    public void useAbility() {
        Cell source = getSourceLamp();
        Cell dest = getDestLamp();
        //swap lamps
        source.flipLamp();
        dest.flipLamp();
        //the turn to switch off the lamp is tracked in a separate value, do not forget to carry this over too!
        dest.setLampOffTurn(source.getlampOffTurn());
        source.setLampOffTurn(-1);
    }

    //old lamp must be ON
    private Cell getSourceLamp(){
        System.out.println("Enter lamp coordinate to turn OFF:");
        CellCoordinate oldLamp = CellCoordinate.askInputCoordinate();
        Cell c = Board.getCell(oldLamp);

        while(c.getCellType() != CellType.LAMP || !c.isLampOn()){
            System.out.println("Lamp must be ON:");
            oldLamp = CellCoordinate.askInputCoordinate();
            c = Board.getCell(oldLamp);
        }

        return c;
    }

    //new lamp must be OFF
    private Cell getDestLamp(){
        System.out.println("Enter lamp coordinate to turn ON:");
        CellCoordinate newLamp = CellCoordinate.askInputCoordinate();
        Cell c = Board.getCell(newLamp);

        while(c.getCellType() != CellType.LAMP || c.isLampOn()){
            System.out.println("Lamp must be OFF:");
            newLamp = CellCoordinate.askInputCoordinate();
            c = Board.getCell(newLamp);
        }

        return c;
    }
}
