package com.blogspot.groglogs.mrjack.structures.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * All possible directions of Watson's lamp.
 * We assign a numeric value to these actions in order to have an easier time prompting users for input.
 */
public enum Direction {
    NORTH(0),
    NORTHEAST(1),
    SOUTHEAST(2),
    SOUTH(3),
    SOUTHWEST(4),
    NORTHWEST(5);

    private int numVal;
    private static Map<Integer, Direction> map = new HashMap<>();

    static{
        for(Direction d : Direction.values()){
            map.put(d.numVal, d);
        }
    }

    Direction(int numVal){
        this.numVal = numVal;
    }

    public int getNumVal(){
        return this.numVal;
    }

    public static Direction fromNumVal(int numVal){
        return map.get(numVal);
    }

    @Override
    public String toString() {
      switch(this) {
        case NORTH: return "N";
        case NORTHEAST: return "NE";
        case SOUTHEAST: return "SE";
        case SOUTH: return "S";
        case SOUTHWEST: return "SW";
        case NORTHWEST: return "NW";
        default: throw new IllegalArgumentException();
      }
    }
}
