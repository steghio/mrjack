package com.blogspot.groglogs.mrjack.structures.enums;

/**
 * The possible types for a character ability.
 * AFTER_MOVE: can be used only after moving.
 * ANY_MOVE: can be used any time.
 * INSTEAD_OF_MOVE: can be used if character has not moved and prevents moving in this turn.
 * ALWAYS: is always active.
 */
public enum CharacterAbilityType {
    AFTER_MOVE,
    ANY_MOVE,
    INSTEAD_OF_MOVE,
    ALWAYS
}
