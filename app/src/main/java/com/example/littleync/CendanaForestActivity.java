package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.littleync.model.OnlineDatabase;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class CendanaForestActivity extends AppCompatActivity {
    volatile String yay = "notUpdated";
    volatile Boolean flag = false;
    Task<DocumentSnapshot> ds;
    ReentrantLock lock = new ReentrantLock();

    public Task<DocumentSnapshot> readTask() {
        final OnlineDatabase dbb = new OnlineDatabase();
        ///Read the DocumentReference
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
//        dbb.userRead("random","users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot doc = task.getResult();
//                            //Map n = task.getResult().getData();
//                            //System.out.println(n.keySet());
//                        } else {
//                            System.out.println("ERRORERRORERRORERROR");
//                        }
//                    }
//
//                }
//        );


        ds = dbb.userRead("random", "users")
                .get();
        return ds;
    }


    public void parseDS(Task<DocumentSnapshot> dss) {
        lock.lock();
        try {
            dss.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                         @Override
                                         public void onSuccess(DocumentSnapshot documentSnapshot) {
                                             User test = documentSnapshot.toObject(User.class);
                                             yay = "sighs";
                                             for (int i = 0; i < 1000; i++) {
                                                 System.out.println(i);
                                             }
                                             flag = true;

                                             System.out.println("halp");

                                             test.addTrade("another onebleh");
                                             test.writeToDatabase(new OnlineDatabase());
                                             System.out.println("halpp");
                                             pir();
                                         }
                                     }
            );


            //System.out.println(m);

            System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");


            System.out.println();
            ///TODO: Parse the Document...

//        User userTest = new User("sighs", 1, 1, 1, 3,
//                0, 0, 0, new ArrayList<String>(), 500000);
//        userTest.addTrade("gold!");
//        userTest.addTrade("silver");
//        userTest.writeToDatabase(dbb);

            Map<String, String> toto = new HashMap<String, String>();
            toto.put("Yay", "HAPPY DAY");
            toto.put("Hi Mark!", "HAPPY DAY");
        }
        finally {
            lock.unlock();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dbb.userWrite("hMYfocWEMPeheG4CD7Re","playground", toto);
        //dbb.userUpdate("hMYfocWEMPeheG4CD7Re","playground", "hello",toto);
        yay = "notUpdated";
        flag = false;
        setContentView(R.layout.cendana_forest);
        parseDS(readTask());
        pir();
    }

    public void pir() {
        lock.lock();
        try {
            if (flag) {
                System.out.println(yay);
            } else {
                System.out.println("other branch");
            }
        } finally {
            lock.unlock();
        }
    }


}
