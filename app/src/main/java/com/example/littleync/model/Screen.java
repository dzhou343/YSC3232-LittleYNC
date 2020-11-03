package com.example.littleync.model;

import android.widget.TextView;

import java.util.Locale;

public class Screen {
    private final Shop SHOP = new Shop();
    private final Monsters MONSTERS = new Monsters();
    private final User user;

    public Screen(User user) {
        this.user = user;
    }

    public void refreshUserAttributes(TextView woodDisplay, TextView woodchoppingGearLevelDisplay, TextView fishDisplay, TextView fishingGearLevelDisplay, TextView goldDisplay, TextView combatGearLevelDisplay, TextView aggLevelDisplay, TextView aggLevelProgressDisplay) {
        String woodRes = String.format(Locale.getDefault(), "Wood: %s", user.getWood());
        woodDisplay.setText(woodRes);
        String woodGearLevel = String.format(Locale.getDefault(), "Wood Gear Level: %s", user.getWoodchoppingGearLevel());
        woodchoppingGearLevelDisplay.setText(woodGearLevel);
        String fishRes = String.format(Locale.getDefault(), "Fish: %s", user.getFish());
        fishDisplay.setText(fishRes);
        String fishGearLevel = String.format(Locale.getDefault(), "Fish Gear Level: %s", user.getFishingGearLevel());
        fishingGearLevelDisplay.setText(fishGearLevel);
        String goldRes = String.format(Locale.getDefault(), "Gold: %s", user.getGold());
        goldDisplay.setText(goldRes);
        String combatGearLevel = String.format(Locale.getDefault(), "Combat Gear Level: %s", user.getCombatGearLevel());
        combatGearLevelDisplay.setText(combatGearLevel);
        String aggLevel = String.format(Locale.getDefault(), "Aggregate Level: %s", user.getAggregateLevel());
        aggLevelDisplay.setText(aggLevel);
        String aggLevelProgress = String.format(Locale.getDefault(),
                "%s / %s", user.getExp(), user.requiredExperience(user.getAggregateLevel() + 1));
        aggLevelProgressDisplay.setText(aggLevelProgress);
    }

    public void refreshSetWoodUpgrade(TextView woodToLevel, TextView woodCost) {
        int currentLevel = user.getWoodchoppingGearLevel();
        String toLevel = String.format(Locale.getDefault(), "%s -> %s", currentLevel, currentLevel + 1);
        woodToLevel.setText(toLevel);
        String cost = String.format(Locale.getDefault(), "Cost: %s Fish, %s Gold",
                SHOP.requiredPrimaryResource(currentLevel), SHOP.requiredSecondaryResource(currentLevel));
        woodCost.setText(cost);
    }

    public void refreshSetFishUpgrade(TextView fishToLevel, TextView fishCost) {
        int currentLevel = user.getFishingGearLevel();
        String toLevel = String.format(Locale.getDefault(), "%s -> %s", currentLevel, currentLevel + 1);
        fishToLevel.setText(toLevel);
        String cost = String.format(Locale.getDefault(), "Cost: %s Wood, %s Gold",
                SHOP.requiredPrimaryResource(currentLevel), SHOP.requiredSecondaryResource(currentLevel));
        fishCost.setText(cost);
    }

    public void refreshSetCombatUpgrade(TextView combatToLevel, TextView goldCost) {
        int currentLevel = user.getCombatGearLevel();
        String toLevel = String.format(Locale.getDefault(), "%s -> %s", currentLevel, currentLevel + 1);
        combatToLevel.setText(toLevel);
        String cost = String.format(Locale.getDefault(), "Cost: %s Fish, %s Wood",
                SHOP.requiredPrimaryResource(currentLevel), SHOP.requiredPrimaryResource(currentLevel));
        goldCost.setText(cost);
    }

}
