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

public class CendanaForestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        dbb.userRead("random", "users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot documentSnapshot) {
                                              User test = documentSnapshot.toObject(User.class);
                                              test.addTrade("another one shidoghaofg");
                                              test.writeToDatabase(dbb);
                                          }
                                      }
                );
        System.out.println();
        ///TODO: Parse the Document...

//        User userTest = new User("newname", 1, 1, 1, 3,
//                0, 0, 0, new ArrayList<String>(), 0);
//        userTest.addTrade("gold!");
//        userTest.addTrade("silver");
//        userTest.writeToDatabase(dbb);

        Map<String,String> toto = new HashMap<String,String>();
        toto.put("Yay","HAPPY DAY");
        toto.put("Hi Mark!","HAPPY DAY");

        //dbb.userWrite("hMYfocWEMPeheG4CD7Re","playground", toto);
        //dbb.userUpdate("hMYfocWEMPeheG4CD7Re","playground", "hello",toto);
        setContentView(R.layout.cendana_forest);
    }
}
