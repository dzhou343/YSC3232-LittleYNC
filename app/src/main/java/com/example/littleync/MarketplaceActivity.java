package com.example.littleync;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.littleync.R;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MarketplaceActivity extends AppCompatActivity {

    private Button trade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttery_trading_hub);
        /**
         * Instantiate from layout's view id
         */
        final String username;
        final Button tradeXML = findViewById(R.id.tradeButton);
        this.trade = tradeXML;
    }

    private String logMsg = "Buttery screen";

    public void tradePage(View view) {
        Log.d(logMsg, logMsg);
        FirebaseAuth fb = FirebaseAuth.getInstance();
        Log.d(logMsg, fb.getCurrentUser().getUid().toString());
    }
}
