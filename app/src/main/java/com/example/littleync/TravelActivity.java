package com.example.littleync;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.littleync.gameActivities.ArmoryActivity;
import com.example.littleync.gameActivities.CendanaForestTimerActivity;
import com.example.littleync.gameActivities.EcopondTimerActivity;
import com.example.littleync.gameActivities.MarketplaceActivity;
import com.example.littleync.gameActivities.SagaBattlegroundTimerActivity;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.littleync.LoginActivity.fLC;
import static com.example.littleync.LoginActivity.lCB;
import static com.example.littleync.LoginActivity.loginStatus;
import static com.example.littleync.LoginActivity.logoutTrigger;
import static com.example.littleync.LoginActivity.userInstance;
import static com.example.littleync.LoginActivity.whereAmINowMap;
import static com.example.littleync.LoginActivity.whereWasIMap;


public class TravelActivity extends AppCompatActivity {

    private Button cTree;
    private Button armory;
    private Button trading;
    private Button ecopond;
    private Button battleground;
    /**
     * Returns the main action bar at the top of the screen, if there exists one, which allows us to edit what is shown under the title of the top bar.
     *
     * @return ActionBar
     * @see <a href="https://developer.android.com/training/appbar/setting-up">https://developer.android.com/training/appbar/setting-up</a>
     */
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
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setSubtitle(String.format("Welcome %s",userInstance.getCurrentUser().getDisplayName()));
        }catch (Exception e) {
            Log.d("TRAVEL ACTIVITY",e.getMessage());
        }

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
        super.onStart();

        //UNMUTE THIS FOR DEPLOYMENT ON ANDROID PHONE
        /*
        cTree.setEnabled(whereAmINowMap.get("Cendana"));
        trading.setEnabled(whereAmINowMap.get("Elm"));
        battleground.setEnabled(whereAmINowMap.get("Saga"));
        ecopond.setEnabled(whereAmINowMap.get("Ecopond"));
        armory.setEnabled(whereAmINowMap.get("Armory"));*/

        //MUTE THIS FOR DEVELOPMENT ON WINDOWS MACHINES
        cTree.setEnabled(true);
        trading.setEnabled(true);
        battleground.setEnabled(true);
        ecopond.setEnabled(true);
        armory.setEnabled(true);

        if (whereAmINowMap.get("Cendana")) {
            cTree.setAlpha(0);
        } else if (whereAmINowMap.get("Elm")) {
            trading.setAlpha(0);
        } else if (whereAmINowMap.get("Saga")) {
            battleground.setAlpha(0);
        } else if (whereAmINowMap.get("Ecopond")) {
            ecopond.setAlpha(0);
        } else if (whereAmINowMap.get("Armory")) {
            armory.setAlpha(0);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        logoutTrigger --;
    }

    /**
     * Calls onStop whenever a new page is generated.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (logoutTrigger < 1) {
            logoutTrigger++;
         } else {
            TravelActivity.super.finish();
        }

    }

    /**
     * Called when the logout button is pressed.
     * loginStatus will be set to false
     * logoutTrigger will be set to 0
     * The fusedLocationProvider will stop the location updates from GPS
     * userInstance will be signed out.
     * @param view
     *
     * @see LoginActivity
     */
    public void logoutButton(View view) {
        try{
            loginStatus = false;
            logoutTrigger = 0;
            whereWasIMap.put("initialized", false);
            fLC.removeLocationUpdates(lCB);
            userInstance.signOut();
            FirebaseAuth.getInstance().signOut();
            TravelActivity.super.finish();
            }
        catch (Exception e) {
            Log.d("TRAVEL ACTIVITY", "Logout failed");
        }

    }

    /**
     *Buttton that navigate players to Cendana Forest. A new activity is spawned and the travel page is set to Pause.
      */
    public void cendanaForestButton(View view) {
        Intent intent = new Intent(this, CendanaForestTimerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    /**
     *Buttton that navigate players to Armory. A new activity is spawned and the travel page is set to Pause.
     */
    public void armoryButton(View view) {
        Intent intent = new Intent(this, ArmoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     *Buttton that navigate players to Marketplace. A new activity is spawned and the travel page is set to Pause.
     */
    public void goToMarketplace(View view) {
        Intent intent = new Intent(this, MarketplaceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    /**
     *Buttton that navigate players to Saga battleground. A new activity is spawned and the travel page is set to Pause.
     */
    public void battleButton(View view) {
        Intent intent = new Intent(this, SagaBattlegroundTimerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     *Buttton that navigate players to ecopond. A new activity is spawned and the travel page is set to Pause.
     */
    public void fishButton(View view) {
        Intent intent = new Intent(this, EcopondTimerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}