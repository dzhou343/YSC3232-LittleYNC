package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.littleync.model.Monsters;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

/**
 * Saga Battleground Activity page where the user can idly battle monsters to gain gold resource
 */
public class SagaBattlegroundActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // To print to log instead of console
    private final static String TAG = "SagaBattleActivity";

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

    // Monster attributes
    private final Monsters MONSTERS = new Monsters();
    private String currentMonster;
    private int currentHP;
    private TextView healthDisplay;
    private ImageView monsterDisplay;

    /**
     * Initialize the objects and TextViews required for this page, including stamina computations
     * and Spinner required to select monsters to battle
     *
     * @param savedInstanceState pass info around
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battleground_page);

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

        // Initializing Spinner
        // This spinner refers to initializing the drop down menu for picking which enemy a user would like to fight
        Spinner enemySpinner = findViewById(R.id.spinner);
        ArrayAdapter<String> enemyAdapter = new ArrayAdapter<>(
                SagaBattlegroundActivity.this,
                R.layout.battleground_spinner,
                getResources().getStringArray(R.array.monster_names));
        // Creates the list of data
        enemyAdapter.setDropDownViewResource(R.layout.battleground_spinner_dropdown);
        // Allows the spinner to show the data within the spinner.
        enemySpinner.setAdapter(enemyAdapter);
        enemySpinner.setOnItemSelectedListener(this);
        healthDisplay = findViewById(R.id.health_display);
        monsterDisplay = findViewById(R.id.monster_img);

        // Timer stuff
        // By default, initialize stamina to full when the activity is created
        // Initialize 1) start/pause/resume button, 2) the reset button and 3) the dynamic time
        // display textview. By default, the reset button is initialized to invisible.
        staminaDisplay = findViewById(R.id.stamina_section);
        startPauseResumeBtn = findViewById(R.id.start_pause_resume_button);
        resetBtn = findViewById(R.id.reset_button);
        timeDisplay = findViewById(R.id.time_left);

        // set the onClickListeners for the two buttons
        startPauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                }
                else {
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
        user.writeToDatabase(userDoc, initialUser);
        Log.d(TAG, "Wrote to DB");
        super.onDestroy();
    }

    /**
     * Update current monster being fought, and relevant TextViews and image
     *
     * @param parent   spinner
     * @param view     for Android
     * @param position for Android
     * @param id       for Android
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentMonster = parent.getItemAtPosition(position).toString();
        currentHP = MONSTERS.getMonsterHitpoints(currentMonster);
        String healthMaxFormatted = String.format(Locale.getDefault(),
                "HP: %s / %s", currentHP, MONSTERS.getMonsterHitpoints(currentMonster));
        healthDisplay.setText(healthMaxFormatted);
        String toastMsg = String.format(Locale.getDefault(),
                "+%s Gold and +%s Exp", MONSTERS.getGoldYield(currentMonster), MONSTERS.getExpYield(currentMonster));
        gainDisplay.setText(toastMsg);
        changeMonsterImage();
    }

    /**
     * Default for spinner
     *
     * @param parent spinner
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

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
     * Deal damage to the current monster, which has the potential to kill the monster, updating
     * gold and exp, and has the potential to update the aggregateLevel, thus, we need to update
     * these TextViews; there is also the check that the User has actually loaded in (since it is
     * loaded in asynchronously)
     */
    public void fight() {
        if (userLoaded) {
            // Deal damage to monster
            currentHP -= user.getCombatGearLevel();
            if (currentHP <= 0) {
                // If monster is dead, then increment gold and exp
                user.addGold(MONSTERS.getGoldYield(currentMonster));
                user.addExp(MONSTERS.getExpYield(currentMonster));
                // Update textViews
                String goldRes = String.format(Locale.getDefault(), "Gold: %s", user.getGold());
                goldDisplay.setText(goldRes);
                String aggLevel = String.format(Locale.getDefault(), "Aggregate Level: %s", user.getAggregateLevel());
                aggLevelDisplay.setText(aggLevel);
                String aggLevelProgress = String.format(Locale.getDefault(),
                        "%s / %s", user.getExp(), user.requiredExperience(user.getAggregateLevel() + 1));
                aggLevelProgressDisplay.setText(aggLevelProgress);
                updateGainText();
                // Reset monster HP to fight again
                currentHP = MONSTERS.getMonsterHitpoints(currentMonster);
            }
            // Otherwise, always update the textView to reflect damage dealt
            String healthMaxFormatted = String.format(Locale.getDefault(),
                    "HP: %s / %s", currentHP, MONSTERS.getMonsterHitpoints(currentMonster));
            healthDisplay.setText(healthMaxFormatted);
        } else {
            Log.d(TAG, "User not yet loaded");
        }
    }

    /**
     * Change the monster image depending on the monster selected to battle
     */
    private void changeMonsterImage() {
        switch(currentMonster) {
            case "Prof. Bodin":
                monsterDisplay.setImageResource(R.drawable.battle_bruno);
                break;
            case "Prof. Wertz":
                monsterDisplay.setImageResource(R.drawable.battle_wertz);
                break;
            case "Prof. Cheung":
                monsterDisplay.setImageResource(R.drawable.battle_cheung);
                break;
            case "Prof. Comaroff":
                monsterDisplay.setImageResource(R.drawable.battle_comaroff);
                break;
            case "Prof. Danvy":
                monsterDisplay.setImageResource(R.drawable.battle_danvy);
                break;
            case "Prof. Field":
                monsterDisplay.setImageResource(R.drawable.battle_field);
                break;
            case "Prof. Hobor":
                monsterDisplay.setImageResource(R.drawable.battle_hobor);
                break;
            case "Prof. De Iorio":
                monsterDisplay.setImageResource(R.drawable.battle_iorio);
                break;
            case "Prof. Sergey":
                monsterDisplay.setImageResource(R.drawable.battle_sergey);
                break;
            case "Prof. Stamps":
                monsterDisplay.setImageResource(R.drawable.battle_stamps);
                break;
            case "Prof. Tolwinski":
                monsterDisplay.setImageResource(R.drawable.battle_tolwinski);
                break;
            case "Prof. Liu":
                monsterDisplay.setImageResource(R.drawable.battle_liu);
                break;
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
        int gain = MONSTERS.getGoldYield(currentMonster);
        int expGain = MONSTERS.getExpYield(currentMonster);

        String combatText = String.format(Locale.getDefault(), "+%s Gold", gain);
        String expText = String.format(Locale.getDefault(), "+%s Exp", expGain);
        SpannableStringBuilder combatSpan = new SpannableStringBuilder(combatText);
        SpannableStringBuilder expSpan = new SpannableStringBuilder(expText);

        int combatColor = Color.parseColor("#CCA533");
        int expColor = Color.parseColor("#FF9999");

        ForegroundColorSpan combatToColor = new ForegroundColorSpan(combatColor);
        ForegroundColorSpan expToColor = new ForegroundColorSpan(expColor);

        combatSpan.setSpan(combatToColor, 0, combatText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        expSpan.setSpan(expToColor, 0, expText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        combatSpan.append(" and ");
        combatSpan.append(expSpan);

        gainDisplay.setText(combatSpan);
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
                fight();
            }
        } else {
            staminaLeft = quotient + 1;
        }

        String staminaLeftFormatted = String.format(Locale.getDefault(), "Stamina: %s / %s", staminaLeft, TOTAL_STAMINA);
        staminaDisplay.setText(staminaLeftFormatted);
    }

}