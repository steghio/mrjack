package com.blogspot.groglogs.mrjack.structures.characters;

import org.junit.Test;

public class MissStealthyTests {

    @Test(expected = UnsupportedOperationException.class)
    public void useAbility() {
        MissStealthy ms = new MissStealthy();
        ms.useAbility();
    }
}
