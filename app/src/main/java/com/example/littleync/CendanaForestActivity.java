package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.littleync.model.OnlineDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class CendanaForestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnlineDatabase dbb = new OnlineDatabase();
        ///Read the DocumentReference
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
        dbb.userRead("hMYfocWEMPeheG4CD7Re","playground").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                             @Override
                                                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                 if (task.isSuccessful()) {
                                                                                     Map n = task.getResult().getData();
                                                                                     System.out.println(n.keySet());

                                                                                 }
                                                                             }

                                                                         }
        );
        System.out.println();
        ///TODO: Parse the Document...

        Map<String,String> toto = new HashMap<String,String>();
        toto.put("Yay","HAPPY DAY");
        toto.put("Hi Mark!","HAPPY DAY");

        dbb.userWrite("hMYfocWEMPeheG4CD7Re","playground").update("test",toto);
        dbb.userWrite("hMYfocWEMPeheG4CD7Re","playground").update("test",toto);
        setContentView(R.layout.cendana_forest);
    }
}
