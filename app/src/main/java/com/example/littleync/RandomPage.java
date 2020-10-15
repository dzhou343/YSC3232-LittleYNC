package com.example.littleync;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RandomPage extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_page_with_interactive_textbox);
        TextView interactive_textbox = (TextView) findViewById(R.id.text_view_4_random_page);
        // By default, stamina is 0 when the activity is created
        String text = interactive_textbox.getText().toString();
    }
}
