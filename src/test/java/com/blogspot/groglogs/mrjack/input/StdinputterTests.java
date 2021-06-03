package com.blogspot.groglogs.mrjack.input;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StdinputterTests {

    @Test
    //keeps asking for input until a valid one is provided, then it stops
    public void wrongInputIsSkipped() {
        //we provide bad input 3 times then we have two subsequent valid inputs, we expect the first one
        String in = "2\n3\n2\n0\n1\n";
        Stdinputter.setStringAsInputStream(in);
        assertEquals("Wrong input is skipped", 0, Stdinputter.getIntInput(0, 1, "", ""));
    }

}
