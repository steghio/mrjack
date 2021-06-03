package com.blogspot.groglogs.mrjack.structures.board;

import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.characters.MissStealthy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CellCoordinateTests {

    Board b;
    CellCoordinate start, end;

    @Before
    //since we track static state in the board, we need to reinitialize correctly for each test
    public void initBoard(){
        Board.clearBoard();
        b = new Board();
    }

    @Test
    public void askInputCoordinate(){
        //we pass out of range values, which the inputter needs to skip and prompt player for again
        //stops when a valid value is given, we expect 3,0
        String in = "-1\n3\n50\n0\n1\n";
        Stdinputter.setStringAsInputStream(in);
        CellCoordinate expected = new CellCoordinate(3, 0);
        CellCoordinate input = CellCoordinate.askInputCoordinate();
        assertEquals("Input coordinates are as expected skipping bad values", expected, input);
    }

    @Test
    public void distance(){
        //a real cell, first plain cell in the first column
        start = new CellCoordinate(5, 0);
        assertEquals("Distance no holes, yes obstacles, of a cell to itself is 0", 0, CellCoordinate.distance(start, start));

        //a real cell, surrounded by: N-plain, NE-plain, SE-house, S-plain, SW-house, NW-house
        start = new CellCoordinate(3, 6);
        for(CellCoordinate delta : Cell.getNearbyCells().values()) {
            end = new CellCoordinate(start.getRow() + delta.getRow(), start.getColumn() + delta.getColumn());
            assertEquals("Distance of a cell to immediate neighbor is 1", 1, CellCoordinate.distance(start, end, true, true));
        }

        //SW of our start, it's a house
        end = new CellCoordinate(4, 7);
        assertEquals("Distance to obstacle neighbor without going through obstacles is -1", -1, CellCoordinate.distance(start, end, false, false));

        //a real open hole cell
        start = new CellCoordinate(0, 5);
        //another real open hole cell
        end = new CellCoordinate(4, 1);
        assertEquals("Distance to hole cell without going through holes is 5", 5, CellCoordinate.distance(start, end, false, false));
        assertEquals("Distance to connected hole cell going through holes is 1", 1, CellCoordinate.distance(start, end, true, false));

        //a neighbor of another real open hole cell
        end = new CellCoordinate(5, 0);
        assertEquals("Distance to hole cell without going through holes is 6", 6, CellCoordinate.distance(start, end, false, false));
        assertEquals("Distance to connected hole cell going through holes is 2", 2, CellCoordinate.distance(start, end, true, false));

        //Miss Stealthy start position
        start = new MissStealthy().getCurrentPosition();
        //this end is just after 3 houses NW of MS start position
        end = new CellCoordinate(5, 4);
        assertEquals("Distance through houses without going through obstacles is 5", 5, CellCoordinate.distance(start, end, false, false));
        assertEquals("Distance through houses going through obstacles is 4", 4, CellCoordinate.distance(start, end, false, true));

        //a cell that does not exist, is unreachable
        end = new CellCoordinate(50, 50);
        assertEquals("Distance to unreachable dest is -1", -1, CellCoordinate.distance(start, end, false, false));
    }

}
