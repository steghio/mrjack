package com.blogspot.groglogs.mrjack.structures.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the possible player actions.
 * PLAY_CHARACTER: choose a character from the available batch and play it.
 * VIEW_INNOCENTS: consult, if any, the list of revealed innocents through HOLMES.
 * VIEW_JACK: if player is JACK, view which character is JACK.
 * ESCAPE_JACK: cannot be chosen since it is automatically calculated each turn and executed if possible.
 * VIEW_GAME_STATUS: shows the current game status: turn, JACK's visibility, characters visibility.
 * VIEW_GAME_BOARD: shows the game board.
 * //leave this next option always last so we have easier handling of available actions for each player
 * ACCUSE_JACK: the player chooses a character and attempts to accuse another of being JACK. If no valid choice can be made
 * the player can continue.
 * We assign a numeric value to these actions in order to have an easier time prompting users for input.
 */
public enum PlayerActionType {
    PLAY_CHARACTER(0),
    VIEW_INNOCENTS(1),
    VIEW_JACK(2),
    VIEW_GAME_STATUS(3),
    VIEW_GAME_BOARD(4),
    //leave as last option everytime so we have easier handling of available actions for each player
    ACCUSE_JACK(5);

    private int numVal;
    private static Map<Integer, PlayerActionType> map = new HashMap<>();

    static{
        for(PlayerActionType d : PlayerActionType.values()){
            map.put(d.numVal, d);
        }
    }

    PlayerActionType(int numVal){
        this.numVal = numVal;
    }

    public int getNumVal(){
        return this.numVal;
    }

    public static PlayerActionType fromNumVal(int numVal){
        return map.get(numVal);
    }
}
