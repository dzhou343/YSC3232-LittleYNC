package com.example.littleync;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class TravelActivity extends AppCompatActivity {

    private FusedLocationProviderClient fLC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_page);
        fLC = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fLC.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Checks for the case when there is no location, could be because the user didn't allow access for the user to the phone's location.
                        if (location != null) {
                            Log.d("YOUR LAST LOC", location.toString());
                        }
                    }
                });
    }

    public void cendanaForestButton(View view) {
        Intent intent = new Intent(this, CendanaForestActivity.class);
        startActivity(intent);
    }

    public void armoryButton(View view) {
        Intent intent = new Intent(this, ArmoryActivity.class);
        startActivity(intent);
    }


    public void goToMarketplace(View view) {
        Intent intent = new Intent(this, MarketplaceActivity.class);
        startActivity(intent);
    }

    public void battleButton(View view) {
        Intent intent = new Intent(this, SagaBattlegroundActivity.class);
        startActivity(intent);
    }

    public void fishButton(View view) {
        Intent intent = new Intent(this, EcopondActivity.class);
        startActivity(intent);
    }

}