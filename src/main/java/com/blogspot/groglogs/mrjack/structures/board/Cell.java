package com.blogspot.groglogs.mrjack.structures.board;

import com.blogspot.groglogs.mrjack.structures.characters.CharacterToken;
import com.blogspot.groglogs.mrjack.structures.enums.CellType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import com.blogspot.groglogs.mrjack.structures.enums.Direction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a cell in the board.
 */
public class Cell {

    /**
     * Nearby cells have this delta:
     * - North = row - 2
     * - NorthEast = row - 1, column + 1
     * - SouthEast = row + 1, column + 1
     * - South = row + 2
     * - SouthWest = row + 1, column - 1
     * - NorthWest = row - 1, column - 1
     */
    private static final Map<Direction, CellCoordinate> nearbyCells = new HashMap<>();
    static{
        nearbyCells.put(Direction.NORTH, new CellCoordinate(-2, 0)); 
        nearbyCells.put(Direction.NORTHEAST, new CellCoordinate(-1, 1));
        nearbyCells.put(Direction.SOUTHEAST, new CellCoordinate(1, 1));
        nearbyCells.put(Direction.SOUTH, new CellCoordinate(2, 0));
        nearbyCells.put(Direction.SOUTHWEST, new CellCoordinate(1, -1));
        nearbyCells.put(Direction.NORTHWEST, new CellCoordinate(-1, -1));
    }

    /**
     * Retrieves the list of delta coordinates necessary to reach nearby cells from a given cell.
     * @return the list of delta coordinates necessary to reach nearby cells from a given cell.
     */
    public static Map<Direction, CellCoordinate> getNearbyCells(){
        return nearbyCells;
    }

    private final CellCoordinate cellCoordinate;
    private final CellType cellType;
    private boolean isExitOpen, isHoleOpen, isLampOn;
    private int lampOffTurn;
    private CharacterToken characterToken;

    /**
     * Creates a Cell with the given coordinate and type.
     * Sets NO character in this cell.
     * Sets lampOffTurn = -1.
     * Sets all attributes for lamp, hole and exit to false.
     * @param cellCoordinate
     * @param cellType
     */
    public Cell(final CellCoordinate cellCoordinate, CellType cellType){
        this.cellCoordinate = cellCoordinate;
        this.cellType = cellType;
        this.isExitOpen = false;
        this.isHoleOpen = false;
        this.isLampOn = false;
        this.lampOffTurn = -1;
        this.characterToken = null;
    }

    public CellType getCellType(){
        return this.cellType;
    }

    public CharacterToken getCharacterToken(){
        return this.characterToken;
    }

    public CellCoordinate getCellCoordinate(){
        return this.cellCoordinate;
    }

    public int getlampOffTurn(){
        return this.lampOffTurn;
    }

    public boolean isExitOpen(){
        return this.isExitOpen;
    }

    public boolean isHoleOpen(){
        return this.isHoleOpen;
    }

    public boolean isLampOn(){
        return this.isLampOn;
    }

    /**
     * Checks whether this cell can accept a character.
     * If cell is HOUSE or LAMP, not possible.
     * If cell already has a character in it, not possible.
     * @return true if a character token can be placed here.
     */
    public boolean canAcceptCharacter(){
        return this.canCharacterMoveThrough() && this.characterToken == null;
    }

    /**
     * Checks whether a character can move through this cell.
     * If cell is HOUSE or LAMP, not possible.
     * @return true if a character token can move through here.
     */
    public boolean canCharacterMoveThrough(){
        return this.cellType != CellType.HOUSE && this.cellType != CellType.LAMP;
    }

    /**
     * Returns nearby cells always excluding out of bounds.
     * @param canGoThroughObstacles true if cells that normally do not allow character movement should be included (HOUSE, LAMP).
     * @return valid nearby cells to this one, given the specific input criteria.
     */
    public Set<CellCoordinate> getNeighbors(boolean canGoThroughObstacles){
        Set<CellCoordinate> destinations = new HashSet<>();

        for(CellCoordinate delta : nearbyCells.values()){
            //always invalid cells
            if(this.cellCoordinate.getRow() + delta.getRow() < 0 ||
                    this.cellCoordinate.getRow() + delta.getRow() >= Board.ROWS ||
                    this.cellCoordinate.getColumn() + delta.getColumn() < 0 ||
                    this.cellCoordinate.getColumn() + delta.getColumn() >= Board.COLUMNS
            ){
                continue;
            }

            Cell dest = Board.getCell(this.cellCoordinate.getRow() + delta.getRow(), this.cellCoordinate.getColumn() + delta.getColumn());
            
            //skip cell if it's an obstacle and we are not allowed to go through them
            if(!canGoThroughObstacles && !dest.canCharacterMoveThrough()){
                continue;
            }
            
            destinations.add(dest.getCellCoordinate());
        }
        return destinations;
    }

    /**
     * If this cell is an open hole, return all valid connected holes.
     * @return all valid connected holes, if this cell is an open hole.
     */
    public Set<CellCoordinate> getConnectedHoles(){
        Set<CellCoordinate> holes = new HashSet<>();

        if((this.cellType != CellType.HOLE && this.cellType != CellType.EXIT_HOLE) || !this.isHoleOpen){
            return holes;
        }

        //collect all valid exit holes, exclude this cell and blocked holes
        for(CellCoordinate c : Board.getHoleCells()){
            Cell dest = Board.getCell(c);
            if(c.equals(this.cellCoordinate) || !dest.isHoleOpen()){
                continue;
            }

            holes.add(c);
        }

        return holes;
    }

    /**
     * Validates whether this cell has at least one of the expected types, if not, throws an exception.
     * @param expectedTypes all acceptable types for this cell.
     * @throws IllegalStateException if this cell has NONE of the expectedTypes.
     */
    private void checkIsValidCell(CellType... expectedTypes){
        boolean isValid = false;

        for(CellType t : expectedTypes){
            if(this.cellType == t){
                isValid = true;
                break;
            }
        }

        if(!isValid){
            throw new IllegalStateException(String.format("Cell (%d,%d) is not a (%s) cell", this.cellCoordinate.getRow(), this.cellCoordinate.getColumn(), expectedTypes));
        }
    }

    /**
     * Inverts this exit, eg open it if it was closed and viceversa.
     */
    public void flipExit(){
        this.checkIsValidCell(CellType.EXIT, CellType.EXIT_HOLE);
        this.isExitOpen = !this.isExitOpen;
    }

    /**
     * Inverts this hole, eg open it if it was closed and viceversa.
     */
    public void flipHole(){
        this.checkIsValidCell(CellType.HOLE, CellType.EXIT_HOLE);
        this.isHoleOpen = !this.isHoleOpen;
    }

    /**
     * Inverts this lamp, eg turn it OFF it if it was ON and viceversa.
     */
    public void flipLamp(){
        this.checkIsValidCell(CellType.LAMP);
        this.isLampOn = !this.isLampOn;
    }

    /**
     * Sets the turn after which this lamp should shut OFF.
     */
    public void setLampOffTurn(int lampOffTurn){
        this.checkIsValidCell(CellType.LAMP);
        this.lampOffTurn = lampOffTurn;
    }

    /**
     * Sets the given character token as residing on this cell.
     * If argument is null, character is removed from cell, otherwise cell is updated to hold this character
     * and character's current position is updated to point to this cell.
     * @param characterToken the character to set on this cell, null to remove the current one.
     */
    public void setCharacterToken(final CharacterToken characterToken){
        this.checkIsValidCell(CellType.PLAIN, CellType.HOLE, CellType.EXIT, CellType.EXIT_HOLE);

        this.characterToken = characterToken;

        if(characterToken != null) {
            this.characterToken.setCurrentPosition(this.cellCoordinate);
        }
    }

    /**
     * Prints the content of this cell.
     * PLAIN cells contain nothing.
     * EXIT cells can be open(E) or blocked (M).
     * HOLE cells can be open(O) or closed (X).
     * LAMP cells can be ON(Y) or OFF(I). If this lamp is on a timer, also displays after which turn it will turn OFF.
     * HOUSE cells are obstacles (#).
     * EXIT_HOLE cells follow the logic of both HOLE and EXIT.
     */
    public void printCellContent(){
        StringBuffer sb = new StringBuffer();
        if(this.getCellType() == CellType.PLAIN){
            sb.append(" ");
        } else {
            switch(this.getCellType()){
                case EXIT:
                    if(this.isExitOpen()){
                        sb.append("E");
                    } else {
                        sb.append("M");
                    }
                    break;
                case HOLE:
                    if(this.isHoleOpen()){
                        sb.append("O");
                    } else {
                        sb.append("X");
                    }
                    break;
                case LAMP:
                    if(this.isLampOn()){
                        if(this.lampOffTurn == -1){
                            sb.append("Y");
                        }
                        else{
                            sb.append(String.format("Y%-1d", this.getlampOffTurn()));
                        }
                    } else {
                        sb.append("I");
                    }
                    break;
                case HOUSE:
                    sb.append("###");
                    break;
                case EXIT_HOLE:
                    if(this.isExitOpen()){
                        sb.append("E");
                    } else {
                        sb.append("M");
                    }

                    if(this.isHoleOpen()){
                        sb.append("O");
                    } else {
                        sb.append("X");
                    }
                    break;
                default:
                    throw new IllegalStateException(String.format("Unknown cell type %s", this.getCellType()));
            }
        }
        System.out.print(String.format("%3s", sb));
    }

    /**
     * Prints the character on this cell, if any.
     * HOUSE cells have nothing, print #.
     * If character is Watson, also print the direction his lamp is pointing.
     */
    public void printCellCharacter(){
        if(this.cellType == CellType.HOUSE){
            System.out.print("####");
            return;
        }

        CharacterToken characterToken = this.getCharacterToken();

        if(characterToken == null){
            System.out.print(String.format("%4c", ' '));
        } else {
            if(characterToken.getCharacterType() == CharacterType.WATSON){
                System.out.print(String.format("%s%-2s", characterToken.getShortName(), characterToken.getLampDirection().toString()));
            }
            else{
                System.out.print(String.format(" %s ", characterToken.getShortName()));
            }
        }
    }
}
