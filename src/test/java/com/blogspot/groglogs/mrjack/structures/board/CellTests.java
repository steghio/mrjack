package com.blogspot.groglogs.mrjack.structures.board;

import com.blogspot.groglogs.mrjack.structures.characters.CharacterToken;
import com.blogspot.groglogs.mrjack.structures.characters.InspecteurLestrade;
import com.blogspot.groglogs.mrjack.structures.enums.CellType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CellTests {

    Board b;

    Cell cell;
    CellCoordinate coordinate;

    @Before
    //since we track static state in the board, we need to reinitialize correctly for each test
    public void initBoard(){
        Board.clearBoard();
        b = new Board();
    }

    @Test
    public void canAcceptCharacter() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.PLAIN);
        assertTrue("Empty plain cell can accept character", cell.canAcceptCharacter());

        cell.setCharacterToken(new InspecteurLestrade());
        assertFalse("Occupied plain cell cannot accept character", cell.canAcceptCharacter());

        cell = new Cell(coordinate, CellType.HOUSE);
        assertFalse("Obstacle cell cannot accept character", cell.canAcceptCharacter());
    }

    @Test
    public void canCharacterMoveThrough() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOUSE);
        assertFalse("Character cannot move through obstacle cell", cell.canCharacterMoveThrough());

        cell = new Cell(coordinate, CellType.LAMP);
        assertFalse("Character cannot move through obstacle cell", cell.canCharacterMoveThrough());

        cell = new Cell(coordinate, CellType.HOLE);
        assertTrue("Character can move through hole cell", cell.canCharacterMoveThrough());
    }

    @Test
    //we use actual board cells for this since the logic will look at other board cells
    public void getNeighbors() {
        //this cell is the top left exit, it neighbors 6 cells: 2 houses, 1 lamp, 1 hole, 2 extra houses that just block the path
        cell = Board.getCell(2, 1);
        Set<CellCoordinate> neighbors = cell.getNeighbors(false);
        assertEquals("Neighbors of corner cell without going through obstacles are 1", 1, neighbors.size());

        Cell neighbor = Board.getCell((CellCoordinate)neighbors.toArray()[0]);
        assertEquals("Neighbor is hole", CellType.HOLE, neighbor.getCellType());
        CellCoordinate neigbhorCoordinates = new CellCoordinate(4,1);
        assertEquals("Neighbor coordinates are correct", neigbhorCoordinates, neighbor.getCellCoordinate());

        neighbors = cell.getNeighbors(true);
        assertEquals("Neighbors of corner cell going through obstacles are 6", 6, neighbors.size());

        //this cell is the only cell in its row, it neighbors 3 cells: 1 lamp, 2 plain
        cell = Board.getCell(0, 5);
        neighbors = cell.getNeighbors(false);
        assertEquals("Neighbors of only top row cell without going through obstacles are 2", 2, neighbors.size());

        neighbors = cell.getNeighbors(true);
        assertEquals("Neighbors of only top row cell going through obstacles are 3", 3, neighbors.size());
    }

    @Test
    public void getConnectedHoles() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOUSE);
        assertTrue("Non hole cell has no connected holes", cell.getConnectedHoles().isEmpty());

        //this is a real open hole. We need it because the logic checks other hole cell status on the board
        cell = Board.getCell(new CellCoordinate(0,5));
        assertEquals("Open hole cell has 6 connected holes", 6, cell.getConnectedHoles().size());

        cell = new Cell(coordinate, CellType.HOLE);
        assertTrue("Closed hole cell has no connected holes", cell.getConnectedHoles().isEmpty());
    }

    @Test
    public void flipExit() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.EXIT);
        assertFalse("Exit before flip is closed", cell.isExitOpen());
        cell.flipExit();
        assertTrue("Exit after flip is closed", cell.isExitOpen());
        cell.flipExit();
        assertFalse("Exit after second flip is closed", cell.isExitOpen());

        cell = new Cell(coordinate, CellType.EXIT_HOLE);
        assertFalse("ExitHole before flip is closed", cell.isExitOpen());
        cell.flipExit();
        assertTrue("ExitHole after flip is closed", cell.isExitOpen());
        cell.flipExit();
        assertFalse("ExitHole after second flip is closed", cell.isExitOpen());
    }

    @Test(expected = IllegalStateException.class)
    public void flipExitThrowsExceptionWhenTypeIsWrong() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOUSE);
        cell.flipExit();
    }

    @Test
    public void flipHole() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOLE);
        assertFalse("Hole before flip is closed", cell.isHoleOpen());
        cell.flipHole();
        assertTrue("Hole after flip is closed", cell.isHoleOpen());
        cell.flipHole();
        assertFalse("Hole after second flip is closed", cell.isHoleOpen());

        cell = new Cell(coordinate, CellType.EXIT_HOLE);
        assertFalse("ExitHole before flip is closed", cell.isHoleOpen());
        cell.flipHole();
        assertTrue("ExitHole after flip is closed", cell.isHoleOpen());
        cell.flipHole();
        assertFalse("ExitHole after second flip is closed", cell.isHoleOpen());
    }

    @Test(expected = IllegalStateException.class)
    public void flipHoleThrowsExceptionWhenTypeIsWrong() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOUSE);
        cell.flipHole();
    }

    @Test
    public void flipLamp() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.LAMP);
        assertFalse("Lamp before flip is OFF", cell.isLampOn());
        cell.flipLamp();
        assertTrue("Lamp after flip is ON", cell.isLampOn());
        cell.flipLamp();
        assertFalse("Lamp after second flip is OFF", cell.isLampOn());
    }

    @Test(expected = IllegalStateException.class)
    public void flipLampThrowsExceptionWhenTypeIsWrong() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOUSE);
        cell.flipLamp();
    }

    @Test
    public void setLampOffTurn() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.LAMP);
        assertEquals("Lamp initial lamp off turn is -1", -1, cell.getlampOffTurn());
        cell.setLampOffTurn(1);
        assertEquals("Lamp off turn is 1", 1, cell.getlampOffTurn());
    }

    @Test(expected = IllegalStateException.class)
    public void setLampOffTurnThrowsExceptionWhenTypeIsWrong() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOUSE);
        cell.setLampOffTurn(1);
    }

    @Test
    //we use a real cell on the board since character swap logic uses other cell info
    public void setCharacterToken() {
        CharacterToken characterToken = new InspecteurLestrade();

        cell = Board.getCell(4, 2);//this is a plain cell with no character in it
        assertNull("dest cell is empty initially", cell.getCharacterToken());

        cell.setCharacterToken(characterToken);
        assertEquals("dest cell contains character", characterToken, cell.getCharacterToken());
        assertEquals("character has dest cell coordinates", cell.getCellCoordinate(), characterToken.getCurrentPosition());
    }

    @Test(expected = IllegalStateException.class)
    public void setCharacterTokenThrowsExceptionWhenTypeIsWrong() {
        coordinate = new CellCoordinate(2, 2);
        cell = new Cell(coordinate, CellType.HOUSE);
        cell.setCharacterToken(new InspecteurLestrade());
    }

    @Ignore
    @Test
    //ignored since a human must look at it for imperfections
    //can be verified by looking at Board.printBoard for all cases
    public void printCellContent() {
    }

    @Ignore
    @Test
    //ignored since a human must look at it for imperfections
    //can be verified by looking at Board.printBoard for all cases
    public void printCellCharacter() {
    }
}
