package com.example.littleync.actionActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.littleync.R;
import com.example.littleync.model.Shop;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import static com.example.littleync.MainActivity.loginStatus;
import static com.example.littleync.MainActivity.logoutTrigger;

/**
 * Armory Activity page which allows the user to upgrade their woodchopping, fishing, and/or combat
 * gear
 */
public class ArmoryActivity extends AppCompatActivity {
    // To print to log instead of console
    private final static String TAG = "ArmoryActivity";

    // DB attributes
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private DocumentReference userDoc;
    private User user;
    private User initialUser;
    private volatile Boolean userLoaded = false;

    // To update User stats at top of page
    private TextView woodDisplay;
    private TextView woodchoppingGearLevelDisplay;
    private TextView fishDisplay;
    private TextView fishingGearLevelDisplay;
    private TextView goldDisplay;
    private TextView combatGearLevelDisplay;
    private TextView aggLevelDisplay;
    private TextView aggLevelProgressDisplay;
    private TextView toastDisplay;

    // To update for next level stats
    private final Shop SHOP = new Shop();
    private TextView woodToLevel;
    private TextView woodCost;
    private TextView fishToLevel;
    private TextView fishCost;
    private TextView combatToLevel;
    private TextView goldCost;

    /**
     * Initialize the objects and TextViews required for this page
     *
     * @param savedInstanceState pass info around
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armory_page);

        // User and relevant TextViews
        woodDisplay = findViewById(R.id.wood_res);
        woodchoppingGearLevelDisplay = findViewById(R.id.wood_gear_level);
        fishDisplay = findViewById(R.id.fish_res);
        fishingGearLevelDisplay = findViewById(R.id.fish_gear_level);
        goldDisplay = findViewById(R.id.gold_res);
        combatGearLevelDisplay = findViewById(R.id.combat_gear_level);
        aggLevelDisplay = findViewById(R.id.agg_level);
        aggLevelProgressDisplay = findViewById(R.id.agg_level_progress);
        toastDisplay = findViewById(R.id.username2);

        woodToLevel = findViewById(R.id.wood_upgrade_level);
        woodCost = findViewById(R.id.wood_upgrade_cost);
        fishToLevel = findViewById(R.id.fish_upgrade_level);
        fishCost = findViewById(R.id.fish_upgrade_cost);
        combatToLevel = findViewById(R.id.battle_upgrade_level);
        goldCost = findViewById(R.id.battle_upgrade_cost);

        String userID = FirebaseAuth.getInstance().getUid();
        userLoaded = false;
        assert userID != null;
        userDoc = fs.collection("users").document(userID);
        readUser(userDoc.get());
    }

    /**
     * Write the local User and any updates made to it back to the DB; this is called when we press
     * the back button to return to the Main Activity
     */
    @Override
    public void onDestroy() {
        user.writeToDatabase(fs, userDoc, initialUser);
        Log.d(TAG, "Wrote to DB");
        logoutTrigger = 0;
        super.onDestroy();

    }

    /**
     * Read in User by userID, update all the textViews at top of page, flags that the User has
     * been loaded in
     *
     * @param ds DocumentSnapshot of the User from the DB
     */
    public void readUser(Task<DocumentSnapshot> ds) {
        ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        // Store the initial values of the user
                                        initialUser = documentSnapshot.toObject(User.class);
                                        // Store the user that this page will manipulate
                                        user = documentSnapshot.toObject(User.class);
                                        userLoaded = true;
                                        refreshScreen();
                                        String msg = "Upgrade your gear!";
                                        toastDisplay.setText(msg);
                                    }
                                }
        );
    }

    /**
     * To set the TextView displays relevant to upgrading the upgrading the woodchopping gear
     */
    public void setWoodUpgrade() {
        int currentLevel = user.getWoodchoppingGearLevel();
        String toLevel = String.format(Locale.getDefault(), "%s -> %s", currentLevel, currentLevel + 1);
        woodToLevel.setText(toLevel);
        String cost = String.format(Locale.getDefault(), "Cost: %s Fish, %s Gold",
                SHOP.requiredPrimaryResource(currentLevel), SHOP.requiredSecondaryResource(currentLevel));
        woodCost.setText(cost);
    }

    /**
     * To set the TextView displays relevant to upgrading the upgrading the fishing gear
     */
    public void setFishUpgrade() {
        int currentLevel = user.getFishingGearLevel();
        String toLevel = String.format(Locale.getDefault(), "%s -> %s", currentLevel, currentLevel + 1);
        fishToLevel.setText(toLevel);
        String cost = String.format(Locale.getDefault(), "Cost: %s Wood, %s Gold",
                SHOP.requiredPrimaryResource(currentLevel), SHOP.requiredSecondaryResource(currentLevel));
        fishCost.setText(cost);
    }

    /**
     * To set the TextView displays relevant to upgrading the upgrading the combat gear
     */
    public void setCombatUpgrade() {
        int currentLevel = user.getCombatGearLevel();
        String toLevel = String.format(Locale.getDefault(), "%s -> %s", currentLevel, currentLevel + 1);
        combatToLevel.setText(toLevel);
        String cost = String.format(Locale.getDefault(), "Cost: %s Fish, %s Wood",
                SHOP.requiredPrimaryResource(currentLevel), SHOP.requiredPrimaryResource(currentLevel));
        goldCost.setText(cost);
    }

    /**
     * To refresh the TextView displays on the entire page, called each time the user presses a
     * button; this called in readUser(), so User is definitely already loaded in
     */
    public void refreshScreen() {
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
        setWoodUpgrade();
        setFishUpgrade();
        setCombatUpgrade();
    }

    /**
     * Method run when the button which upgrades the woodchopping gear is pressed, calls
     * SHOP.increaseWoodchoppingLevel() to try and complete upgrade; there is also the check that
     * * the User has actually loaded in (since it is loaded in asynchronously)
     *
     * @param view for Android
     */
    public void upgradeWood(View view) {
        if (userLoaded) {
            if (SHOP.increaseWoodchoppingLevel(user)) {
                refreshScreen();
                String msg = "Successfully upgraded woodchopping gear level!";
                toastDisplay.setText(msg);
            } else {
                String msg = "Unable to upgrade.";
                toastDisplay.setText(msg);
            }
        } else {
            Log.d(TAG, "User not yet loaded");
        }
    }

    /**
     * Method run when the button which upgrades the fishing gear is pressed, calls
     * SHOP.increaseFishingGearLevel() to try and complete upgrade; there is also the check that
     * * the User has actually loaded in (since it is loaded in asynchronously)
     *
     * @param view for Android
     */
    public void upgradeFish(View view) {
        if (userLoaded) {
            if (SHOP.increaseFishingGearLevel(user)) {
                refreshScreen();
                String msg = "Successfully upgraded fishing gear level!";
                toastDisplay.setText(msg);
            } else {
                String msg = "Unable to upgrade.";
                toastDisplay.setText(msg);
            }
        } else {
            Log.d(TAG, "User not yet loaded");
        }
    }

    /**
     * Method run when the button which upgrades the combat gear is pressed, calls
     * SHOP.increaseCombatGearLevel() to try and complete upgrade; there is also the check that
     * * the User has actually loaded in (since it is loaded in asynchronously)
     *
     * @param view for Android
     */
    public void upgradeCombat(View view) {
        if (userLoaded) {
            if (SHOP.increaseCombatGearLevel(user)) {
                String msg = "Successfully upgraded combat gear level!";
                toastDisplay.setText(msg);
            } else {
                String msg = "Unable to upgrade.";
                toastDisplay.setText(msg);
            }
        } else {
            Log.d(TAG, "User not yet loaded");
        }
    }

}