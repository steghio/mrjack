package com.blogspot.groglogs.mrjack.structures.board;

import com.blogspot.groglogs.mrjack.structures.enums.CellType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/*
An initialized board looks like this:

  0    1    2    3    4    5    6    7    8    9    10   11   12
       ___       ___       ___       ___       ___       ___
\     /###\     /###\     /  O\     /###\     /###\     /###\     /
 \___/#### \___/#### \___/     \___/#### \___/#### \___/#### \___/  0
 /###\ 0,1 /###\ 0,3 /   \ 0,5 /   \ 0,7 /###\ 0,9 /###\ 0,11/###\
/#### \___/#### \___/ WG  \___/     \___/#### \___/#### \___/#### \ 1
\ 1,0 /  E\ 1,2 /###\ 1,4 /  I\ 1,6 /   \ 1,8 /###\ 1,10/ MX\ 1,12/
 \___/     \___/#### \___/     \___/     \___/#### \___/     \___/  2
 /###\ 2,1 / Y3\ 2,3 /   \ 2,5 /   \ 2,7 /   \ 2,9 /   \ 2,11/   \
/#### \___/     \___/     \___/     \___/     \___/     \___/     \ 3
\ 3,0 /  O\ 3,2 /   \ 3,4 /###\ 3,6 /###\ 3,8 /   \ 3,10/ Y2\ 3,12/
 \___/     \___/     \___/#### \___/#### \___/     \___/     \___/  4
 /   \ 4,1 /   \ 4,3 /   \ 4,5 /   \ 4,7 /   \ 4,9 /   \ 4,11/   \
/     \___/     \___/     \___/ JS  \___/     \___/     \___/     \ 5
\ 5,0 /###\ 5,2 /###\ 5,4 /   \ 5,6 /  Y\ 5,8 /###\ 5,10/   \ 5,12/
 \___/#### \___/#### \___/     \___/     \___/#### \___/     \___/  6
 /   \ 6,1 /###\ 6,3 /   \ 6,5 /   \ 6,7 /   \ 6,9 /  O\ 6,11/   \
/     \___/#### \___/     \___/     \___/ JB  \___/     \___/ SG  \ 7
\ 7,0 /###\ 7,2 /   \ 7,4 /###\ 7,6 /###\ 7,8 /   \ 7,10/###\ 7,12/
 \___/#### \___/     \___/#### \___/#### \___/     \___/#### \___/  8
 /   \ 8,1 /  O\ 8,3 /   \ 8,5 /  O\ 8,7 /   \ 8,9 /###\ 8,11/   \
/ MS  \___/     \___/ IL  \___/     \___/     \___/#### \___/     \ 9
\ 9,0 /   \ 9,2 /###\ 9,4 /  Y\ 9,6 /   \ 9,8 /###\ 9,10/###\ 9,12/
 \___/     \___/#### \___/     \___/     \___/#### \___/#### \___/  10
 /   \10,1 /   \10,3 /   \10,5 /   \10,7 /   \10,9 /   \10,11/   \
/     \___/     \___/     \___/ SH  \___/     \___/     \___/     \ 11
\11,0 / Y1\11,2 /   \11,4 /###\11,6 /###\11,8 /   \11,10/  O\11,12/
 \___/     \___/     \___/#### \___/#### \___/     \___/     \___/  12
 /   \12,1 /   \12,3 /   \12,5 /   \12,7 /   \12,9 / Y4\12,11/###\
/     \___/     \___/     \___/     \___/     \___/     \___/#### \ 13
\13,0 / MX\13,2 /###\13,4 /   \13,6 /  I\13,8 /###\13,10/  E\13,12/
 \___/     \___/#### \___/     \___/     \___/#### \___/     \___/  14
 /###\14,1 /###\14,3 /###\14,5 /   \14,7 /   \14,9 /###\14,11/###\
/#### \___/#### \___/#### \___/     \___/JWNE \___/#### \___/#### \ 15
\15,0 /###\15,2 /###\15,4 /###\15,6 /  O\15,8 /###\15,10/###\15,12/
 \___/#### \___/#### \___/#### \___/     \___/#### \___/#### \___/  16
 /   \16,1 /   \16,3 /   \16,5 /   \16,7 /   \16,9 /   \16,11/   \
/     \___/     \___/     \___/     \___/     \___/     \___/     \

 */
public class BoardTests {

    private Board b;

    @Before
    //since we track static state in the board, we need to reinitialize correctly for each test
    public void initBoard(){
        Board.clearBoard();
        b = new Board();
    }

    @Test
    //there are 4 lamps that are on a timer, we need to track them in the correct order
    public void timedLampsAreCorrectlyOrdered() {
        for(int i = 0; i < b.orderedOnLampCells.length; i++){
            Cell c = b.getNextLampToTurnOff();
            assertEquals("Cell is LAMP", CellType.LAMP, c.getCellType());
            assertEquals("Lamp should turn off this turn", i + 1, c.getlampOffTurn());
            assertEquals("Lamp has expected coordinates", b.orderedOnLampCells[i], c.getCellCoordinate());
            assertTrue("Lamp is ON", c.isLampOn());
            assertFalse("Exit is CLOSED", c.isExitOpen());
            assertFalse("Hole is CLOSED", c.isHoleOpen());
        }
    }

    @Test
    public void onLampsAreON() {
        for(int i = 0; i < b.onLampCells.length; i++){
            Cell c = Board.getCell(b.onLampCells[i]);
            assertEquals("Cell is LAMP", CellType.LAMP, c.getCellType());
            assertEquals("Lamp has expected coordinates", b.onLampCells[i], c.getCellCoordinate());
            assertTrue("Lamp is ON", c.isLampOn());
            assertFalse("Exit is CLOSED", c.isExitOpen());
            assertFalse("Hole is CLOSED", c.isHoleOpen());
        }
    }

    @Test
    public void offLampsAreOFF() {
        for(int i = 0; i < b.offLampCells.length; i++){
            Cell c = Board.getCell(b.offLampCells[i]);
            assertEquals("Cell is LAMP", CellType.LAMP, c.getCellType());
            assertEquals("Lamp has expected coordinates", b.offLampCells[i], c.getCellCoordinate());
            assertFalse("Lamp is OFF", c.isLampOn());
            assertFalse("Exit is CLOSED", c.isExitOpen());
            assertFalse("Hole is CLOSED", c.isHoleOpen());
        }
    }

    @Test
    public void holesAreOpen() {
        for(CellCoordinate cellCoordinate : b.holeCells){
            Cell c = Board.getCell(cellCoordinate);
            assertEquals("Cell is HOLE", CellType.HOLE, c.getCellType());
            assertEquals("Hole has expected coordinates", cellCoordinate, c.getCellCoordinate());
            assertTrue("Hole is OPEN", c.isHoleOpen());
            assertFalse("Exit is CLOSED", c.isExitOpen());
            assertFalse("LAMP is OFF", c.isLampOn());
        }
    }

    @Test
    public void exitsAreOpen() {
        for(CellCoordinate cellCoordinate : b.exitCells){
            Cell c = Board.getCell(cellCoordinate);
            assertEquals("Cell is EXIT", CellType.EXIT, c.getCellType());
            assertEquals("Exit has expected coordinates", cellCoordinate, c.getCellCoordinate());
            assertTrue("Exit is OPEN", c.isExitOpen());
            assertFalse("Lamp is OFF", c.isLampOn());
            assertFalse("Hole is CLOSED", c.isHoleOpen());
        }
    }

    @Test
    public void holeExitsAreClosed() {
        for(CellCoordinate cellCoordinate : b.exitHoleCells){
            Cell c = Board.getCell(cellCoordinate);
            assertEquals("Cell is EXIT_HOLE", CellType.EXIT_HOLE, c.getCellType());
            assertEquals("ExitHole has expected coordinates", cellCoordinate, c.getCellCoordinate());
            assertFalse("Exit is CLOSED", c.isExitOpen());
            assertFalse("Hole is CLOSED", c.isHoleOpen());
            assertFalse("Lamp is OFF", c.isLampOn());
        }
    }

    @Test
    public void housesAreCorrect() {
        for(CellCoordinate cellCoordinate : b.houseCells){
            Cell c = Board.getCell(cellCoordinate);
            assertEquals("Cell is HOUSE", CellType.HOUSE, c.getCellType());
            assertEquals("House has expected coordinates", cellCoordinate, c.getCellCoordinate());
            assertFalse("Exit is CLOSED", c.isExitOpen());
            assertFalse("Hole is CLOSED", c.isHoleOpen());
            assertFalse("Lamp is OFF", c.isLampOn());
        }
    }

    @Test
    public void getHoleCellsReturnsAllCellsWithAHole() {
        Set<CellCoordinate> cellCoordinates = Board.getHoleCells();

        assertTrue("Result contains all holes", cellCoordinates.containsAll(b.holeCells));
        assertTrue("Result contains all exit holes", cellCoordinates.containsAll(b.exitHoleCells));
    }

    @Test
    public void getExitCellsReturnsAllCellsWithAnExit() {
        Set<CellCoordinate> cellCoordinates = Board.getExitCells();

        assertTrue("Result contains all exits", cellCoordinates.containsAll(b.exitCells));
        assertTrue("Result contains all exit holes", cellCoordinates.containsAll(b.exitHoleCells));
    }

    @Test
    public void placeCellPlacesCellIfAllowed() {
        Board.clearBoard();

        CellCoordinate cellCoordinates = new CellCoordinate(2,2);

        Cell cell = b.placeCell(CellType.PLAIN, cellCoordinates);
        assertNotNull("Cell was placed", cell);
        assertEquals("Cell has correct type", CellType.PLAIN, cell.getCellType());
        assertEquals("Cell has correct coordinates", cellCoordinates, cell.getCellCoordinate());

        Cell cellOnBoard = Board.getCell(cell.getCellCoordinate());
        assertNotNull("Cell is on board", cellOnBoard);
        assertEquals("Cell on board correct type", cell.getCellType(), cellOnBoard.getCellType());

        assertEquals("Cell on board has correct coordinates", cell.getCellCoordinate(), cellOnBoard.getCellCoordinate());
    }

    @Test(expected = IllegalStateException.class)
    public void placeCellThrowsExceptionIfCellCannotBePlaced() {
        //on a normal board we already have a cell in 2,2 therefore we expect exception
        b.placeCell(CellType.PLAIN, new CellCoordinate(2,2));
    }

    @Ignore
    @Test
    //ignored since a human must look at it for imperfections
    public void printBoard() {
        Board.printBoard();
    }
}
