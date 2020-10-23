package com.example.littleync.model;

import java.lang.Math;

public class Shop {

    // Formula for primary resource: 5 * level ^ 1.4
    private int requiredPrimaryResource(int level) {
        return (int)(5 * Math.pow(level, 1.4));
    }

    // Formula for secondary resource: 5 * level ^ 1.2
    private int requiredSecondaryResource(int level) {
        return (int)(5 * Math.pow(level, 1.2));
    }

    public Boolean increaseWoodchoppingLevel(User user) {
        int currentLevel = user.getWoodchoppingGearLevel();
        int currentFish = user.getFish();
        int currentGold = user.getGold();
        int requiredFish = requiredPrimaryResource(currentLevel);
        int requiredGold = requiredSecondaryResource(currentLevel);
        if (currentFish >= requiredFish && currentGold >= requiredGold) {
            user.setFish(currentFish - requiredFish);
            user.setGold(currentGold - requiredGold);
            user.setWoodchoppingGearLevel(currentLevel + 1);
            return true;
        } else {
            return false;
        }
    }

    public Boolean increaseFishingGearLevel(User user) {
        int currentLevel = user.getFishingGearLevel();
        int currentWood = user.getWood();
        int currentGold = user.getGold();
        int requiredWood = requiredPrimaryResource(currentLevel);
        int requiredGold = requiredSecondaryResource(currentLevel);
        if (currentWood >= requiredWood && currentGold >= requiredGold) {
            user.setWood(currentWood - requiredWood);
            user.setGold(currentGold - requiredGold);
            user.setFishingGearLevel(currentLevel + 1);
            return true;
        } else {
            return false;
        }
    }

    public Boolean increaseCombatGearLevel(User user) {
        int currentLevel = user.getCombatGearLevel();
        int currentFish = user.getFish();
        int currentWood = user.getWood();
        int requiredFish = requiredPrimaryResource(currentLevel);
        int requiredWood = requiredPrimaryResource(currentLevel);
        if (currentFish >= requiredFish && currentWood >= requiredWood) {
            user.setFish(currentFish - requiredFish);
            user.setWood(currentWood - requiredWood);
            user.setCombatGearLevel(currentLevel + 1);
            return true;
        } else {
            return false;
        }
    }

}
