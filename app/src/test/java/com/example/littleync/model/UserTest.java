package com.example.littleync.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class UserTest {
    private User testUser;
    // In case we need mock Firestore instances
    // FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);

    @Test
    public void testRequiredExperience() {
        Assert.assertEquals(104748, testUser.requiredExperience(70));
        Assert.assertTrue(testUser.requiredExperience(69) <= 104748);
    }

    @Test
    public void testAddGold() {
        Assert.assertEquals(0, testUser.getGold());
        testUser.addGold(5000);
        Assert.assertEquals(5000, testUser.getGold());
    }

    @Test
    public void testAddWood() {
        Assert.assertEquals(0, testUser.getWood());
        testUser.addWood(5000);
        testUser.addGold(5000); // Should do nothing
        Assert.assertEquals(5000, testUser.getWood());
    }

    @Test
    public void testAddFish() {
        Assert.assertEquals(80000, testUser.getFish());
        testUser.addWood(5000);
        testUser.addGold(5000); // Should do nothing
        testUser.addFish(1);
        Assert.assertEquals(80001, testUser.getFish());
    }

    @Test
    public void testChopWood() {
        testUser.chopWood();
        Assert.assertEquals(1, testUser.getWoodchoppingGearLevel());
        Assert.assertEquals(1, testUser.getWood());
    }

    @Test
    public void testFishFish() {
        testUser.fishFish();
        Assert.assertEquals(1, testUser.getFishingGearLevel());
        Assert.assertEquals(80001, testUser.getFish());
    }

    @Test
    public void testAddTrade() {
        testUser.addTrade("testTrade");
        Assert.assertTrue(testUser.getTrades().contains("testTrade"));
    }

    @Test
    public void testAddExp() {
        testUser.addExp(500);
        Assert.assertEquals(500, testUser.getExp());
    }

    @Before
    public void setUp() throws Exception {
        testUser = new User("test", 1, 1, 1, 0, 0, 80000, 0, new ArrayList<String>(), 0);
    }

}