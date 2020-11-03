package com.example.littleync.model;

import org.junit.Assert;
import org.junit.Test;

public class MonstersTest {
    private final Monsters testMonsters = new Monsters();

    @Test
    public void testGetMonsterHitpoints() {
        Assert.assertEquals(1000000, testMonsters.getMonsterHitpoints("Prof. Bodin"));
        Assert.assertEquals(640, testMonsters.getMonsterHitpoints("Prof. Liu"));
        Assert.assertEquals(40, testMonsters.getMonsterHitpoints("Prof. Danvy"));
    }

    @Test
    public void testGetMonsterGoldYield() {
        Assert.assertEquals(10000, testMonsters.getMonsterGoldYield("Prof. Bodin"));
        Assert.assertEquals(2, testMonsters.getMonsterGoldYield("Prof. Stamps"));
        Assert.assertEquals(200, testMonsters.getMonsterGoldYield("Prof. Tolwinski"));
    }

    @Test
    public void testGetMonsterExpYield() {
        Assert.assertEquals(10000, testMonsters.getMonsterExpYield("Prof. Bodin"));
        Assert.assertEquals(128, testMonsters.getMonsterExpYield("Prof. De Iorio"));
        Assert.assertEquals(16, testMonsters.getMonsterExpYield("Prof. Cheung"));
    }

}
