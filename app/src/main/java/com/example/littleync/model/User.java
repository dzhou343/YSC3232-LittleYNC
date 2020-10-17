package com.example.littleync.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    String databaseID = "random";

    String userName;
    int woodchoppingGearLevel;
    int fishingGearLevel;
    int combatGearLevel;
    int aggregateLevel;
    int wood;
    int fish;
    int gold;
    ArrayList<String> trades;
    int exp;

    public User(String userName, int woodchoppingGearLevel, int fishingGearLevel,
                int combatGearLevel, int aggregateLevel, int wood, int fish, int gold,
                ArrayList<String> trades, int exp) {
        this.userName = userName;
        this.woodchoppingGearLevel = woodchoppingGearLevel;
        this.fishingGearLevel = fishingGearLevel;
        this.combatGearLevel = combatGearLevel;
        this.aggregateLevel = aggregateLevel;
        this.wood = wood;
        this.fish = fish;
        this.gold = gold;
        this.trades = trades;
        this.exp = exp;
    }

    public User() {

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public void setWoodchoppingGearLevel(int woodchoppingGearLevel) {
        this.woodchoppingGearLevel = woodchoppingGearLevel;
    }

    public void setFishingGearLevel(int fishingGearLevel) {
        this.fishingGearLevel = fishingGearLevel;
    }

    public void setCombatGearLevel(int combatGearLevel) {
        this.combatGearLevel = combatGearLevel;
    }

    public void setAggregateLevel(int aggregateLevel) {
        this.aggregateLevel = aggregateLevel;
    }

    public void setWood(int wood) {
        this.wood = wood;
    }

    public void setFish(int fish) {
        this.fish = fish;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setTrades(ArrayList<String> trades) {
        this.trades = trades;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void addTrade(String tradeID) {
        trades.add(tradeID);
    }

    public String getUserName() {
        return userName;
    }

    public int getWoodchoppingGearLevel() {
        return woodchoppingGearLevel;
    }

    public int getFishingGearLevel() {
        return fishingGearLevel;
    }

    public int getCombatGearLevel() {
        return combatGearLevel;
    }

    public int getAggregateLevel() {
        return aggregateLevel;
    }

    public int getWood() {
        return wood;
    }

    public int getFish() {
        return fish;
    }

    public int getGold() {
        return gold;
    }

    public ArrayList<String> getTrades() {
        return trades;
    }

    public int getExp() {
        return exp;
    }

    public void writeToDatabase(OnlineDatabase db) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("userName", getUserName());
        docData.put("woodchoppingGearLevel", getWoodchoppingGearLevel());
        docData.put("fishingGearLevel", getFishingGearLevel());
        docData.put("combatGearLevel", getCombatGearLevel());
        docData.put("aggregateLevel", getAggregateLevel());
        docData.put("wood", getWood());
        docData.put("fish", getFish());
        docData.put("gold", getGold());
        docData.put("trades", getTrades());
        docData.put("exp", getExp());

        db.userReadWrite().set(docData);
    }

}
