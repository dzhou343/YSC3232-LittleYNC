package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.littleync.model.Monsters;
import com.example.littleync.model.OnlineDatabase;
import com.example.littleync.model.Shop;
import com.example.littleync.model.Trade;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.concurrent.locks.ReentrantLock;

public class CendanaForestActivity extends AppCompatActivity {
    private volatile Boolean userLoaded = false;
    private OnlineDatabase db;
    private User user;
    private final Monsters MONSTERS = new Monsters();
    private final Shop SHOP = new Shop();
    private final static String TAG = "CendanaForestActivity";

    public synchronized Task<DocumentSnapshot> readTask() {
        return db.userReadWrite().get();
    }

    public synchronized void parseDS(Task<DocumentSnapshot> ds) {
        ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        user = documentSnapshot.toObject(User.class);
                                        userLoaded = true;
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
        if (userLoaded) {
            for (int i = 0; i < 10; i++) {
                user.addExp(MONSTERS.getExpYield("Prof. Wertz"));
                user.addGold(MONSTERS.getGoldYield("Prof. Bodin"));

                //Log.d(TAG, String.valueOf(user.getExp()));

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
        if (userLoaded) {
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            user.addWood();
            System.out.println(user.getWood());
            System.out.println(user.getExp());
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        }
    }

    public synchronized void postTrade() {
        if (userLoaded) {
            System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            Trade t = new Trade("test1", "wood", "fish", 100, 200);
            t.writeToDatabase(db);
            System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
        }
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
        userLoaded = false;
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
        if (currentValue == 0) {
            currentValue = 50;
        } else {
            currentValue -= 1;
        }
        // This is the new stamina that will be displayed after clicking the play button
        String newStaminaToDisplay = getString(R.string.Stamina, Integer.toString(currentValue));
        // Changing value stored by textView
        stamina.setText(newStaminaToDisplay);

        // Ignore for now, testing buttons to write to DB
        postTrade();
    }

}
