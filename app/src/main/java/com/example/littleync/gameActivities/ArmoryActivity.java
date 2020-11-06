package com.example.littleync.gameActivities;

import androidx.appcompat.app.AppCompatActivity;

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
public class ArmoryActivity extends AttributesActivity {

    // To update for next level stats
    private final Shop SHOP = new Shop();
    private TextView toastDisplay;
    private TextView woodToLevel;
    private TextView woodCost;
    private TextView fishToLevel;
    private TextView fishCost;
    private TextView combatToLevel;
    private TextView goldCost;

    /**
     * Sets the tag for the Log
     */
    public ArmoryActivity() {
        super("ArmoryActivity");
    }

    /**
     * Sets the correct content view
     */
    @Override
    protected void settingContentView() {
        setContentView(R.layout.armory_page);
    }

    /**
     * Initialize the objects and TextViews required for this page
     *
     * @param savedInstanceState pass info around
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toastDisplay = findViewById(R.id.armory_toast_msg);
        woodToLevel = findViewById(R.id.wood_upgrade_level);
        woodCost = findViewById(R.id.wood_upgrade_cost);
        fishToLevel = findViewById(R.id.fish_upgrade_level);
        fishCost = findViewById(R.id.fish_upgrade_cost);
        combatToLevel = findViewById(R.id.battle_upgrade_level);
        goldCost = findViewById(R.id.battle_upgrade_cost);
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
    @Override
    public void refreshUserAttributes() {
        super.refreshUserAttributes();
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
                refreshUserAttributes();
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
                refreshUserAttributes();
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
                refreshUserAttributes();
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