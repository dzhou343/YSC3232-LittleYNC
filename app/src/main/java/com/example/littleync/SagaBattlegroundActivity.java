package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SagaBattlegroundActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // To print to log instead of console
    private final static String TAG = "SagaBattleActivity";

    // DB attributes
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private DocumentReference userDoc;
    private User user;
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
    private TextView woodAndExpGainDisplay;

    // Timer attributes
    private static final long TIME_PER_STAMINA = 5000;
    private static final int TOTAL_STAMINA = 50;
    private int staminaLeft = TOTAL_STAMINA;
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
    private Spinner enemySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saga_battleground);

        // INITIALIZING SPINNER
        // This spinner refers to initializing the drop down menu for picking which enemy a user would like to fight
        Spinner enemySpinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> enemyAdapter = new ArrayAdapter<String>(
                SagaBattlegroundActivity.this,
                R.layout.battle_spinner,
                getResources().getStringArray(R.array.monster_names));
        // Creates the list of data
        enemyAdapter.setDropDownViewResource(R.layout.battle_spinner_dropdown);
        // Allows the spinner to show the data within the spinner.
        enemySpinner.setAdapter(enemyAdapter);

        enemySpinner.setOnItemSelectedListener(this);
        // END OF SPINNER INITIALIZATION

        staminaDisplay = (TextView) findViewById(R.id.stamina_section);
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

        healthDisplay = (TextView) findViewById(R.id.health_display);
        monsterDisplay = (ImageView) findViewById(R.id.monster_img);

        // All other text views
        woodDisplay = findViewById(R.id.wood_res);
        woodchoppingGearLevelDisplay = findViewById(R.id.wood_gear_level);
        fishDisplay = findViewById(R.id.fish_res);
        fishingGearLevelDisplay = findViewById(R.id.fish_gear_level);
        goldDisplay = findViewById(R.id.gold_res);
        combatGearLevelDisplay = findViewById(R.id.combat_gear_level);
        aggLevelDisplay = findViewById(R.id.agg_level);
        aggLevelProgressDisplay = findViewById(R.id.agg_level_progress);
        woodAndExpGainDisplay = findViewById(R.id.toast_msg);

        // TODO: Pass in the correct userID
        String userID = "random";
        userLoaded = false;
        userDoc = fs.collection("users").document(userID);
        readUser(userDoc.get());
    }

    /**
     * Write the local User and any updates made to it back to the DB
     * This is called when we press the back button to return to the Main Activity
     */
    @Override
    public void onDestroy() {
        user.writeToDatabase(userDoc);
        Log.d(TAG, "Wrote to DB");
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Set current Monster and HP, then update its textViews and image
        currentMonster = parent.getItemAtPosition(position).toString();
        currentHP = MONSTERS.getMonsterHitpoints(currentMonster);
        String healthMaxFormatted = String.format(Locale.getDefault(),
                "HP: %s / %s", currentHP, MONSTERS.getMonsterHitpoints(currentMonster));
        healthDisplay.setText(healthMaxFormatted);
        String toastMsg = String.format(Locale.getDefault(),
                "+%s Gold and +%s Exp", MONSTERS.getGoldYield(currentMonster), MONSTERS.getExpYield(currentMonster));
        woodAndExpGainDisplay.setText(toastMsg);
        changeMonsterImage();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Read in User by userID, update all the textViews at top of page
     *
     * @param ds
     */
    public void readUser(Task<DocumentSnapshot> ds) {
        ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
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
                                    }
                                }
        );
    }

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

    // TIMER STUFF
    private void startTimer(){
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

    private void pauseTimer(){
        myTimer.cancel();
        timerRunning = false;
        resetBtn.setVisibility(View.VISIBLE);
        startPauseResumeBtn.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }

    private void resetTimer(){
        timeLeft = TOTAL_TIME_PER_SESSION;
        updateCountdownText();
        updateStamina();
        resetBtn.setVisibility(View.INVISIBLE);
        startPauseResumeBtn.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        startPauseResumeBtn.setVisibility(View.VISIBLE);
    }

    private void updateCountdownText(){
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeDisplay.setText(timeLeftFormatted);
    }

    private void updateStamina(){
        int quotient = (int) (timeLeft / TIME_PER_STAMINA);
        if ((int) (timeLeft / 1000) % (TIME_PER_STAMINA / 1000) == 0) {
            staminaLeft = quotient;
            // For each unit of stamina consumed we want to attack the monster
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