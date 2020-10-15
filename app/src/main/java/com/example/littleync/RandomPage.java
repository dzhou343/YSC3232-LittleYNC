package com.example.littleync;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RandomPage extends AppCompatActivity{
    TextView interactive_textbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_page_with_interactive_textbox);
        interactive_textbox = (TextView) findViewById(R.id.text_view_4_random_page);
        // By default, stamina is 0 when the activity is created
        String text = interactive_textbox.getText().toString();
    }


    protected void changeText(String s){
        interactive_textbox.setText(s);
    }
}
