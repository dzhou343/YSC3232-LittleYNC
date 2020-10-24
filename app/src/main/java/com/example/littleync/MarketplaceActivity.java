package com.example.littleync;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MarketplaceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page);

        Spinner marketplace1Spinner = (Spinner) findViewById(R.id.marketplace1_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MarketplaceActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.marketplace1_spinner));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marketplace1Spinner.setAdapter(adapter1);
    }
}
