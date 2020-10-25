package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TravelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_page);
    }

    public void cendanaForestButton(View view) {
        Intent intent = new Intent(this, CendanaForestActivity.class);
        startActivity(intent);
    }

    public void armoryButton(View view) {
        Intent intent = new Intent(this, ArmoryActivity.class);
        startActivity(intent);
    }

    public void battleButton(View view){
        Intent intent = new Intent(this, SagaBattlegroundActivity.class);
        startActivity(intent);
    }
}