package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.littleync.model.Monsters;
import com.example.littleync.model.OnlineDatabase;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.concurrent.locks.ReentrantLock;

public class CendanaForestActivity extends AppCompatActivity {
    private volatile Boolean flag = false;
    private OnlineDatabase db;
    private User user;
    private Monsters monsters = new Monsters();

    public synchronized Task<DocumentSnapshot> readTask() {
        return db.userReadWrite().get();
    }

    public synchronized void parseDS(Task<DocumentSnapshot> ds) {
        ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        user = documentSnapshot.toObject(User.class);
                                        for (int i = 0; i < 30; i++) {
                                            System.out.println(i);
                                        }
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
                user.addExp(monsters.getExpYield("Prof. Wertz"));
                user.addGold(monsters.getGoldYield("Prof. Bodin"));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cendana_forest);
        TextView stamina = (TextView) findViewById(R.id.stamina_section);
        // By default, stamina is 0 when the activity is created
        String staminaToDisplay = getString(R.string.Stamina, Integer.toString(0));
        stamina.setText(staminaToDisplay);

        db = new OnlineDatabase("random");
        flag = false;
        parseDS(readTask());
    }

    public void modifyStaminaTest(View v) {
        // Need to get a reference to the text view to access its values
        TextView stamina = (TextView) findViewById(R.id.stamina_section);
        // Here we are dynamically getting the current text being displayed by the TextView
        String staminaBeingDisplayed = stamina.getText().toString();
        // Splitting up the string into parts
        String[] splitted = staminaBeingDisplayed.split(" ");
        // Getting the actual stamina value that we will change
        Integer currentValue = Integer.parseInt(splitted[1]);
        // Changing string value based on what it is
        if (currentValue == 0) currentValue = 50;
        else if (currentValue != 0) currentValue = currentValue - 1;
        // This is the new stamina that will be displayed after clicking the play button
        String newStaminaToDisplay = getString(R.string.Stamina, Integer.toString(currentValue));
        // Changing value stored by textView
        stamina.setText(newStaminaToDisplay);

        // Ignore for now, testing buttons to write to DB
        fight();
    }

}
