package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SagaBattlegroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saga_battleground);

        Spinner enemySelector = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> enemyAdapter = new ArrayAdapter<String>(
                SagaBattlegroundActivity.this,
                R.layout.battle_spinner,
                getResources().getStringArray(R.array.enemies));
        // Creates the list of data
        enemyAdapter.setDropDownViewResource(R.layout.battle_spinner_dropdown);
        // Allows the spinner to show the data within the spinner.
        enemySelector.setAdapter(enemyAdapter);
    }


}