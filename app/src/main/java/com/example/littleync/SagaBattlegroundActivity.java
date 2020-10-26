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
import android.widget.Toast;

import com.example.littleync.model.Monsters;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SagaBattlegroundActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final Monsters monsters = new Monsters();

    // To print to log instead of console
    private final static String TAG = "SagaBattleActivity";


    // Timer attributes
    //    time (in milliseconds) taken to deplete one unit of stamina = 3s
    private static final long TIME_PER_STAMINA = 5000;
    private static final int TOTAL_STAMINA = 50;
    private int staminaLeft = TOTAL_STAMINA;
    private static final long TOTAL_TIME_PER_SESSION = TIME_PER_STAMINA * TOTAL_STAMINA;
    private boolean timerRunning;
    //    total time left in the session
    private long timeLeft = TOTAL_TIME_PER_SESSION;

    // TODO: idk what are these?
    private ImageButton startPauseResumeBtn;
    private ImageButton resetBtn;
    private CountDownTimer myTimer;
    private TextView timeDisplay;
    private TextView staminaDisplay;
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
                getResources().getStringArray(R.array.enemies));
        // Creates the list of data
        enemyAdapter.setDropDownViewResource(R.layout.battle_spinner_dropdown);
        // Allows the spinner to show the data within the spinner.
        enemySpinner.setAdapter(enemyAdapter);

        enemySpinner.setOnItemSelectedListener(this);
        // END OF SPINNER INITIALIZATION

        // By default, initialize stamina to full when the activity is created
        staminaDisplay = (TextView) findViewById(R.id.stamina_section);
//        String stamina_text = getString(R.string.Stamina, Integer.toString(0));
//        stamina_display.setText(stamina_text);
//        initialize the 1) start/pause/resume button, 2) the reset button and 3) the dynamic time
//        display textview. By default, the reset button is initialized to invisible.
        startPauseResumeBtn = findViewById(R.id.start_pause_resume_button);
        resetBtn = findViewById(R.id.reset_button);
        timeDisplay = findViewById(R.id.time_left);

        // set the onClickListeners for the two buttons
        startPauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();}
                else{
                    startTimer();}
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

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String currentChoice = parent.getItemAtPosition(position).toString();
        initializeHealthPoints(currentChoice);
        changeMonsterImage(currentChoice);
        // nothing happens if a user selects the monster they are currently fighting.

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // This function is used to display the full hp of a monster when it is chosen.
    private void initializeHealthPoints(String monsterName){
        Integer monsterHealthToDisplay = monsters.getMonsterHitpoints(monsterName);
        String monsterHealthString = monsterHealthToDisplay.toString();
        String healthMaxFormatted = String.format(Locale.getDefault(), "Health: 0 / %s", monsterHealthString);
        healthDisplay.setText(healthMaxFormatted);
    }

    private void changeMonsterImage(String monsterName){
        if (monsterName == "Prof. Bodin"){
            monsterDisplay.setImageResource(R.drawable.battle_bruno);
        } else if (monsterName == "Prof. Wertz"){
            monsterDisplay.setImageResource(R.drawable.battle_wertz);
        }
    }



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
//        conversion from milliseconds to minutes and seconds
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String time_left_formatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeDisplay.setText(time_left_formatted);
    }

    //    better update with an if condition, i.e. compute stamina now and only update when stamina_now is
    //    different from stamina_left (the previous stamina till now).
    private void updateStamina(){
        int quotient = (int) (timeLeft / TIME_PER_STAMINA);
//        System.out.println(String.format(Locale.getDefault(), "%d, %d", (int) (time_left / 1000) % 60, (int) (time_left / 1000) % (time_per_stamina / 1000)));
        if ((int) (timeLeft / 1000) % (TIME_PER_STAMINA / 1000) == 0) {
            staminaLeft = quotient;
            // For each unit of stamina consumed we want to chop wood

            // WE NEED TO CALL fight() function here.


        } else {
            staminaLeft = quotient + 1;
        }

        String stamina_left_formatted = String.format(Locale.getDefault(), "Stamina: %s / %s", staminaLeft, TOTAL_STAMINA);
        staminaDisplay.setText(stamina_left_formatted);
    }

}