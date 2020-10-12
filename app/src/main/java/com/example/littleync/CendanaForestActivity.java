package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CendanaForestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cendana_forest);
        TextView stamina = (TextView) findViewById(R.id.stamina_section);
        // By default, stamina is 0 when the activity is created
        String staminaToDisplay = getString(R.string.Stamina, Integer.toString(0));
        stamina.setText(staminaToDisplay);
    }

    public void modifyStaminaTest(View v){
        // Need to get a reference to the text view to access its values
        TextView stamina = (TextView) findViewById(R.id.stamina_section);
        // Here we are dynamically getting the current text being displayed by the TextView
        String staminaBeingDisplayed = stamina.getText().toString();
        // Splitting up the string into parts
        String[] splitted = staminaBeingDisplayed.split(" ");
        // Getting the actual stamina value that we will change
        Integer currentValue = Integer.parseInt(splitted[1]);
        // Changing string value based on what it is
        if(currentValue == 0) currentValue = 50;
        else if(currentValue != 0) currentValue = currentValue - 1;
        // This is the new stamina that will be displayed after clicking the play button
        String newStaminaToDisplay = getString(R.string.Stamina, Integer.toString( currentValue) );
        // Changing value stored by textView
        stamina.setText(newStaminaToDisplay);
    }
}
