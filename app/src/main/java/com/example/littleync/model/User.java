package com.example.littleync.model;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class User {
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

    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

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

    /**
     * Second Constructor for User Class
     * This is only called by the database
     */
    public User() {}

    public void writeToDatabase(final DocumentReference userDoc, final User initialUser) {
        String userID = FirebaseAuth.getInstance().getUid();
        fs.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot documentSnapshot) {
                                              // See the values of the user before writing
                                              User finalUser = documentSnapshot.toObject(User.class);

                                              // Only these three attributes can be affected by trade
                                              int deltaWood = finalUser.getWood() - initialUser.getWood();
                                              int deltaFish = finalUser.getFish() - initialUser.getFish();
                                              int deltaGold = finalUser.getGold() - initialUser.getGold();

                                              // Write to DB
                                              Map<String, Object> docData = new HashMap<>();
                                              docData.put("userName", getUserName());
                                              docData.put("woodchoppingGearLevel", getWoodchoppingGearLevel());
                                              docData.put("fishingGearLevel", getFishingGearLevel());
                                              docData.put("combatGearLevel", getCombatGearLevel());
                                              docData.put("aggregateLevel", getAggregateLevel());
                                              docData.put("wood", getWood() + deltaWood);
                                              docData.put("fish", getFish() + deltaFish);
                                              docData.put("gold", getGold() + deltaGold);
                                              docData.put("trades", getTrades());
                                              docData.put("exp", getExp());
                                              userDoc.set(docData);
                                          }
                                      }
                );
    }

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

    public void addTrade(String documentID) {
        trades.add(documentID);
    }

    public void removeTrade(String documentID) {
        trades.remove(documentID);
    }

    public void addExp(int exp) {
        setExp(getExp() + exp);
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
