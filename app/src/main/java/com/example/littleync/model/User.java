package com.example.littleync.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private final String COLLECTION = "users";
    private String databaseID = "random";

    private String userName;
    private int woodchoppingGearLevel;
    private int fishingGearLevel;
    private int combatGearLevel;
    private int aggregateLevel;
    private int wood;
    private int fish;
    private int gold;
    private ArrayList<String> trades;
    private int exp;

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
        docData.put("fishGearLevel", getFishingGearLevel());
        docData.put("combatGearLevel", getCombatGearLevel());
        docData.put("aggregateLevel", getAggregateLevel());
        docData.put("wood", getWood());
        docData.put("fish", getFish());
        docData.put("gold", getGold());
        docData.put("trades", getTrades());
        docData.put("exp", getExp());

        db.userWrite(databaseID, COLLECTION).set(docData);

//        db.userWrite(userID, collection).update("userName", getUserName());
//        db.userWrite(userID, collection).update("woodchoppingGearLevel", getWoodchoppingGearLevel());
//        db.userWrite(userID, collection).update("fishingGearLevel", getFishingGearLevel());
//        db.userWrite(userID, collection).update("combatGearLevel", getCombatGearLevel());
//        db.userWrite(userID, collection).update("aggregateLevel", getAggregateLevel());
//        db.userWrite(userID, collection).update("wood", getWood());
//        db.userWrite(userID, collection).update("fish", getFish());
//        db.userWrite(userID, collection).update("gold", getGold());
//        db.userWrite(userID, collection).update("trades", getTrades());
//        db.userWrite(userID, collection).update("exp", getExp());
    }

}
