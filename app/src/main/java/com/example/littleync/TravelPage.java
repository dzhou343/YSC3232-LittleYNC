package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.littleync.model.OnlineDatabase;

public class TravelPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_page);
    }

    public void cendanaForestButton(View view) {
        Intent intent = new Intent(this, CendanaForestActivity.class);
        startActivity(intent);
        OnlineDatabase dbb = new OnlineDatabase();
        ///Read the DocumentReference
        System.out.println(dbb.userRead("VMkxZndQx7gh4jGI4GDE").get());
        ///TODO: Parse the Document...
    }
}