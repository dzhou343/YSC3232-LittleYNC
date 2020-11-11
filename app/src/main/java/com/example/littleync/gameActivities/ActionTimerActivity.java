package com.example.littleync.gameActivities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.littleync.R;

import java.util.Locale;

/**
 * Abstract class we implement for woodchopping, fishing, and combat. All three of these activites
 * are extremely similar, with the main difference being the action that gets performed
 */
public abstract class ActionTimerActivity extends AttributesActivity {

    // Gain display to reflect update of User stats
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
     * To be implemented by the concrete class; this differs depending on whether we want to chop
     * wood, fish for fish, or battle monsters
     */
    protected abstract void action();

    /**
     * Constructor for this abstract class which just sets the Log print for us to debug
     *
     * @param TAG name of the class which implements this
     */
    public ActionTimerActivity(String TAG) {
        super(TAG);
    }

    /**
     * Create the timer for the action activities
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gain display
        gainDisplay = findViewById(R.id.gain_display);

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
     * Call the action() method, which has the potential to update the TextViews; there is also the
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

            /**
            *  Method that handles the case when the timer has reached 00:00 or stamina has reached 0/50.
            * */
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