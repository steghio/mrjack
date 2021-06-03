package com.blogspot.groglogs.mrjack.structures.deck;

import com.blogspot.groglogs.mrjack.structures.characters.*;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * Represents the full deck of 8 characters.
 */
public class CharacterDeck {

    public final static int NUM_CHARACTERS = 8;

    private CharacterToken[] characterCards;//use array format for Fisher-Yates shuffle
    private final Random gen;

    public CharacterDeck(){
        this.characterCards = new CharacterToken[NUM_CHARACTERS];
        this.characterCards[0] = new InspecteurLestrade();
        this.characterCards[1] = new JeremyBert();
        this.characterCards[2] = new JohnHWatson();
        this.characterCards[3] = new JohnSmith();
        this.characterCards[4] = new MissStealthy();
        this.characterCards[5] = new SergentGoodley();
        this.characterCards[6] = new SherlockHolmes();
        this.characterCards[7] = new SirWilliamGull();

        this.gen = new Random();
    }

    /**
     * Gets the CharacterToken at the given array position.
     * @param position the token position in the array.
     * @return the CharacterToken at the given array position.
     */
    public CharacterToken getCharacterTokenAtPosition(int position){
        return this.characterCards[position];
    }

    private void swap(int curr, int swapWith){
        CharacterToken tmp = this.characterCards[curr];
        this.characterCards[curr] = this.characterCards[swapWith];
        this.characterCards[swapWith] = tmp;
    }

    private void fisherYatesShuffle(){
        for(int curr = this.characterCards.length - 1; curr >= 0; curr--){
            int swapWith = gen.nextInt(curr + 1);

            if(curr != swapWith){
                swap(curr, swapWith);
            }
        }
    }

    /**
     * Shuffles the deck using Fisher-Yates logic and returns a queue of CharacterToken.
     * Tests are here: https://gist.github.com/steghio/ea6d32381f2e589fd3f09cfed07d65a7#file-fisheryatesjtests-java
     * @return the shuffled CharacterTokens.
     */
    public Queue<CharacterToken> shuffle(){
        this.fisherYatesShuffle();

        Queue<CharacterToken> q = new ArrayDeque<>();
        for(int i = 0; i < this.characterCards.length; i++){
            q.add(this.characterCards[i]);
        }

        return q;
    }
}
