package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.littleync.model.Monsters;
import com.example.littleync.model.OnlineDatabase;
import com.example.littleync.model.Shop;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public class CendanaForestActivity extends AppCompatActivity {
    private volatile Boolean flag = false;
    private OnlineDatabase db;
    private User user;
    private final Monsters MONSTERS = new Monsters();
    private final Shop SHOP = new Shop();

    //    time (in milliseconds) taken to deplete one unit of stamina = 3s
    private static final long TIME_PER_STAMINA = 5000;
    private static final int TOTAL_STAMINA = 50;
    private int staminaLeft = TOTAL_STAMINA;
    private static final long TOTAL_TIME_PER_SESSION = TIME_PER_STAMINA * TOTAL_STAMINA;
    private boolean timerRunning;
    //    total time left in the session
    private long timeLeft = TOTAL_TIME_PER_SESSION;

    private ImageButton startPauseResumeBtn;
    private ImageButton resetBtn;
    private CountDownTimer myTimer;
    private TextView timeDisplay;
    private TextView staminaDisplay;

    public synchronized Task<DocumentSnapshot> readTask() {
        return db.userReadWrite().get();
    }

    public synchronized void parseDS(Task<DocumentSnapshot> ds) {
        ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        user = documentSnapshot.toObject(User.class);
                                        flag = true;
                                    }
                                }
        );
//        User userTest = new User("sighs", 1, 1, 1, 3,
//                0, 0, 0, new ArrayList<String>(), 500000);
//        userTest.addTrade("gold!");
//        userTest.addTrade("silver");
//        userTest.writeToDatabase(dbb);
    }

    public synchronized void fight() {
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
        if (flag) {
            for (int i = 0; i < 10; i++) {
                user.addExp(MONSTERS.getExpYield("Prof. Wertz"));
                user.addGold(MONSTERS.getGoldYield("Prof. Bodin"));
                System.out.println(user.getExp());
                System.out.println(user.getGold());
                System.out.println(user.getAggregateLevel());
            }
            user.writeToDatabase(db);
        } else {
            System.out.println("SHOULD NEVER REACH HERE");
        }
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
    }

    public synchronized void chopWood() {
        if (flag) {
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            user.addWood();
            System.out.println(user.getWood());
            System.out.println(user.getExp());
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cendana_forest);

        // By default, initialize stamina to full when the activity is created
        staminaDisplay = (TextView) findViewById(R.id.stamina_section);
//        String stamina_text = getString(R.string.Stamina, Integer.toString(0));
//        stamina_display.setText(stamina_text);
//        initialize the 1) start/pause/resume button, 2) the reset button and 3) the dynamic time
//        display textview. By default, the reset button is initialized to invisible.
        startPauseResumeBtn = findViewById(R.id.start_pause_resume_button);
        resetBtn = findViewById(R.id.reset_button);
        timeDisplay = findViewById(R.id.time_left);

//        set the onClickListeners for the two buttons
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

        db = new OnlineDatabase("random");
        flag = false;
        parseDS(readTask());
    }


//    FROM HERE: TIMER STUFF
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
        } else {
            staminaLeft = quotient + 1;
        }

        String stamina_left_formatted = String.format(Locale.getDefault(), "Stamina: %s / %s", staminaLeft, TOTAL_STAMINA);
        staminaDisplay.setText(stamina_left_formatted);
        // Ignore for now, testing buttons to write to DB
        chopWood();
    }

}

