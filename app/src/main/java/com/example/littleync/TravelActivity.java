package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.location.gms.location.FusedLocationProviderClient;

public class TravelActivity extends AppCompatActivity {

	private FusedLocationProviderClient fLC;
	
	@Override
	protected void onCreate() {
		fLC = LocationServices.getFusedLocationProviderClient(this);
		fLC.getLastLocation()
        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Checks for the case when there is no location, could be because the user didn't allow access for the user to the phone's location.
                if (location != null) {
                    Log.d("YOUR LAST KNOWN LOCATION IS AT:", location)
                }
            }
        });
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_page);
    }

    public void cendanaForestButton(View view) {
        Intent intent = new Intent(this, CendanaForestActivity.class);
        startActivity(intent);
    }

    public void armoryButton(View view) {
        Intent intent = new Intent(this, ArmoryActivity.class);
        startActivity(intent);
    }


    public void goToMarketplace(View view){
        Intent intent = new Intent(this, MarketplaceActivity.class);
        startActivity(intent);
    }

    public void battleButton(View view){
        Intent intent = new Intent(this, SagaBattlegroundActivity.class);
        startActivity(intent);
    }

    public void fishButton(View view){
        Intent intent = new Intent(this, EcopondActivity.class);
        startActivity(intent);
    }

}