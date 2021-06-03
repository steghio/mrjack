package com.blogspot.groglogs.mrjack.structures.board;

import com.blogspot.groglogs.mrjack.input.Stdinputter;
import com.blogspot.groglogs.mrjack.structures.common.Pair;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the coordinates of a cell.
 */
public class CellCoordinate {
    private int row, column;

    public CellCoordinate(int row, int column){
        this.row = row;
        this.column = column;
    }

    public int getRow(){
        return this.row;
    }

    public int getColumn(){
        return this.column;
    }

    /**
     * Method used to prompt the user for a valid pair of coordinates.
     * Keeps asking for valid input until a proper set of coordinates can be retrieved.
     * @return the user input coordinates.
     */
    public static CellCoordinate askInputCoordinate(){
        int row = Stdinputter.getIntInput(0, Board.ROWS - 1, "Input row: ", "Invalid row");
        int column = Stdinputter.getIntInput(0, Board.COLUMNS - 1, "Input column: ", "Invalid column");
        return new CellCoordinate(row,column);
    }

    /**
     * Calculates the distance between start and end cell without crossing obstacles and using holes.
     * The calculated distance is the shortest walking path between the two cells.
     * @param start start cell.
     * @param end end cell.
     * @return the distance between the two cells or -1 if no path exists.
     */
    public static int distance(final CellCoordinate start, final CellCoordinate end){
        return CellCoordinate.distance(start, end, false, false);
    }

    /**
     * Calculates the distance between start and end cell.
     * The calculated distance is the shortest walking path between the two cells.
     * @param start start cell.
     * @param end end cell.
     * @param useHoles if true, holes can be used to quick travel.
     * @param canGoThroughObstacles if true, the path can go through obstacles eg HOUSE and LAMP.
     * @return the distance between the two cells or -1 if no path exists.
     */
    public static int distance(final CellCoordinate start, final CellCoordinate end, boolean useHoles, boolean canGoThroughObstacles){
        Set<CellCoordinate> visited = new HashSet<>();
        Queue<Pair<CellCoordinate,Integer>> cellsToVisit = new ArrayDeque<>();

        Pair<CellCoordinate, Integer> init = new Pair<>(start, 0);

        cellsToVisit.add(init);

        while(!cellsToVisit.isEmpty()){
            Pair<CellCoordinate, Integer> currPosition = cellsToVisit.poll();

            //have we reached our destination?
            if(currPosition.getFirst().equals(end)){
                return currPosition.getSecond();
            }

            if(visited.contains(currPosition.getFirst())){
                continue;
            }

            visited.add(currPosition.getFirst());

            Cell cell = Board.getCell(currPosition.getFirst());

            //visit all neighbors next
            Set<CellCoordinate> neighbors = cell.getNeighbors(canGoThroughObstacles);

            for(CellCoordinate c : neighbors){
                if(!visited.contains(c)){
                    Pair<CellCoordinate, Integer> nextCoordinate = new Pair<>(c, currPosition.getSecond() + 1);
                    cellsToVisit.add(nextCoordinate);
                }
            }

            //if we are on an open hole, and can use it, visit all valid connected destinations too
            //going into a hole counts as a movement point
            if(useHoles) {
                Set<CellCoordinate> connectedHoles = cell.getConnectedHoles();
                for (CellCoordinate c : connectedHoles) {
                    if (!visited.contains(c)) {
                        Pair<CellCoordinate, Integer> nextCoordinate = new Pair<>(c, currPosition.getSecond() + 1);
                        cellsToVisit.add(nextCoordinate);
                    }
                }
            }
        }

        return -1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.row, this.column);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CellCoordinate)) return false;
        CellCoordinate other = (CellCoordinate)o;
        return this.row == other.row && this.column == other.column ;
    }

    @Override
    public String toString(){
        return "(" + this.row + "," + this.column + ")";
    }
}
