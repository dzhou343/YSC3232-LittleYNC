package com.example.littleync.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String databaseID = "random";
    private final int[] levels = {
            // Formula: 50 * level ^ 1.8
            // 1 - 50
            0, 50, 174, 361, 606, 905, 1257, 1660, 2111, 2609,
            3154, 3745, 4380, 5059, 5780, 6545, 7351, 8199, 9087, 10016,
            10985, 11994, 13041, 14127, 15252, 16415, 17616, 18854, 20130, 21443,
            22792, 24178, 25600, 27057, 28551, 30081, 31645, 33245, 34880, 36549,
            38254, 39992, 41765, 43572, 45413, 47288, 49196, 51138, 53113, 55121,
            // 51 - 100
            57163, 59237, 61344, 63484, 65656, 67861, 70098, 72367, 74669, 77002,
            79367, 81764, 84192, 86652, 89144, 91667, 94221, 96806, 99422, 102070,
            104748, 107457, 110196, 112966, 115767, 118598, 121460, 124352, 127274, 130226,
            133208, 136220, 139262, 142334, 145436, 148567, 151728, 154918, 158138, 161388,
            164666, 167974, 171312, 174678, 178073, 181498, 184951, 188433, 191945, 195484,
            // 101 - 150
            199053, 202650, 206276, 209931, 213614, 217325, 221065, 224833, 228629, 232454,
            236307, 240188, 244097, 248033, 251998, 255991, 260012, 264061, 268137, 272241,
            276373, 280532, 284719, 288934, 293176, 297445, 301742, 306067, 310418, 314797,
            319203, 323637, 328097, 332585, 337099, 341641, 346210, 350806, 355428, 360078,
            364754, 369457, 374187, 378943, 383727, 388537, 393373, 398236, 403126, 408042,
            // 151 - 200
            412984, 417954, 422949, 427971, 433019, 438093, 443194, 448321, 453474, 458653,
            463858, 469090, 474347, 479631, 484941, 490276, 495637, 501025, 506438, 511877,
            517342, 522833, 528349, 533891, 539459, 545052, 550671, 556316, 561986, 567682,
            573403, 579150, 584922, 590720, 596543, 602391, 608265, 614164, 620089, 626038,
            632013, 638013, 644039, 650089, 656165, 662265, 668391, 674542, 680718, 1000000
    };

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

    public User() {}

    private int computeAggregateLevelIndex() {
        int index = 0;
        int exp = getExp();
        while (index < 200 && exp >= levels[index]) {
            index++;
        }
        return index;
    }

    private Boolean checkNextLevel() {
        return getExp() >= levels[aggregateLevel - 1];
    }

    public void addGold(int gold) {
        setGold(getGold() + gold);
        addExp(gold);
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    public void addWood() {
        setWood(getWood() + getWoodchoppingGearLevel());
        addExp(getWoodchoppingGearLevel());
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    public void addFish() {
        setFish(getFish() + getFishingGearLevel());
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

    public String getUserName() {
        return this.userName;
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
