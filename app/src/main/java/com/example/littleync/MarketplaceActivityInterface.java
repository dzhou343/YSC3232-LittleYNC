package com.example.littleync;

import android.os.Build;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.RequiresApi;

import com.example.littleync.model.Trade;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface MarketplaceActivityInterface {
    void onDestroy();

    void readTrades();

    void readUser(Task<DocumentSnapshot> ds);

    // err I don't think this does anything. delete?
    void tradePage(View view);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void postTrade(View view);

    // FOR TESTING ONLY, PLEASE IGNORE!
    void acceptTradeTest(View view);

    void acceptTrade(Trade toAccept);

    void onItemSelected(AdapterView<?> parent, View view, int position, long id);

    void onNothingSelected(AdapterView<?> parent);
}
