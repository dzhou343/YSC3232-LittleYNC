package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
 * Ecopond Activity page where the user can idly fish for fish to gain fish resource
 */
public class EcopondActivity extends AppCompatActivity {
    // To print to log instead of console
    private final static String TAG = "EcopondActivity";


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
    private TextView gainDisplay;

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
     * Initialize the objects and TextViews required for this page, including stamina computations
     *
     * @param savedInstanceState pass info around
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecopond_page);

        // User and relevant TextViews
        woodDisplay = findViewById(R.id.wood_res);
        woodchoppingGearLevelDisplay = findViewById(R.id.wood_gear_level);
        fishDisplay = findViewById(R.id.fish_res);
        fishingGearLevelDisplay = findViewById(R.id.fish_gear_level);
        goldDisplay = findViewById(R.id.gold_res);
        combatGearLevelDisplay = findViewById(R.id.combat_gear_level);
        aggLevelDisplay = findViewById(R.id.agg_level);
        aggLevelProgressDisplay = findViewById(R.id.agg_level_progress);
        gainDisplay = findViewById(R.id.toast_msg);

        String userID = FirebaseAuth.getInstance().getUid();
        userLoaded = false;
        assert userID != null;
        userDoc = fs.collection("users").document(userID);
        readUser(userDoc.get());

        // Timer stuff
        // By default, initialize stamina to full when the activity is created
        // Initialize 1) start/pause/resume button, 2) the reset button and 3) the dynamic time
        // display textview. By default, the reset button is initialized to invisible.
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
    @Override
    public void onDestroy() {
        user.writeToDatabase(fs, userDoc, initialUser);
        Log.d(TAG, "Wrote to DB");
        logoutTrigger = 0;
        super.onDestroy();
        if (loginStatus) {
            Intent intent = new Intent(this.getApplicationContext(), TravelActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

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
                                        // Assign User attributes to textViews
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
                                        gainDisplay.setText("");
                                    }
                                }
        );
    }

    /**
     * Call the user.fishFish() method, which updates fish and exp, and has the potential to update
     * the aggregateLevel, thus, we need to update these TextViews; there is also the check that
     * the User has actually loaded in (since it is loaded in asynchronously)
     */
    public void fishFish() {
        if (userLoaded) {
            user.fishFish();
            String fishRes = String.format(Locale.getDefault(), "Fish: %s", user.getFish());
            fishDisplay.setText(fishRes);
            String aggLevel = String.format(Locale.getDefault(), "Aggregate Level: %s", user.getAggregateLevel());
            aggLevelDisplay.setText(aggLevel);
            String aggLevelProgress = String.format(Locale.getDefault(),
                    "%s / %s", user.getExp(), user.requiredExperience(user.getAggregateLevel() + 1));
            aggLevelProgressDisplay.setText(aggLevelProgress);
        } else {
            Log.d(TAG, "User not yet loaded");
        }
    }

    /**
     * Begin the timer counting down
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
     * Pause the timer
     */
    private void pauseTimer() {
        myTimer.cancel();
        timerRunning = false;
        resetBtn.setVisibility(View.VISIBLE);
        startPauseResumeBtn.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }

    /**
     * Reset the timer
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
     * Update text for the timer
     */
    private void updateCountdownText() {
        // Conversion from milliseconds to minutes and seconds
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeDisplay.setText(timeLeftFormatted);
    }

    private void updateGainText(){
        int gain = user.getFishingGearLevel();

        String fishText = String.format(Locale.getDefault(), "+%s Fish", gain);
        String expText = String.format(Locale.getDefault(), "+%s Exp", gain);

        SpannableStringBuilder fishSpan = new SpannableStringBuilder(fishText);
        SpannableStringBuilder expSpan = new SpannableStringBuilder(expText);

        int fishColor = Color.parseColor("#8CEAFF");
        int expColor = Color.parseColor("#FF9999");

        ForegroundColorSpan fishToColor = new ForegroundColorSpan(fishColor);
        ForegroundColorSpan expToColor = new ForegroundColorSpan(expColor);

        fishSpan.setSpan(fishToColor, 0, fishText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        expSpan.setSpan(expToColor, 0, expText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        fishSpan.append(" and ");
        fishSpan.append(expSpan);

        gainDisplay.setText(fishSpan);
    }

    /**
     * Update the stamina left which occurs every 5s, and for each unit, we want to fish fish
     */
    private void updateStamina() {
        int staminaLeft;
        int quotient = (int) (timeLeft / TIME_PER_STAMINA);
        if ((int) (timeLeft / 1000) % (TIME_PER_STAMINA / 1000) == 0) {
            staminaLeft = quotient;
            // For each unit of stamina consumed we want to fish fish
            if (staminaLeft < TOTAL_STAMINA) {
                fishFish();
                updateGainText();
            }
        } else {
            staminaLeft = quotient + 1;
        }

        String staminaLeftFormatted = String.format(Locale.getDefault(), "Stamina: %s / %s", staminaLeft, TOTAL_STAMINA);
        staminaDisplay.setText(staminaLeftFormatted);
    }

}