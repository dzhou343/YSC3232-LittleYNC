package com.example.littleync.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ShopTest {
    private final Shop testShop = new Shop();
    private User testUser;

    @Test
    public void testRequiredPrimaryResource() {
        Assert.assertEquals(1914, testShop.requiredPrimaryResource(70));
        Assert.assertTrue(testShop.requiredPrimaryResource(69) <= 1914);
    }

    @Test
    public void testRequiredSecondaryResource() {
        Assert.assertEquals(818, testShop.requiredSecondaryResource(70));
        Assert.assertTrue(testShop.requiredSecondaryResource(69) <= 818);
    }

    @Test
    public void testIncreaseFishingGearLevel() {
        Assert.assertFalse(testShop.increaseFishingGearLevel(testUser));
        testUser.addGold(5000);
        testUser.addWood(5000);
        Assert.assertTrue(testShop.increaseFishingGearLevel(testUser));
        Assert.assertEquals(2, testUser.getFishingGearLevel());
        Assert.assertEquals(1, testUser.getWoodchoppingGearLevel());
    }

    @Test
    public void testIncreaseWoodchoppingLevel() {
        Assert.assertFalse(testShop.increaseWoodchoppingLevel(testUser));
        testUser.addGold(5000);
        testUser.addFish(50);
        Assert.assertTrue(testShop.increaseWoodchoppingLevel(testUser));
        Assert.assertEquals(2, testUser.getWoodchoppingGearLevel());
    }

    @Test
    public void testIncreaseCombatGearLevel() {
        Assert.assertFalse(testShop.increaseCombatGearLevel(testUser));
        testUser.addFish(5000);
        testUser.addWood(5000);
        Assert.assertTrue(testShop.increaseCombatGearLevel(testUser));
        Assert.assertTrue(testShop.increaseCombatGearLevel(testUser));
        Assert.assertEquals(3, testUser.getCombatGearLevel());
    }

    @Before
    public void setUp() throws Exception {
        testUser = new User("test", 1, 1, 1, 0, 0, 80000, 0, new ArrayList<String>(), 0);
    }

}
