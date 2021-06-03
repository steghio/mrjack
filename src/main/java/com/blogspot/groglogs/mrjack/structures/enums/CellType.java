package com.blogspot.groglogs.mrjack.structures.enums;

/**
 * The possible types for a cell in the grid.
 * PLAIN: nothing is there, can accept characters.
 * HOUSE: obstacle, impassable and cannot accept characters.
 * LAMP: obstacle, impassable, cannot accept characters, has a lamp that can be ON or OFF.
 * HOLE: can accept characters, is connected to all other holes, can be OPEN or CLOSED.
 * EXIT: can accept characters, Jack must pass through here to escape, can be OPEN or BLOCKED.
 * EXIT_HOLE: behaves as both a HOLE and an EXIT
 */
//TODO could be enhanced to track info such as is this cell an obstacle or not
public enum CellType {
    PLAIN,
    HOUSE,
    LAMP,
    HOLE,
    EXIT,
    EXIT_HOLE
}
