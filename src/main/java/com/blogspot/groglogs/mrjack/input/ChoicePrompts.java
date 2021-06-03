package com.blogspot.groglogs.mrjack.input;

import com.blogspot.groglogs.mrjack.Game;
import com.blogspot.groglogs.mrjack.structures.characters.CharacterToken;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterActionType;
import com.blogspot.groglogs.mrjack.structures.enums.CharacterType;
import com.blogspot.groglogs.mrjack.structures.enums.Direction;
import com.blogspot.groglogs.mrjack.structures.enums.PlayerActionType;

import java.util.Map;

/**
 * Contains the logic necessary to issue player prompts.
 */
public class ChoicePrompts {

    /**
     * Present possible actions to the player and accept valid inputs only.
     * Only the inspector can attempt to accuse JACK.
     * @return the text to present to the player describing the currently available player actions to choose from.
     */
    public static String promptPlayerAction(){
        StringBuffer sb = new StringBuffer();

        if(Game.getCurrentPlayer() == Game.PLAYER_JACK){
            sb.append("JACK ");
        } else {
            sb.append("INSPECTOR ");
        }

        sb.append("choose an action:\n");

        for(PlayerActionType action : PlayerActionType.values()){
            //only the inspector can attempt to accuse JACK
            if(action == PlayerActionType.ACCUSE_JACK && Game.getCurrentPlayer() != Game.PLAYER_INSPECTOR){
                continue;
            }
            sb.append(String.format("%d: %s\n", action.getNumVal(), action));
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * Present possible character actions for the chosen character to the player.
     * @param c the chosen character.
     * @return the text to present to the player describing the currently available character actions to choose from.
     */
    public static String promptCharacterAction(final CharacterToken c){
        StringBuffer sb = new StringBuffer();

        sb.append("Choose a character action:\n");

        if(c.canMove() && c.getAvailableActions().contains(CharacterActionType.MOVE)){
            sb.append("0: Move\n");
        }

        if(c.canUseAbility() && c.getAvailableActions().contains(CharacterActionType.USE_ABILITY)){
            sb.append("1: Use ability\n");
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * Present possible characters to choose from excluding the given character to the player.
     * @param exclude the character to exclude from the available choices.
     * @param allowCancel true if the player should be presented with the choice to cancel the action.
     * @return the text to present to the player describing the currently available characters to choose from.
     */
    public static String promptCharacterChoice(CharacterType exclude, boolean allowCancel){
        StringBuffer sb = new StringBuffer();

        sb.append("Choose a character:\n");

        for(CharacterType c : CharacterType.values()){
            if(c != exclude){
                sb.append(c.getNumVal());
                sb.append(": ");
                sb.append(Game.getCharacterToken(c).getName());
                sb.append("\n");
            }
        }

        if(allowCancel){
            sb.append("-1: cancel\n");
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * Given the current batch of character choices, present them to the player and accept valid inputs only.
     * @param currCharacterChoices the currently available characters to choose from, each turn is two batches of four.
     * @param isPrompt true if we are asking for input, false if we are only displaying the choices.
     * @return the text to present to the player describing the currently available characters to choose from.
     */
    public static String promptBatchCharacterChoice(final Map<Integer, CharacterToken> currCharacterChoices, boolean isPrompt){
        StringBuffer sb = new StringBuffer();

        if(isPrompt) {
            if (Game.getCurrentPlayer() == Game.PLAYER_JACK) {
                sb.append("JACK ");
            } else {
                sb.append("INSPECTOR ");
            }

            sb.append("choose a character to play:\n");
        }
        else {
            sb.append("Available characters to play:\n");
        }

        for(Map.Entry<Integer, CharacterToken> e : currCharacterChoices.entrySet()){
            sb.append(String.format("%d: %s\n", e.getKey(), e.getValue().getName()));
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * Present possible directions for WATSON's lamp to choose from to the player.
     * @return the text to present to the player describing the currently available directions for WATSON's lamp to choose from.
     */
    public static String promptLampDirection(){
        StringBuffer sb = new StringBuffer();

        sb.append("Choose a lamp direction:\n");

        for(Direction d : Direction.values()){
            sb.append(String.format("%d: %s\n", d.getNumVal(), d));
        }

        sb.append("\n");

        return sb.toString();
    }
}
