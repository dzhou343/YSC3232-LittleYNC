package com.example.littleync.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    //private String databaseID = "random";

    private String userName;
    private int woodchoppingGearLevel = 1;
    private int fishingGearLevel = 1;
    private int combatGearLevel = 1;
    private int aggregateLevel = 1;
    private int wood = 0;
    private int fish = 0;
    private int gold = 0;
    private ArrayList<String> trades = new ArrayList<String>();
    private int exp = 0;
    private String UID;


    /**
     * Constructor which takes in elements
     *
     * @param userName
     * @param woodchoppingGearLevel
     * @param fishingGearLevel
     * @param combatGearLevel
     * @param aggregateLevel
     * @param wood
     * @param fish
     * @param gold
     * @param trades
     * @param exp
     */
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

    // Needed to automatically parse DB

    /**
     * Second Constructor for User Class
     * This is only called by the database
     */
    public User() {}

    // Formula for level: 50 * level ^ 1.8
    public int requiredExperience(int level) {
        return (int) (50 * Math.pow(level, 1.8));
    }

    private int computeAggregateLevelIndex() {
        int index = getAggregateLevel();
        int exp = getExp();
        while (exp >= requiredExperience(index)) {
            index++;
        }
        return index;
    }

    private Boolean checkNextLevel() {
        return getExp() >= requiredExperience(getAggregateLevel());
    }

    public void addGold(int gold) {
        setGold(getGold() + gold);
    }

    public void addWood(int wood) {
        setWood(getWood() + wood);
    }

    public void addFish(int fish) {
        setFish(getFish() + fish);
    }

    public void chopWood() {
        addWood(getWoodchoppingGearLevel());
        addExp(getWoodchoppingGearLevel());
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    public void fishFish() {
        addFish(getFishingGearLevel());
        addExp(getFishingGearLevel());
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    public void addTrade(String tradeID) {
        trades.add(tradeID);
    }

    public void addExp(int exp) {
        setExp(getExp() + exp);
    }

    public void writeToDatabase(DocumentReference userDoc) {
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
        userDoc.set(docData);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

//    public void setDatabaseID(String databaseID) {
//        this.databaseID = databaseID;
//    }

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

    public String getUserName() {
        return this.userName;
    }

    public String getUID() {
        return this.UID;
    }

    public int getWoodchoppingGearLevel() {
        return this.woodchoppingGearLevel;
    }

    public int getFishingGearLevel() {
        return this.fishingGearLevel;
    }

    public int getCombatGearLevel() {
        return this.combatGearLevel;
    }

    public int getAggregateLevel() {
        return this.aggregateLevel;
    }

    public int getWood() {
        return this.wood;
    }

    public int getFish() {
        return this.fish;
    }

    public int getGold() {
        return this.gold;
    }

    public ArrayList<String> getTrades() {
        return this.trades;
    }

    public int getExp() {
        return this.exp;
    }

}
