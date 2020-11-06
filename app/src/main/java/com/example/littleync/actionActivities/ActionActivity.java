package com.example.littleync.actionActivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

import static com.example.littleync.MainActivity.loginStatus;
import static com.example.littleync.MainActivity.logoutTrigger;

/**
 * Abstract class we implement for woodchopping, fishing, and combat. All three of these activites
 * are extremely similar, with the main difference being the action that gets performed
 */
public abstract class ActionActivity extends AppCompatActivity {
    // To print to log instead of console
    private final String TAG;

    // DB attributes
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private DocumentReference userDoc;
    protected User user;
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
    protected TextView gainDisplay;

    // Timer attributes
    // Time (in milliseconds) taken to deplete one unit of stamina = 5s
    private static final long TIME_PER_STAMINA = 5000;
    private static final int TOTAL_STAMINA = 50;
    private static final long TOTAL_TIME_PER_SESSION = TIME_PER_STAMINA * TOTAL_STAMINA;
    private boolean timerRunning;
    private long timeLeft = TOTAL_TIME_PER_SESSION;
    private ImageButton startPauseResumeBtn;
    private ImageButton resetBtn;
    private CountDownTimer myTimer;
    private TextView timeDisplay;
    private TextView staminaDisplay;

    /**
     * Constructor for this abstract class which just sets the Log print for us to debug
     *
     * @param TAG name of the class which implements this
     */
    public ActionActivity(String TAG) {
        this.TAG = TAG;
    }

    /**
     * Sets the correct content view for the abstract class implementing, called in onCreate()
     */
    protected abstract void settingContentView();

    /**
     * Create spinners in onCreate, called in onCreate(); only used for SagaBattleGroundActivity
     * @see SagaBattlegroundActivity
     */
    protected abstract void createSpinners();

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
        gainDisplay = findViewById(R.id.gain_display);

        // Load in the User from the DB
        String userID = FirebaseAuth.getInstance().getUid();
        userLoaded = false;
        assert userID != null;
        userDoc = fs.collection("users").document(userID);
        readUser(userDoc.get());

        // Create spinners if necessary
        createSpinners();

        // Timer stuff
        // By default, initialize stamina to full when the activity is created
        // Initialize 1) start/pause/resume button, 2) the reset button and 3) the dynamic time
        // display TextView. By default, the reset button is initialized to invisible.
        staminaDisplay = findViewById(R.id.stamina_section);
        startPauseResumeBtn = findViewById(R.id.start_pause_resume_button);
        resetBtn = findViewById(R.id.reset_button);
        timeDisplay = findViewById(R.id.time_left);

        // Set the onClickListeners for the two buttons
        startPauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountdownText();
        updateStamina();
    }

    /**
     * Write the local User and any updates made to it back to the DB; this is called when we press
     * the back button to return to the Main Activity
     */
    protected void onDestroy() {
        user.writeToDatabase(fs, userDoc, initialUser);
        Log.d(TAG, "Wrote to DB");
        logoutTrigger = 0;
        super.onDestroy();
         // Checks that the loginStatus is indeed true, then if it is, start a new TravelActivity Class, and clear all the redundant activities in the stack.
        if (loginStatus) {
            Intent intent = new Intent(this.getApplicationContext(), TravelActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * Refreshes the TextViews that display the User attributes at the top of the page
     */
    private void refreshUserAttributes() {
        String woodRes = String.format(Locale.getDefault(), "%s", user.getWood());
        woodDisplay.setText(woodRes);
        String woodGearLevel = String.format(Locale.getDefault(), "%s", user.getWoodchoppingGearLevel());
        woodchoppingGearLevelDisplay.setText(woodGearLevel);
        String fishRes = String.format(Locale.getDefault(), "%s", user.getFish());
        fishDisplay.setText(fishRes);
        String fishGearLevel = String.format(Locale.getDefault(), "%s", user.getFishingGearLevel());
        fishingGearLevelDisplay.setText(fishGearLevel);
        String goldRes = String.format(Locale.getDefault(), "%s", user.getGold());
        goldDisplay.setText(goldRes);
        String combatGearLevel = String.format(Locale.getDefault(), "%s", user.getCombatGearLevel());
        combatGearLevelDisplay.setText(combatGearLevel);
        String aggLevel = String.format(Locale.getDefault(), "LEVEL %s", user.getAggregateLevel());
        aggLevelDisplay.setText(aggLevel);
        String aggLevelProgress = String.format(Locale.getDefault(),
                "%s / %s", user.getExp(), user.requiredExperience(user.getAggregateLevel() + 1));
        aggLevelProgressDisplay.setText(aggLevelProgress);
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
                gainDisplay.setText("");
            }
        });
    }

    /**
     * To be implemented by the concrete class; this differs depending on whether we want to chop
     * wood, fish for fish, or battle monsters
     */
    protected abstract void action();

    /**
     * Call the actio() method, which has the potential to update the TextViews; there is also the
     * check that the User has actually loaded in (since it is loaded in asynchronously)
     */
    private void actionAndRefresh() {
        if (userLoaded) {
            action();
            refreshUserAttributes();
        } else {
            Log.d(TAG, "User not yet loaded");
        }
    }

    /**
     * Begins the timer counting down
     */
    private void startTimer() {
        myTimer = new CountDownTimer(TOTAL_TIME_PER_SESSION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountdownText();
                updateStamina();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                startPauseResumeBtn.setVisibility(View.INVISIBLE);
                resetBtn.setVisibility(View.VISIBLE);
            }

        }.start();

        timerRunning = true;
        resetBtn.setVisibility(View.INVISIBLE);
        startPauseResumeBtn.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
    }

    /**
     * Pauses the timer
     */
    private void pauseTimer() {
        myTimer.cancel();
        timerRunning = false;
        resetBtn.setVisibility(View.VISIBLE);
        startPauseResumeBtn.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }

    /**
     * Resets the timer
     */
    private void resetTimer() {
        timeLeft = TOTAL_TIME_PER_SESSION;
        updateCountdownText();
        updateStamina();
        resetBtn.setVisibility(View.INVISIBLE);
        startPauseResumeBtn.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        startPauseResumeBtn.setVisibility(View.VISIBLE);
    }

    /**
     * Updates text for the timer
     */
    private void updateCountdownText() {
        // Conversion from milliseconds to minutes and seconds
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeDisplay.setText(timeLeftFormatted);
    }

    /**
     * Updates the stamina left which occurs every 5s, and for each unit, we want to conduct the
     * action
     */
    private void updateStamina() {
        int staminaLeft;
        int quotient = (int) (timeLeft / TIME_PER_STAMINA);
        if ((int) (timeLeft / 1000) % (TIME_PER_STAMINA / 1000) == 0) {
            staminaLeft = quotient;
            // For each unit of stamina consumed we want to chop wood
            if (staminaLeft < TOTAL_STAMINA) {
                actionAndRefresh();
            }
        } else {
            staminaLeft = quotient + 1;
        }

        String staminaLeftFormatted = String.format(Locale.getDefault(), "Stamina: %s / %s", staminaLeft, TOTAL_STAMINA);
        staminaDisplay.setText(staminaLeftFormatted);
    }

}