package com.example.littleync;

import android.view.View;

public interface TravelActivityInterface {

    void onStart();

    void onStop()/*
    @Override
    public void onDestroy() {
        if (logoutTrigger < 2) {
            logoutTrigger++;
        } else {
            this.finish();
            super.onDestroy();
        }
    }*/;

    void logoutButton(View view);

    void cendanaForestButton(View view);

    void armoryButton(View view);

    void goToMarketplace(View view);

    void battleButton(View view);

    void fishButton(View view);
}
