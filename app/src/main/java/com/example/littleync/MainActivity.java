package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Login button */
    public void loginButton(View view) {
        Intent intent = new Intent(this, TravelPage.class);
        startActivity(intent);

    }

    // Called when the user presses the sign-up text.
    public void onClick(View view) {

    }
}