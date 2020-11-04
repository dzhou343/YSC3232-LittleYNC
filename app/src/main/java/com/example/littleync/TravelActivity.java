package com.example.littleync;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.google.firebase.auth.*;
import static com.example.littleync.MainActivity.fLC;
import static com.example.littleync.MainActivity.lCB;
import static com.example.littleync.MainActivity.loginStatus;
import static com.example.littleync.MainActivity.logoutTrigger;
import static com.example.littleync.MainActivity.userInstance;
import static com.example.littleync.MainActivity.whereAmINowMap;
import static com.example.littleync.MainActivity.whereWasIMap;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class TravelActivity extends AppCompatActivity {

    private Button cTree;
    private Button armory;
    private Button trading;
    private Button ecopond;
    private Button battleground;

    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hides the back button on travel page.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setSubtitle(String.format("Welcome %s",userInstance.getCurrentUser().getDisplayName()));
        setContentView(R.layout.travel_page);
        //Disable the home button on the travel page.
        cTree = (Button) findViewById(R.id.cendana_forest_travel);
        armory = (Button) findViewById(R.id.armory_travel_page);
        trading = (Button) findViewById(R.id.buttery_trading_travel_page);
        ecopond = (Button) findViewById(R.id.ecopond_travel_page);
        battleground = (Button) findViewById(R.id.saga_battleground_travel_page);
    }
    /**
     * Check for the activated location of the user based on whereAmINowMap to set alpha of button transparent.
     */

    @Override
    public void onStart() {
        super.onStart();/*
        cTree.setEnabled(whereAmINowMap.get("Cendana"));
        trading.setEnabled(whereAmINowMap.get("Elm"));
        battleground.setEnabled(whereAmINowMap.get("Saga"));
        ecopond.setEnabled(whereAmINowMap.get("Ecopond"));
        armory.setEnabled(whereAmINowMap.get("Armory"));*/

        cTree.setEnabled(true);
        trading.setEnabled(true);
        battleground.setEnabled(true);
        ecopond.setEnabled(true);
        armory.setEnabled(true);

        if (whereAmINowMap.get("Cendana") == true) {
            cTree.setAlpha(0);
        } else if (whereAmINowMap.get("Elm") == true) {
            trading.setAlpha(0);
        } else if (whereAmINowMap.get("Saga") == true) {
            battleground.setAlpha(0);
        } else if (whereAmINowMap.get("Ecopond") == true) {
            ecopond.setAlpha(0);
        } else if (whereAmINowMap.get("Armory") == true) {
            armory.setAlpha(0);
        }



    }

    /**
     * Calls onStop whenever a new page is generated.
     */

    @Override
    public void onStop() {
        super.onStop();
        if (logoutTrigger < 2) {
            logoutTrigger++;
        } else {
            TravelActivity.super.finish();
        }

    }/*
    @Override
    public void onDestroy() {
        if (logoutTrigger < 2) {
            logoutTrigger++;
        } else {
            this.finish();
            super.onDestroy();
        }
    }*/

    public void logoutButton(View view) {
        try{
            loginStatus = false;
            logoutTrigger = 0;
            whereWasIMap.put("initialized", false);
            fLC.removeLocationUpdates(lCB);
            userInstance.signOut();
            TravelActivity.super.finish();
            }
        catch (Exception e) {
            //Intent in = new Intent(this, TravelActivity.class);
            //startActivity(in);
        }

    }
    public void cendanaForestButton(View view) {
        Intent intent = new Intent(this, CendanaForestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void armoryButton(View view) {
        Intent intent = new Intent(this, ArmoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void goToMarketplace(View view) {
        Intent intent = new Intent(this, MarketplaceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void battleButton(View view) {
        Intent intent = new Intent(this, SagaBattlegroundActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void fishButton(View view) {
        Intent intent = new Intent(this, EcopondActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}