package com.example.littleync.gameActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.littleync.R;
import com.example.littleync.TravelActivity;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import static com.example.littleync.LoginActivity.loginStatus;
import static com.example.littleync.LoginActivity.logoutTrigger;

/**
 * Abstract class we implement for woodchopping, fishing, and combat. All three of these activities
 * are extremely similar, with the main difference being the action that gets performed
 */
public abstract class AttributesActivity extends AppCompatActivity {

    // To print to log instead of console
    protected final String TAG;

    // DB attributes
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private DocumentReference userDoc;
    private User initialUser;
    protected User user;
    protected volatile Boolean userLoaded = false;

    // To update User stats at top of page
    private TextView woodDisplay;
    private TextView woodchoppingGearLevelDisplay;
    private TextView fishDisplay;
    private TextView fishingGearLevelDisplay;
    private TextView goldDisplay;
    private TextView combatGearLevelDisplay;
    private TextView aggLevelDisplay;
    private TextView aggLevelProgressDisplay;

    /**
     * Sets the correct content view for the abstract class implementing, called in onCreate()
     */
    protected abstract void settingContentView();

    public AttributesActivity(String TAG) {
        this.TAG = TAG;
    }

    /**
     * Initialize the objects and TextViews required for this page, including stamina computations
     *
     * @param savedInstanceState pass info around
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingContentView();

        // User and relevant TextViews
        woodDisplay = findViewById(R.id.wood_res);
        woodchoppingGearLevelDisplay = findViewById(R.id.wood_gear_level);
        fishDisplay = findViewById(R.id.fish_res);
        fishingGearLevelDisplay = findViewById(R.id.fish_gear_level);
        goldDisplay = findViewById(R.id.gold_res);
        combatGearLevelDisplay = findViewById(R.id.combat_gear_level);
        aggLevelDisplay = findViewById(R.id.agg_level);
        aggLevelProgressDisplay = findViewById(R.id.agg_level_progress);

        // Load in the User from the DB
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
    protected void onDestroy() {
        user.writeToDatabase(fs, userDoc, initialUser);
        Log.d(TAG, "Wrote to DB");
        logoutTrigger = 0;
        super.onDestroy();
        // Checks that the loginStatus is indeed true, then if it is, start a new TravelActivity
        // Class, and clear all the redundant activities in the stack.
        if (loginStatus) {
            Intent intent = new Intent(this.getApplicationContext(), TravelActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * Read in User by userID, update all the textViews at top of page, and flags that the User has
     * been loaded in
     *
     * @param ds DocumentSnapshot of the User from the DB
     */
    private void readUser(Task<DocumentSnapshot> ds) {
        ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Store the initial values of the user
                initialUser = documentSnapshot.toObject(User.class);
                // Store the user that this page will manipulate
                user = documentSnapshot.toObject(User.class);
                userLoaded = true;
                // Assign User attributes to textViews
                refreshUserAttributes();
            }
        });
    }

    /**
     * Refreshes the TextViews that display the User attributes at the top of the page
     */
    protected void refreshUserAttributes() {
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

}