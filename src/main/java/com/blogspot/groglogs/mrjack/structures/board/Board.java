package com.blogspot.groglogs.mrjack.structures.board;

import com.blogspot.groglogs.mrjack.structures.enums.CellType;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the game board.
 *
 Board is a rectangle matrix.
 Adjacent columns are are one row up or down distant.
 Adjacent rows are two rows up or down distant.

 Sample grid:
    _
  _/A\_
 /B\_/D\
 \_/C\_/
   \_/

 Matrix output:

   0 1 2
 0   A
 1 B   D
 2   C

 Only movement NOT allowed is B-D.

 Rows have the following number of elements from top to bottom: 1, 2, 5, 6, 6, 7, 6, 7, 6, 7, 6, 7, 6, 6, 5, 2, 1.
 Columns have the following number of elements from left to right: 5, 7, 6, 6, 7, 8, 8, 8, 7, 6, 6, 7, 5.
 */
public class Board {

    public static final int ROWS = 17, COLUMNS = 13;

    /////// INITIAL BOARD STATE /////////

    //we use sets for these to make handling easier later on since we query them often
    //location of all holes that are NOT on an exit, they are open
    protected static final Set<CellCoordinate> holeCells = new HashSet<>();
    //location of all exists that do NOT have a hole, they are open
    protected static final Set<CellCoordinate> exitCells = new HashSet<>();
    //location of all exists that DO have a hole, both exit and hole are closed
    protected static final Set<CellCoordinate> exitHoleCells = new HashSet<>();
    static {
        //holes
        holeCells.add(new CellCoordinate(0,5));
        holeCells.add(new CellCoordinate(4,1));
        holeCells.add(new CellCoordinate(7,10));
        holeCells.add(new CellCoordinate(9,2));
        holeCells.add(new CellCoordinate(9,6));
        holeCells.add(new CellCoordinate(12,11));
        holeCells.add(new CellCoordinate(16,7));
        //exits
        exitCells.add(new CellCoordinate(2,1));
        exitCells.add(new CellCoordinate(14,11));
        //exits with holes
        exitHoleCells.add(new CellCoordinate(2,11));
        exitHoleCells.add(new CellCoordinate(14,1));
    }

    //location of all lamps that are on a timer
    //lamps are added to the array in the order they will shut off
    protected static final CellCoordinate[] orderedOnLampCells = new CellCoordinate[]{
            new CellCoordinate(12,1),
            new CellCoordinate(4,11),
            new CellCoordinate(3,2),
            new CellCoordinate(13,10)
    };

    //location of all lamps that are ON and NOT on a timer
    protected static final CellCoordinate[] onLampCells = new CellCoordinate[]{
            new CellCoordinate(6,7),
            new CellCoordinate(10,5)
    };

    //location of all lamps that are OFF
    protected static final CellCoordinate[] offLampCells = new CellCoordinate[]{
            new CellCoordinate(2,5),
            new CellCoordinate(14,7)
    };

    //location of all houses
    protected static final CellCoordinate[] houseCells = new CellCoordinate[]{
            new CellCoordinate(2,3),
            new CellCoordinate(4,5),
            new CellCoordinate(4,7),
            new CellCoordinate(6,1),
            new CellCoordinate(6,3),
            new CellCoordinate(6,9),
            new CellCoordinate(7,2),
            new CellCoordinate(8,1),
            new CellCoordinate(8,5),
            new CellCoordinate(8,7),
            new CellCoordinate(8,11),
            new CellCoordinate(9,10),
            new CellCoordinate(10,3),
            new CellCoordinate(10,9),
            new CellCoordinate(10,11),
            new CellCoordinate(12,5),
            new CellCoordinate(12,7),
            new CellCoordinate(14,9),
            //these are all border houses
            new CellCoordinate(0,3),
            new CellCoordinate(3,0),
            new CellCoordinate(1,12),
            new CellCoordinate(0,1),
            new CellCoordinate(1,0),
            new CellCoordinate(1,2),
            new CellCoordinate(0,7),
            new CellCoordinate(0,9),
            new CellCoordinate(0,11),
            new CellCoordinate(1,10),
            new CellCoordinate(2,9),
            new CellCoordinate(1,8),
            new CellCoordinate(16,1),
            new CellCoordinate(15,0),
            new CellCoordinate(15,2),
            new CellCoordinate(14,3),
            new CellCoordinate(16,3),
            new CellCoordinate(15,4),
            new CellCoordinate(16,5),
            new CellCoordinate(16,9),
            new CellCoordinate(15,10),
            new CellCoordinate(16,11),
            new CellCoordinate(15,12),
            new CellCoordinate(13,12)

    };
    /////// END INITIAL BOARD STATE /////////

    //the actual board
    private static Cell[][] grid = new Cell[ROWS][COLUMNS];
    //we need to track this in a specific order since we turn one of them off after each turn
    private Queue<Cell> timedLamps;

    /**
     * Initializes the board placing all cells in it.
     */
    public Board(){
        this.timedLamps = new ArrayDeque<>();
        this.initBoard();
    }

    /**
     * Used for testing, we will recreate the board multiple times, therefore we need logic to clear it too.
     */
    public static void clearBoard(){
        grid = new Cell[ROWS][COLUMNS];
    }

    /**
     * Returns the next lamp to turn off at the end of the turn.
     * @return the next lamp to turn off at the end of the turn.
     */
    public Cell getNextLampToTurnOff(){
        if(!timedLamps.isEmpty()){
            return timedLamps.poll();
        }

        return null;
    }

    /**
     * Returns the cell at the given coordinates.
     * Assumes coordinates are valid.
     * @param row
     * @param column
     * @return the cell at the given coordinates.
     */
    public static Cell getCell(int row, int column){
        return getCell(new CellCoordinate(row, column));
    }

    /**
     * Returns the cell at the given coordinates.
     * Assumes coordinates are valid.
     * @param cellCoordinate
     * @return the cell at the given coordinates.
     */
    public static Cell getCell(final CellCoordinate cellCoordinate){
        return grid[cellCoordinate.getRow()][cellCoordinate.getColumn()];
    }

    /**
     * Returns all cells that have a hole.
     * @return all cells that have a hole.
     */
    public static Set<CellCoordinate> getHoleCells(){
        Set<CellCoordinate> holes = new HashSet<>();
        holes.addAll(holeCells);
        holes.addAll(exitHoleCells);
        return holes;
    }

    /**
     * Returns all cells that have an exit.
     * @return all cells that have an exit.
     */
    public static Set<CellCoordinate> getExitCells(){
        Set<CellCoordinate> exits = new HashSet<>();
        exits.addAll(exitHoleCells);
        exits.addAll(exitCells);
        return exits;
    }

    protected Cell placeCell(CellType cellType, final CellCoordinate coordinate){
        if(getCell(coordinate) != null){
            throw new IllegalStateException(
                    String.format("Cell %s (%d,%d) already set", cellType, coordinate.getRow(), coordinate.getColumn()));
        }

        Cell c = new Cell(coordinate, cellType);
        grid[coordinate.getRow()][coordinate.getColumn()] = c;

        return c;
    }

    /**
     * Initializes the board setting all cells in the correct place.
     * Additionally, adds the exitHole cells to the set of holes AFTER everything has been placed on the board.
     */
    private void initBoard(){
        //all holes that are NOT also an exit, initially open
        for(CellCoordinate c : holeCells){
            placeCell(CellType.HOLE, c).flipHole();
        }

        //lamps are added to the array in the order they will shut off
        int lampOffTurn = 1;
        for(CellCoordinate c : orderedOnLampCells){
            Cell cell = placeCell(CellType.LAMP, c);
            cell.flipLamp();
            cell.setLampOffTurn(lampOffTurn);

            lampOffTurn++;
            
            timedLamps.add(cell);
        }

        for(CellCoordinate c : onLampCells){
            placeCell(CellType.LAMP, c).flipLamp();
        }

        for(CellCoordinate c : offLampCells){
            placeCell(CellType.LAMP, c);
        }

        //all exits that do NOT have a hole, initially open
        for(CellCoordinate c : exitCells){
            placeCell(CellType.EXIT, c).flipExit();
        }

        //these exits have an initially blocked hole AND are also blocked by police
        for(CellCoordinate c : exitHoleCells){
            placeCell(CellType.EXIT_HOLE, c);
        }

        for(CellCoordinate c : houseCells){
            placeCell(CellType.HOUSE, c);
        }

        //all other cells are PLAIN
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                if(grid[i][j] == null){
                    grid[i][j] = new Cell(new CellCoordinate(i, j), CellType.PLAIN);
                }
            }
        }

    }

    /**
     * Prints the board a grid of hexagons.
     * Each hexagon spans 4 output lines and 7 output columns.
     * The upper part (2 lines) of an hexagon displays information regarding the cell content.
     * The lower part displays information regarding which character is on that cell (if any)
     * and the coordinates (row, column) of that cell.
     *
     * We therefore alternate between printing upper and lower part of adjacent hexagons within
     * each 2 output line span.
     *
     * Pattern from top of the board is (XX is half hexagon content, YY is coordinate of that hexagon):
     *
     * EVEN ROWS:
     *
     * \ YY  /XX
     *  \___/       and repeat
     *
     * ODD ROWS:
     *
     *  /   \ YY
     * / XX  \___    and repeat
     *
     * EVEN rows only display content from ODD columns and viceversa.
     *
     * Sample:
     *
     *   0    1    2    3    4    5    6    7    8    9    10   11   12
     *        ___       ___       ___       ___       ___       ___
     * \     /###\     /###\     /  O\     /###\     /###\     /###\     /
     *  \___/#### \___/#### \___/     \___/#### \___/#### \___/#### \___/  0
     *  /###\ 0,1 /###\ 0,3 /   \ 0,5 /   \ 0,7 /###\ 0,9 /###\ 0,11/###\
     * /#### \___/#### \___/ WG  \___/     \___/#### \___/#### \___/#### \ 1
     */
    public static void printBoard(){
        //print column numbers of the grid for user reference
        for(int j = 0; j < COLUMNS; j++){
            System.out.print(String.format("%2c%-3d", ' ', j));
        }
        System.out.print("\n");

        //init top of the board
        for(int j = 0; j < COLUMNS; j++){
            //print either the 7 column space output or the top 3 underscores of the very first row of hexagons
            if(j % 2 == 0){
                System.out.print(String.format("%7c", ' '));
            }
            else {
                System.out.print("___");
            }
        }
        System.out.print("\n");

        //print the whole board using our pattern
        for(int i = 0; i < ROWS; i++){

            //for even rows we print the BOTTOM content of the hexagon BEFORE us
            //followed by the TOP content of the hexagon in THIS row
            if(i % 2 == 0){
                //even rows only have hexagons in odd columns -> start with j = 1, increments 2 steps

                //top part of first output line
                for(int j = 1; j < COLUMNS; j += 2){
                    //print coordinates of the hexagon BEFORE us
                    if(i > 0){
                        System.out.print(String.format("\\%2d,%-2d/", i - 1, j - 1));
                    }
                    //first column always start with spaces
                    else {
                        System.out.print(String.format("\\%5c/", ' '));
                    }

                    //then we print the cell content of THIS hexagon
                    grid[i][j].printCellContent();
                }

                //print last output column
                if(i > 0){
                    //coordinates of hexagons in last column
                    System.out.print(String.format("\\%2d,%-2d/\n", i - 1, 12));
                }
                else {
                    //first column always start with spaces
                    System.out.print(String.format("\\%5c/\n", ' '));
                }

                //bottom part of first output line
                for(int j = 1; j < COLUMNS; j += 2){
                    //close hexagon BEFORE us
                    System.out.print(" \\___/");

                    //then print, if any, the character in THIS hexagon
                    grid[i][j].printCellCharacter();
                }

                //print ROW numbers at end of the row for user reference
                System.out.print(String.format(" \\___/%2c%d\n", ' ', i));
            }
            //for odd rows we print the TOP content of the hexagon in THIS row
            //followed by the BOTTOM content of the NEXT hexagon
            else {
                //odd rows only have hexagons in even columns -> start with j = 0, increments 2 steps

                System.out.print(" ");

                //top part of second output line
                for(int j = 0; j < COLUMNS; j += 2){
                    //start top of this hexagon
                    System.out.print("/");

                    //print content of THIS hexagon
                    grid[i][j].printCellContent();

                    //then print coordinates of NEXT hexagon
                    if(j != 12){
                        System.out.print(String.format("\\%2d,%-2d", i - 1, j + 1));
                    }
                }

                //close this first output line
                System.out.print("\\\n");

                //bottom part of second output line
                for(int j = 0; j < COLUMNS; j += 2){
                    //start second line of top of this hexagon
                    System.out.print("/");

                    //print content of THIS hexagon
                    grid[i][j].printCellCharacter();

                    //then close bottom of NEXT hexagon
                    System.out.print(" \\");

                    //close hexagon or add row number for user reference if we reached the last column
                    if(j + 2 <= COLUMNS){
                        System.out.print("___");
                    }
                    else{
                        System.out.print(String.format(" %d", i));
                    }
                }

                //close this output line
                System.out.print("\n");

            }
        }

        //print bottom of the board, closing all half open hexagons with two additional output lines
        System.out.print(" ");
        for(int j = 0; j < COLUMNS; j++){
            if(j % 2 == 0){
                System.out.print(String.format("/%3c", ' '));
            }
            else {
                System.out.print(String.format("\\%2d,%-2d", 16, j));
            }
        }
        System.out.print("\\\n");

        for(int j = 0; j < COLUMNS; j++){
            if(j % 2 == 0){
                System.out.print(String.format("/%5c\\", ' '));
            }
            else {
                System.out.print("___");
            }
        }
        System.out.print("\n");
    }
}
