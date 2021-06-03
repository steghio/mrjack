package com.blogspot.groglogs.mrjack.structures.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The possible character actions.
 * MOVE: move this character.
 * USE_ABILITY: use this character's ability.
 * We assign a numeric value to these actions in order to have an easier time prompting users for input.
 */
public enum CharacterActionType {
    MOVE(0),
    USE_ABILITY(1);

    private int numVal;
    private static Map<Integer, CharacterActionType> map = new HashMap<>();

    static{
        for(CharacterActionType d : CharacterActionType.values()){
            map.put(d.numVal, d);
        }
    }

    CharacterActionType(int numVal){
        this.numVal = numVal;
    }

    public int getNumVal(){
        return this.numVal;
    }

    public static CharacterActionType fromNumVal(int numVal){
        return map.get(numVal);
    }
}
