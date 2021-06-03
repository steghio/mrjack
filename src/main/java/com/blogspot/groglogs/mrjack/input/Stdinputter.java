package com.blogspot.groglogs.mrjack.input;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Handles user input from System.IN.
 */
public class Stdinputter {

    private static Scanner s = new Scanner(System.in);

    /**
     * Used in testing, there is no actual need to offer this high level wrapping otherwise.
     * @param string the string to use as input stream for this scanner.
     */
    public static void setStringAsInputStream(String string){
        s = new Scanner(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Prompts the user for some integer input using askMessage.
     * If input is outside lowerBound and upperBound shows errorMessage and prompts user
     * again for input, otherwise returns the input integer.
     * @param lowerBound the lower bound for this integer input
     * @param upperBound the upper bound for this integer input
     * @param askMessage the message to prompt the user with
     * @param errorMessage the message to display when user inputs an invalid value
     * @return the integer representing the user input
     */
    public static int getIntInput(int lowerBound, int upperBound, String askMessage, String errorMessage){
        //no matter what is the lower bound, this won't be an available choice for sure
        int choice = lowerBound - 1;

        System.out.println();

        while(choice < lowerBound || choice > upperBound) {

            System.out.println(askMessage);

            try {
                choice = s.nextInt();

                if (choice < lowerBound || choice > upperBound) {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException | NoSuchElementException | IllegalStateException e) {
                System.out.println(errorMessage);
                //reset our marker to keep prompting the player
                choice = lowerBound - 1;
            }
            //always consume the newline character no matter if input was good or bad
            s.nextLine();
        }

        return choice;
    }
}
