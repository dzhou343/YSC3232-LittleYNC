package com.example.littleync.model;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User object with attributes that match what is stored in the users collection of the DB
 */
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

    /**
     * Constructor for the User; only called in sign-up page when we need to initialize
     * a brand new user
     *
     * @param userName              this User's user name
     * @param woodchoppingGearLevel woodchopping gear level
     * @param fishingGearLevel      fishing gear level
     * @param combatGearLevel       combar gear level
     * @param aggregateLevel        an aggregate level to indicate how experienced this User is
     * @param wood                  wood resource
     * @param fish                  fish resource
     * @param gold                  gold resource
     * @param trades                list of trade document IDs, which correspond to "trades" collection in DB,
     *                              each User can have a max of 5 trades at any given time
     * @param exp                   this is used to calculate the aggregate level; required exp by level:
     *                              50 * level ^ 1.8
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
     * Empty constructor required to automatically parse User document from DB
     */
    public User() {}

    /**
     * Write the selected User object to DB immediately. This is used when we sign up and
     * when we update the seller's User in the DB when trading. We are able to immediately
     * write to DB without doing any intermediate updates since there is no chance for
     * intermediate User attributes to be affected, unlike below.
     *
     * @param userDoc the DocumentReference that points to the User of interest; note that
     *                this is not always the User that is currently logged in (e.g. while
     *                trading)
     */
    public void writeToDatabaseDirectly(final DocumentReference userDoc) {
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

    /**
     * Write User object to the DB. We need to also pass in the initial state of the user. This is
     * so that in the case when the User object is locally stored for some task, e.g. fishing or
     * woodchopping, if one of their active trades are accepted, then we need to reflect this
     * properly and not immediately overwrite this trade data. Thus, we need to calculate delta
     * change for our three resources (which are the ones that can be affected by trading), as well
     * as the User trade list. For the User trade list, we need to maintain a purely local
     * newTrades list, then combine that with the most recent read from the DB.
     *
     * @param userDoc     the DocumentReference object that connects this User object to the DB
     * @param initialUser a snapshot of the User when initially loaded in
     */
    public void writeToDatabase(FirebaseFirestore fs, final DocumentReference userDoc, final User initialUser) {
        String userID = FirebaseAuth.getInstance().getUid();
        assert userID != null;
        fs.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot documentSnapshot) {
                                              // See the values of the user before writing
                                              User finalUser = documentSnapshot.toObject(User.class);

                                              // Only these three attributes can be affected by trade
                                              assert finalUser != null;
                                              int deltaWood = finalUser.getWood() - initialUser.getWood();
                                              int deltaFish = finalUser.getFish() - initialUser.getFish();
                                              int deltaGold = finalUser.getGold() - initialUser.getGold();
                                              ArrayList<String> deltaTrades = finalUser.getTrades();

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
                                              docData.put("trades", deltaTrades);
                                              docData.put("exp", getExp());
                                              userDoc.set(docData);
                                          }
                                      }
                );
    }

    /**
     * Returns the amount of exp required to get to the next aggregate level; formula to
     * convert exp to aggregate level: 50 * level ^ 1.8
     *
     * @param level the current aggregate level
     * @return the amount of exp required for the next aggregate level
     */
    public int requiredExperience(int level) {
        return (int) (50 * Math.pow(level, 1.8));
    }

    /**
     * Compute the aggregate level from the amount of exp
     *
     * @return the current aggregate level
     */
    private int computeAggregateLevelIndex() {
        int index = getAggregateLevel();
        int exp = getExp();
        while (exp >= requiredExperience(index)) {
            index++;
        }
        return index;
    }

    /**
     * Simple check if the aggregate level needs to be updated
     *
     * @return true if level needs to be updated
     */
    private Boolean checkNextLevel() {
        return getExp() >= requiredExperience(getAggregateLevel());
    }

    /**
     * Add a specified amount of gold
     *
     * @param gold the amount to be added
     */
    public void addGold(int gold) {
        setGold(getGold() + gold);
    }

    /**
     * Add a specified amount of wood
     *
     * @param wood the amount to be added
     */
    public void addWood(int wood) {
        setWood(getWood() + wood);
    }

    /**
     * Add a specified amount of fish
     *
     * @param fish the amount to be added
     */
    public void addFish(int fish) {
        setFish(getFish() + fish);
    }

    /**
     * Add a specified amount of exp
     *
     * @param exp the amount to be added
     */
    public void addExp(int exp) {
        setExp(getExp() + exp);
    }

    /**
     * Called in the Cendana Forest page at every unit of stamina consumed; increments wood
     * and exp by the current woodchopping gear level
     */
    public void chopWood() {
        addWood(getWoodchoppingGearLevel());
        addExp(getWoodchoppingGearLevel());
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    /**
     * Called in the Ecopond page at every unit of stamina consumed; increments fish
     * and exp by the current fishing gear level
     */
    public void fishFish() {
        addFish(getFishingGearLevel());
        addExp(getFishingGearLevel());
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    /**
     * Adds a trade to the local User object, physically adds to both the current trades list and
     * the newTrades list; we need to ensure the local User object does not have more than 5
     * trades, but when we write to the DB, we need this separately maintained newTrades list
     *
     * @param documentID the documentID that corresponds to the document in the trades collection
     */
    public void addTrade(String documentID) {
        trades.add(documentID);
    }

    /**
     * Remove a trade from this User object
     *
     * @param documentID the documentID to remove
     */
    public void removeTrade(String documentID) {
        trades.remove(documentID);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // From here onwards are getters and setters; note that these are all required for //
    // Firestore API to automatically parse from DB into a local User object           //
    /////////////////////////////////////////////////////////////////////////////////////

    public void setUserName(String userName) {
        this.userName = userName;
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

}
