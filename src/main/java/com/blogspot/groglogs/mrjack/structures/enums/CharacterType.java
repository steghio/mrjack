package com.blogspot.groglogs.mrjack.structures.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * All the characters are associated with a unique type.
 * We assign a numeric value to these actions in order to have an easier time prompting users for input.
 */
public enum CharacterType {
    LESTRADE(0),
    BERT(1),
    WATSON(2),
    SMITH(3),
    STEALTHY(4),
    GOODLEY(5),
    HOLMES(6),
    GULL(7);

    private int numVal;
    private static Map<Integer, CharacterType> map = new HashMap<>();

    static{
        for(CharacterType d : CharacterType.values()){
            map.put(d.numVal, d);
        }
    }

    CharacterType(int numVal){
        this.numVal = numVal;
    }

    public int getNumVal(){
        return this.numVal;
    }

    public static CharacterType fromNumVal(int numVal){
        return map.get(numVal);
    }
}
