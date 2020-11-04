package com.example.littleync.model;

/**
 * Shop object that is created only for the Armory Activity, providing some useful methods for the
 * User to upgrade their gear
 */
public class Shop {

    /**
     * Formula for required amount of primary resource: 5 * level ^ 1.4
     *
     * @param level the current level of what is to be upgraded
     * @return the amount of resource required for the next level
     */
    public int requiredPrimaryResource(int level) {
        return (int) (5 * Math.pow(level, 1.4));
    }

    /**
     * Formula for required amount of secondary resource: 5 * level ^ 1.2
     *
     * @param level the current level of what is to be upgraded
     * @return the amount of resource required for the next level
     */
    public int requiredSecondaryResource(int level) {
        return (int) (5 * Math.pow(level, 1.2));
    }

    /**
     * Try to level up woodchopping gear, which requires fish as its primary resource and gold
     * as its secondary resource
     *
     * @param user the User object to upgrade
     * @return whether or not the User has enough resources to upgrade
     */
    public boolean increaseWoodchoppingLevel(User user) {
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

    /**
     * Try to level up fishing gear, which requires wood as its primary resource and gold
     * as its secondary resource
     *
     * @param user the User object to upgrade
     * @return whether or not the User has enough resources to upgrade
     */
    public boolean increaseFishingGearLevel(User user) {
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

    /**
     * Try to level up combat gear, which requires both wood and fish as a primary resource
     *
     * @param user the User object to upgrade
     * @return whether or not the User has enough resources to upgrade
     */
    public boolean increaseCombatGearLevel(User user) {
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
